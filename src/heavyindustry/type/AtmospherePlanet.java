package heavyindustry.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

import static mindustry.Vars.*;

/**
 * Just a regular planet, but with a fixed atmosphere shader at the little cost of performance.
 *
 * @since 1.0.6
 */
public class AtmospherePlanet extends Planet {
	public @Nullable FrameBuffer buffer;

	public AtmospherePlanet(String name, Planet parent, float radius) {
		super(name, parent, radius);
	}

	public AtmospherePlanet(String name, Planet parent, float radius, int sectorSize) {
		super(name, parent, radius, sectorSize);
	}

	@Override
	public void load() {
		super.load();
		if (!headless) {
			buffer = new FrameBuffer(Core.graphics.getWidth(), Core.graphics.getHeight(), true);
			buffer.getTexture().setFilter(TextureFilter.nearest);
		}
	}

	@Override
	public void drawAtmosphere(Mesh atmosphere, Camera3D cam) {
		Gl.depthMask(false);
		Blending.additive.apply();

		var shader = Shadersf.depthAtmosphere;
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
			mesh = MeshBuilder.buildHex(mesher, divisions, false, radius, 0.2f);
		}

		public AtmosphereHexMesh(int divisions) {
			this(generator, divisions);
		}

		@Override
		public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
			if (params.alwaysDrawAtmosphere || Core.settings.getBool("atmosphere")) {
				var depth = Shadersf.depth;
				buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
				buffer.begin(Tmp.c1.set(0xffffff00));
				Blending.disabled.apply();

				depth.camera = renderer.planets.cam;
				depth.bind();
				depth.setUniformMatrix4("u_proj", projection.val);
				depth.setUniformMatrix4("u_trans", transform.val);
				depth.apply();
				mesh.render(depth, Gl.triangles);

				Blending.normal.apply();
				buffer.end();
			}

			var shader = Shaders.planet;
			shader.planet = AtmospherePlanet.this;
			shader.lightDir.set(solarSystem.position).sub(position).rotate(Vec3.Y, getRotation()).nor();
			shader.ambientColor.set(solarSystem.lightColor);
			shader.bind();
			shader.setUniformMatrix4("u_proj", projection.val);
			shader.setUniformMatrix4("u_trans", transform.val);
			shader.apply();
			mesh.render(shader, Gl.triangles);
		}
	}
}