package heavyindustry.graphics.g3d;

import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class CircleMesh extends PlanetMesh {
	public final Mesh mesh;
	public Texture texture;
	public Color color = Color.white.cpy();

	public CircleMesh(Texture texture, Planet planet, int sides, float radiusIn, float radiusOut, Vec3 axis) {
		this.planet = planet;
		this.texture = texture;

		MeshUtils.begin(sides * 6 * (3 + 3 + 2) * 2);

		Tmp.v33.setZero();

		class MeshPoint {
			final Vec3 position;
			final Vec2 textureCords;

			public MeshPoint(Vec3 position, Vec2 textureCords) {
				this.position = position;
				this.textureCords = textureCords;
			}
		}

		MeshPoint[] meshPoints = {new MeshPoint(Tmp.v31.setZero(), Tmp.v1.set(0, 0)), new MeshPoint(Tmp.v33.setZero(), Tmp.v3.set(1, 0)), new MeshPoint(Tmp.v34.setZero(), Tmp.v4.set(1, 1)), new MeshPoint(Tmp.v32.setZero(), Tmp.v2.set(0, 1))};

		int[] order = {0, 1, 2, 2, 3, 0};
		Vec3 plane = new Vec3().set(1, 0, 0).rotate(Vec3.X, 90).rotate(Vec3.X, axis.angle(Vec3.X) + 1).rotate(Vec3.Y, axis.angle(Vec3.Y) + 1).rotate(Vec3.Z, axis.angle(Vec3.Z) + 1).crs(axis);

		Vec3 inv = axis.cpy().unaryMinus();

		for (int i = 0; i < sides; i++) {
			meshPoints[0].position.set(plane).rotate(axis, i * 1f / sides * 360).setLength2(1).scl(radiusIn);

			meshPoints[1].position.set(plane).rotate(axis, i * 1f / sides * 360).setLength2(1).scl(radiusOut);

			meshPoints[2].position.set(plane).rotate(axis, (i + 1f) / sides * 360).setLength2(1).scl(radiusOut);

			meshPoints[3].position.set(plane).rotate(axis, (i + 1f) / sides * 360).setLength2(1).scl(radiusIn);

			for (int j : order) {
				MeshPoint point = meshPoints[j];
				MeshUtils.vert(point.position, axis, point.textureCords);
			}
			for (int j = order.length - 1; j >= 0; j--) {
				MeshPoint point = meshPoints[order[j]];
				MeshUtils.vert(point.position, inv, point.textureCords);
			}
		}

		mesh = MeshUtils.end();
	}

	private static Shader shader() {
		return Shadersf.planetTexture;
	}

	@Override
	public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
		//don't waste performance rendering 0-alpha
		if (params.planet == planet && Mathf.zero(1f - params.uiAlpha, 0.01f)) return;

		preRender(params);

		Shader shader = shader();
		shader.bind();
		shader.setUniformMatrix4("u_proj", projection.val);
		shader.setUniformMatrix4("u_trans", transform.val);
		shader.setUniformf("u_color", color);
		setPlanetInfo("u_sun_info", planet.solarSystem);
		setPlanetInfo("u_planet_info", planet);
		texture.bind(0);
		shader.setUniformi("u_texture", 0);
		shader.apply();

		mesh.render(shader, Gl.triangles);
	}

	@Override
	public void preRender(PlanetParams params) {
		Shadersf.planetTexture.planet = planet;
		Shadersf.planetTexture.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
		Shadersf.planetTexture.ambientColor.set(planet.solarSystem.lightColor);
		//TODO better disappearing
		Shadersf.planetTexture.alpha = params.planet == planet ? 1f - params.uiAlpha : 1f;
	}

	private void setPlanetInfo(String name, Planet planet) {
		Vec3 position = planet.position;
		Shader shader = shader();
		shader.setUniformf(name, position.x, position.y, position.z, planet.radius);
	}
}
