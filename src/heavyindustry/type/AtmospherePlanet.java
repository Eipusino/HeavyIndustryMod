package heavyindustry.type;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Nullable;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.gl.DepthFrameBuffer;
import mindustry.Vars;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

/**
 * Just a regular planet, but with a fixed atmosphere shader at the little cost of performance.
 *
 * @since 1.0.6
 */
public class AtmospherePlanet extends Planet {
	public @Nullable DepthFrameBuffer buffer;

	public AtmospherePlanet(String name, Planet parent, float radius) {
		super(name, parent, radius);
	}

	public AtmospherePlanet(String name, Planet parent, float radius, int sectorSize) {
		super(name, parent, radius, sectorSize);
	}

	@Override
	public void load() {
		super.load();
		if (!Vars.headless && buffer == null) {
			buffer = new DepthFrameBuffer(2, 2, true);
			buffer.getTexture().setFilter(TextureFilter.nearest);
		}
	}

	@Override
	public void drawAtmosphere(Mesh atmosphere, Camera3D cam) {
		Gl.depthMask(false);
		Blending.additive.apply();

		var shader = HShaders.depthAtmosphere;
		shader.camera = cam;
		shader.planet = this;
		shader.bind();
		shader.apply();
		atmosphere.render(shader, Gl.triangles);

		Blending.normal.apply();
		Gl.depthMask(true);
	}

	public class AtmosphereHexMesh implements GenericMesh {
		protected Mesh mesh;

		public AtmosphereHexMesh(HexMesher mesher, int divisions) {
			mesh = MeshBuilder.buildHex(mesher, divisions, radius, 0.2f);
		}

		public AtmosphereHexMesh(int divisions) {
			this(generator, divisions);
		}

		@Override
		public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
			buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			buffer.begin(Color.clear);

			var shader = Shaders.planet;
			shader.planet = AtmospherePlanet.this;
			shader.lightDir.set(solarSystem.position).sub(position).rotate(Vec3.Y, getRotation()).nor();
			shader.ambientColor.set(solarSystem.lightColor);
			shader.bind();
			shader.setUniformMatrix4("u_proj", Vars.renderer.planets.cam.combined.val);
			shader.setUniformMatrix4("u_trans", transform.val);
			shader.apply();
			mesh.render(shader, Gl.triangles);

			buffer.end();

			var blit = HShaders.depthScreenspace;
			blit.buffer = buffer;
			Draw.blit(blit);
		}

		@Override
		public void dispose() {
			mesh.dispose();
		}

		@Override
		public boolean isDisposed() {
			return mesh.isDisposed();
		}
	}
}