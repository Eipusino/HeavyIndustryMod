package heavyindustry.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.graphics.gl.*;
import heavyindustry.type.*;
import mindustry.*;
import mindustry.type.*;

import static heavyindustry.HVars.*;
import static mindustry.Vars.*;

/**
 * Defines the {@linkplain Shader shader}s this mod offers.
 *
 * @author Eipusino
 */
public final class Shadersf {
	public static DepthShader depth;
	public static DepthScreenspaceShader depthScreenspace;
	public static DepthAtmosphereShader depthAtmosphere;
	public static AlphaShader alphaShader;
	public static SurfaceShaderf brine, nanoFluid, boundWater, pit, waterPit;
	public static ChromaticAberrationShader chromatic;
	public static MaskShader alphaMask;
	public static WaveShader wave;
	public static MirrorFieldShader mirrorField;
	public static MaterializeShader materialize;
	public static VerticalBuildShader vertBuild;
	public static BlockBuildCenterShader blockBuildCenter;
	public static TractorConeShader tractorCone;
	public static DimShader dimShader;
	public static SmallSpaceShader smallSpaceShader;
	public static Shader baseShader, passThrough;
	public static TilerShader tiler;
	public static PlanetTextureShader planetTexture;

	/** Don't let anyone instantiate this class. */
	private Shadersf() {}

	/** Loads the shaders. */
	public static void init() {
		String prevVert = Shader.prependVertexCode, prevFrag = Shader.prependFragmentCode;
		Shader.prependVertexCode = Shader.prependFragmentCode = "";

		depth = new DepthShader();
		depthScreenspace = new DepthScreenspaceShader();
		depthAtmosphere = new DepthAtmosphereShader();

		alphaShader = new AlphaShader();

		brine = new SurfaceShaderf("brine");
		nanoFluid = new SurfaceShaderf("nano-fluid");
		boundWater = new SurfaceShaderf("bound-water");
		pit = new PitShader("pit", name("concrete-blank1"), name("stone-sheet"), name("truss"));
		waterPit = new PitShader("water-pit", name("concrete-blank1"), name("stone-sheet"), name("truss"));

		chromatic = new ChromaticAberrationShader();

		alphaMask = new MaskShader();
		mirrorField = new MirrorFieldShader();
		wave = new WaveShader();

		materialize = new MaterializeShader();
		vertBuild = new VerticalBuildShader();
		blockBuildCenter = new BlockBuildCenterShader();
		tractorCone = new TractorConeShader();
		dimShader = new DimShader();
		smallSpaceShader = new SmallSpaceShader("small-space");

		baseShader = new Shader(dsv("screenspace"), msf("dist-base"));
		passThrough = new Shader(dsv("screenspace"), msf("pass-through"));

		tiler = new TilerShader();

		planetTexture = new PlanetTextureShader();

		Shader.prependVertexCode = prevVert;
		Shader.prependFragmentCode = prevFrag;
	}

	public static void dispose() {
		brine.dispose();
		nanoFluid.dispose();
		boundWater.dispose();
	}

	/**
	 * Resolves shader files from this mod via {@link Vars#tree}.
	 *
	 * @param name The shader file name, e.g. {@code my-shader.frag}.
	 * @return The shader file, located inside {@code shaders/}.
	 */
	public static Fi dsf(String name) {
		return Core.files.internal("shaders/" + name + ".frag");
	}

	public static Fi dsv(String name) {
		return Core.files.internal("shaders/" + name + ".vert");
	}

	public static Fi msf(String name) {
		return internalTree.child("shaders/" + name + ".frag");
	}

	public static Fi msv(String name) {
		return internalTree.child("shaders/" + name + ".vert");
	}

	/** Specialized mesh shader to capture fragment depths. */
	public static class DepthShader extends Shader {
		public Camera3D camera;

		/** The only instance of this class: {@link #depth}. */
		DepthShader() {
			super(msv("depth"), msf("depth"));
		}

		@Override
		public void apply() {
			setUniformf("u_camPos", camera.position);
			setUniformf("u_camRange", camera.near, camera.far - camera.near);
		}
	}

	public static class DepthScreenspaceShader extends Shader {
		public DepthFrameBuffer buffer;

		/** The only instance of this class: {@link #depthScreenspace}. */
		DepthScreenspaceShader() {
			super(msv("depth-screenspace"), msf("depth-screenspace"));
		}

		@Override
		public void apply() {
			buffer.getTexture().bind(1);
			buffer.getDepthTexture().bind(0);

			setUniformi("u_color", 1);
			setUniformi("u_depth", 0);
		}
	}

	/**
	 * An atmosphere shader that incorporates the planet shape in a form of depth texture. Better quality, but at the little
	 * cost of performance.
	 */
	public static class DepthAtmosphereShader extends Shader {
		private static final Mat3D mat = new Mat3D();

		public Camera3D camera;
		public AtmospherePlanet planet;

		/** The only instance of this class: {@link #depthAtmosphere}. */
		DepthAtmosphereShader() {
			super(msv("depth-atmosphere"), msf("depth-atmosphere"));
		}

		@Override
		public void apply() {
			setUniformMatrix4("u_proj", camera.combined.val);
			setUniformMatrix4("u_trans", planet.getTransform(mat).val);

			setUniformf("u_camPos", camera.position);
			setUniformf("u_relCamPos", Tmp.v31.set(camera.position).sub(planet.position));
			setUniformf("u_camRange", camera.near, camera.far - camera.near);
			setUniformf("u_center", planet.position);
			setUniformf("u_light", planet.getLightNormal());
			setUniformf("u_color", planet.atmosphereColor.r, planet.atmosphereColor.g, planet.atmosphereColor.b);

			setUniformf("u_innerRadius", planet.radius + planet.atmosphereRadIn);
			setUniformf("u_outerRadius", planet.radius + planet.atmosphereRadOut);

			planet.buffer.getTexture().bind(0);
			setUniformi("u_topology", 0);
			setUniformf("u_viewport", Core.graphics.getWidth(), Core.graphics.getHeight());
		}
	}

	public static class PlanetTextureShader extends Shader {
		public Vec3 lightDir = new Vec3(1, 1, 1).nor();
		public Color ambientColor = Color.white.cpy();
		public Vec3 camDir = new Vec3();
		public float alpha = 1f;
		public Planet planet;

		/** The only instance of this class: {@link #planetTexture}. */
		PlanetTextureShader() {
			super(msv("circle-mesh"), msf("circle-mesh"));
		}

		@Override
		public void apply() {
			camDir.set(renderer.planets.cam.direction).rotate(Vec3.Y, planet.getRotation());

			setUniformf("u_alpha", alpha);
			setUniformf("u_lightdir", lightDir);
			setUniformf("u_ambientColor", ambientColor.r, ambientColor.g, ambientColor.b);
			setPlanetInfo("u_sun_info", planet.solarSystem);
			setPlanetInfo("u_planet_info", planet);
			setUniformf("u_camdir", camDir);
			setUniformf("u_campos", renderer.planets.cam.position);
		}

		private void setPlanetInfo(String name, Planet planet) {
			Vec3 position = planet.position;
			Shader shader = this;
			shader.setUniformf(name, position.x, position.y, position.z, planet.radius);
		}
	}

	public static class TilerShader extends Shader {
		public Texture texture = Core.atlas.white().texture;
		public float scl = 4f;

		/** The only instance of this class: {@link #tiler}. */
		TilerShader() {
			super(dsv("screenspace"), msf("tiler"));
		}

		@Override
		public void apply() {
			setUniformf("u_offset", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_texsize", Core.camera.width, Core.camera.height);
			setUniformf("u_tiletexsize", texture.width / scl, texture.height / scl);

			texture.bind(1);
			renderer.effectBuffer.getTexture().bind(0);

			setUniformi("u_tiletex", 1);
		}
	}

	public static class AlphaShader extends Shader {
		public float alpha = 1f;

		/** The only instance of this class: {@link #alphaShader}. */
		AlphaShader() {
			super(dsv("screenspace"), msf("post-alpha"));
		}

		@Override
		public void apply() {
			setUniformf("u_alpha", alpha);
		}
	}

	public static class WaveShader extends Shader {
		public Color waveMix = Color.white;
		public float mixAlpha = 0.4f;
		public float mixOmiga = 0.75f;
		public float maxThreshold = 0.9f;
		public float minThreshold = 0.6f;
		public float waveScl = 0.2f;

		/** The only instance of this class: {@link #wave}. */
		WaveShader() {
			super(dsv("screenspace"), msf("wave"));
		}

		@Override
		public void apply() {
			setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_resolution", Core.camera.width, Core.camera.height);
			setUniformf("u_time", Time.time);

			setUniformf("mix_color", waveMix);
			setUniformf("mix_alpha", mixAlpha);
			setUniformf("mix_omiga", mixOmiga);
			setUniformf("wave_scl", waveScl);
			setUniformf("max_threshold", maxThreshold);
			setUniformf("min_threshold", minThreshold);
		}
	}

	public static class MirrorFieldShader extends Shader {
		public Color waveMix = Color.white;
		public Vec2 offset = new Vec2(0, 0);
		public float stroke = 2;
		public float gridStroke = 0.8f;
		public float mixAlpha = 0.4f;
		public float alpha = 0.2f;
		public float maxThreshold = 1;
		public float minThreshold = 0.7f;
		public float waveScl = 0.03f;
		public float sideLen = 10;

		/** The only instance of this class: {@link #mirrorField}. */
		MirrorFieldShader() {
			super(dsv("screenspace"), msf("mirror-field"));
		}

		@Override
		public void apply() {
			setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_resolution", Core.camera.width, Core.camera.height);
			setUniformf("u_time", Time.time);

			setUniformf("offset", offset);
			setUniformf("u_step", stroke);
			setUniformf("mix_color", waveMix);
			setUniformf("mix_alpha", mixAlpha);
			setUniformf("u_stroke", gridStroke);
			setUniformf("u_alpha", alpha);
			setUniformf("wave_scl", waveScl);
			setUniformf("max_threshold", maxThreshold);
			setUniformf("min_threshold", minThreshold);
			setUniformf("side_len", sideLen);
		}
	}

	public static final class MaskShader extends Shader {
		public Texture texture;

		/** The only instance of this class: {@link #alphaMask}. */
		MaskShader() {
			super(dsv("screenspace"), msf("alpha-mask"));
		}

		@Override
		public void apply() {
			setUniformi("u_texture", 1);
			setUniformi("u_mask", 0);

			texture.bind(1);
		}
	}

	public static final class ChromaticAberrationShader extends Shader {
		ChromaticAberrationShader() {
			super(dsv("screenspace"), msf("aberration"));
		}

		@Override
		public void apply() {
			setUniformf("u_dp", Scl.scl(1f));
			setUniformf("u_time", Time.time / Scl.scl(1f));
			setUniformf("u_offset",
					Core.camera.position.x - Core.camera.width / 2,
					Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_texsize", Core.camera.width, Core.camera.height);
		}
	}

	public static class MaterializeShader extends Shader {
		public float progress, offset, time;
		public int shadow;
		public Color color = new Color();
		public TextureRegion region;

		MaterializeShader() {
			super(dsv("default"), msf("materialize"));
		}

		@Override
		public void apply() {
			setUniformf("u_progress", progress);
			setUniformf("u_offset", offset);
			setUniformf("u_time", time);
			setUniformf("u_width", region.width);
			setUniformf("u_shadow", shadow);
			setUniformf("u_color", color);
			setUniformf("u_uv", region.u, region.v);
			setUniformf("u_uv2", region.u2, region.v2);
			setUniformf("u_texsize", region.texture.width, region.texture.height);
		}
	}

	public static class VerticalBuildShader extends Shader {
		public float progress, time;
		public Color color = new Color();
		public TextureRegion region;

		VerticalBuildShader() {
			super(dsv("default"), msf("vertical-build"));
		}

		@Override
		public void apply() {
			setUniformf("u_time", time);
			setUniformf("u_color", color);
			setUniformf("u_progress", progress);
			setUniformf("u_uv", region.u, region.v);
			setUniformf("u_uv2", region.u2, region.v2);
			setUniformf("u_texsize", region.texture.width, region.texture.height);
		}
	}

	public static class BlockBuildCenterShader extends Shader {
		public float progress;
		public TextureRegion region;
		public float time;

		BlockBuildCenterShader() {
			super(dsv("default"), msf("block-build-center"));
		}

		@Override
		public void apply() {
			setUniformf("u_progress", progress);
			setUniformf("u_uv", region.u, region.v);
			setUniformf("u_uv2", region.u2, region.v2);
			setUniformf("u_time", time);
			setUniformf("u_texsize", region.texture.width, region.texture.height);
		}
	}

	public static class TractorConeShader extends Shader {
		public float cx, cy;
		public float time, spacing, thickness;

		TractorConeShader() {
			super(dsv("screenspace"), msf("tractor-cone"));
		}

		@Override
		public void apply() {
			setUniformf("u_dp", Scl.scl(1f));
			setUniformf("u_time", time / Scl.scl(1f));
			setUniformf("u_offset",
					Core.camera.position.x - Core.camera.width / 2,
					Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_texsize", Core.camera.width, Core.camera.height);

			setUniformf("u_spacing", spacing / Scl.scl(1f));
			setUniformf("u_thickness", thickness / Scl.scl(1f));
			setUniformf("u_cx", cx / Scl.scl(1f));
			setUniformf("u_cy", cy / Scl.scl(1f));
		}

		public void setCenter(float x, float y) {
			cx = x;
			cy = y;
		}
	}

	public static class DimShader extends Shader {
		public float alpha;

		DimShader() {
			super(dsv("screenspace"), msf("dim"));
		}

		@Override
		public void apply() {
			setUniformf("u_alpha", alpha);
		}
	}

	public static class SmallSpaceShader extends Shader {
		Texture texture;

		SmallSpaceShader(String fragment) {
			super(dsv("screenspace"), msf(fragment));
		}

		@Override
		public void apply() {
			if (texture == null) {
				texture = new Texture(internalTree.child("sprites/small-space.png"));
				texture.setFilter(TextureFilter.linear);
				texture.setWrap(TextureWrap.repeat);
			}

			setUniformf("u_campos", Core.camera.position.x, Core.camera.position.y);
			setUniformf("u_ccampos", Core.camera.position);
			setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
			setUniformf("u_time", Time.time);

			texture.bind(1);
			renderer.effectBuffer.getTexture().bind(0);

			setUniformi("u_stars", 1);
		}
	}

	/** SurfaceShader but uses a mod fragment asset. */
	public static class PitShader extends SurfaceShaderf {
		protected TextureRegion topLayer, bottomLayer, truss;
		protected String topLayerName, bottomLayerName, trussName;

		public PitShader(String fragment, String topLayer, String bottomLayer, String truss) {
			super(fragment);
			topLayerName = topLayer;
			bottomLayerName = bottomLayer;
			trussName = truss;
		}

		@Override
		public void apply() {
			Texture texture = Core.atlas.find("grass1").texture;
			if (topLayer == null)
				topLayer = Core.atlas.find(topLayerName);

			if (bottomLayer == null)
				bottomLayer = Core.atlas.find(bottomLayerName);

			if (truss == null)
				truss = Core.atlas.find(trussName);

			if (noiseTex == null)
				noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);

			setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_resolution", Core.camera.width, Core.camera.height);
			setUniformf("u_time", Time.time);

			setUniformf("u_toplayer", topLayer.u, topLayer.v, topLayer.u2, topLayer.v2);
			setUniformf("u_bottomlayer", bottomLayer.u, bottomLayer.v, bottomLayer.u2, bottomLayer.v2);
			setUniformf("bvariants", bottomLayer.width / 32f);
			setUniformf("u_truss", truss.u, truss.v, truss.u2, truss.v2);

			texture.bind(2);
			noiseTex.bind(1);
			renderer.effectBuffer.getTexture().bind(0);
			setUniformi("u_noise", 1);
			setUniformi("u_texture2", 2);
		}
	}

	public static class SurfaceShaderf extends Shader {
		Texture noiseTex;

		public SurfaceShaderf(String fragment) {
			super(dsv("screenspace"), msf(fragment));
			loadNoise();
		}

		public String textureName() {
			return "noise";
		}

		public void loadNoise() {
			Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
				t.setFilter(TextureFilter.linear);
				t.setWrap(TextureWrap.repeat);
			};
		}

		@Override
		public void apply() {
			setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_resolution", Core.camera.width, Core.camera.height);
			setUniformf("u_time", Time.time);

			if (hasUniform("u_noise")) {
				if (noiseTex == null) {
					noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
				}

				noiseTex.bind(1);
				renderer.effectBuffer.getTexture().bind(0);

				setUniformi("u_noise", 1);
			}
		}
	}
}
