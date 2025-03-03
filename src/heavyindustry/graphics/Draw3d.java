package heavyindustry.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static mindustry.Vars.renderer;

/** Draws 2d region with applying {@link Mat3D}. */
public final class Draw3d {
	public static final Mat3D
			m1 = new Mat3D(),
			m2 = new Mat3D(),
			m3 = new Mat3D(),
			m4 = new Mat3D(),
			m5 = new Mat3D();
	public static final Vec3
			v1 = new Vec3(),
			v2 = new Vec3(),
			v3 = new Vec3(),
			v4 = new Vec3(),
			v5 = new Vec3(),
			v6 = new Vec3(),
			v7 = new Vec3();
	public static final float shadowLayer = Layer.flyingUnit + 1;
	public static final float shadowFadeEnd = 300f, zToShadowScl = 3f / shadowFadeEnd;

	private static final Seq<QueuedBloom> bloomQueue = new Seq<>();
	private static final Seq<Runnable> shadowQueue = new Seq<>();

	static float[] vertices = new float[3 * 2 * 4];

	private Draw3d() {}

	public static void init() {
		Events.run(Trigger.drawOver, () -> {
			if (shadowQueue.any()) {
				Draw.draw(shadowLayer, () -> {
					FrameBuffer buffer = renderer.effectBuffer;
					buffer.begin(Color.clear);
					Draw.sort(false);
					Gl.blendEquationSeparate(Gl.funcAdd, Gl.max);

					for (Runnable s : shadowQueue) {
						s.run();
					}

					Draw.sort(true);
					buffer.end();
					Gl.blendEquationSeparate(Gl.funcAdd, Gl.funcAdd);

					buffer.blit(HShaders.passThrough);
				});
				shadowQueue.clear();
			}

			if (bloomQueue.any()) {
				bloomQueue.sort(q -> q.layer);
				Bloom bloom = renderer.bloom;
				if (bloom != null) {
					Draw.draw(Layerf.skyBloom, () -> {
						bloom.capture();
						for (QueuedBloom b : bloomQueue) {
							b.draw.run();
						}
						bloom.render();
					});
				} else {
					for (QueuedBloom b : bloomQueue) {
						b.draw.run();
					}
				}
				bloomQueue.clear();
			}
		});
	}

	public static float shadowAlpha(float z) {
		return Mathf.clamp(1f - z / shadowFadeEnd);
	}

	public static float shadowScale(float z) {
		return 1f + zToShadowScl * z;
	}

	public static void drawAimDebug(float x, float y, float z, float length, float rotation, float tilt, float spread) {
		Lines.stroke(3f);
		Draw.color(Color.blue); //Down
		Lines3d.lineAngleBase(x, y, z, length, rotation, 0f, tilt - spread);
		Lines.stroke(6f);
		Draw.color(Pal.accent); //Center
		Lines3d.lineAngleBase(x, y, z, length, rotation, 0f, tilt);
		Lines.stroke(3f);
		Draw.color(Color.red); //Right
		Lines3d.lineAngleBase(x, y, z, length, rotation, -spread, tilt);
		Draw.color(Color.lime); //Left
		Lines3d.lineAngleBase(x, y, z, length, rotation, spread, tilt);
		Draw.color(Color.orange); //Up
		Lines3d.lineAngleBase(x, y, z, length, rotation, 0f, tilt + spread);
	}

	public static void drawDiskDebug(float x1, float y1, float x2, float y2, float z2, float rad) {
		float rotation = Angles.angle(x2, y2, x1, y1);
		float tilt = 90f - Angles.angle(Mathf.dst(x1, y1, x2, y2), z2);

		Tmp.v31.set(Vec3.Z).rotate(Vec3.Y, tilt).rotate(Vec3.Z, -rotation);
		Tmp.v32.set(rad, 0, 0).rotate(Vec3.Y, tilt).rotate(Vec3.Z, -rotation);

		Tmp.v32.rotate(Tmp.v31, Time.time * 2f);

		//Disk
		Lines.stroke(3f);
		Draw.color(Color.white);
		int vertCount = Lines.circleVertices(rad * Perspective.scale(x2, y2, z2));
		if (vertCount < 0) return;
		float[] verts = Fill3d.diskVertices(x2, y2, z2, rotation, 0f, tilt, rad, vertCount);
		for (int i = 0; i <= vertCount; i += 3) {
			float px2, py2, pz2;
			if (i == vertCount - 3) { //TODO Make a Lines3D.circle
				px2 = verts[0];
				py2 = verts[1];
				pz2 = verts[2];
			} else {
				px2 = verts[i + 3];
				py2 = verts[i + 3 + 1];
				pz2 = verts[i + 3 + 2];
			}
			Lines3d.line(verts[i], verts[i + 1], verts[i + 2], px2, py2, pz2);
		}
		//Stuff
		Draw.color(Color.yellow);
		Lines3d.line(x2, y2, z2, x2 + Tmp.v31.x, y2 + Tmp.v31.y, z2 + Tmp.v31.z);
		Draw.color(Color.purple);
		Lines3d.line(x2, y2, z2, x2 + Tmp.v32.x, y2 + Tmp.v32.y, z2 + Tmp.v32.z);
	}

	public static void drawLineDebug(float x1, float y1, float z1, float x2, float y2, float z2) {
		Lines3d.line(x1, y1, z1, x2, y2, z2);

		int pointCount = Lines3d.linePointCount(x1, y1, z1, x2, y2, z2);
		float[] points = Lines3d.linePoints(x1, y1, z1, x2, y2, z2, pointCount);
		for (int i = 0; i < points.length; i += 3) {
			float x = points[i],
					y = points[i + 1];
			Lines3d.line(x, y, 0, x, y, points[i + 2]);
		}
	}

	public static float layerOffset(float x, float y) {
		float max = Math.max(Core.camera.width, Core.camera.height);
		return -Mathf.dst(x, y, Core.camera.position.x, Core.camera.position.y) / max / 1000f;
	}

	public static float layerOffset(float cx, float cy, float tx, float ty) {
		float angleTo = Angles.angle(cx, cy, tx, ty),
				angleCam = Angles.angle(cx, cy, Core.camera.position.x, Core.camera.position.y);
		float angleDist = Angles.angleDist(angleTo, angleCam);
		float max = Math.max(Core.camera.width, Core.camera.height);

		return layerOffset(cx, cy) + Mathf.dst(cx, cy, tx, ty) * Mathf.cosDeg(angleDist) / max / 1000f;
	}

	public static void highBloom(Runnable draw) {
		highBloom(true, Draw.z(), draw);
	}

	public static void highBloom(float layer, Runnable draw) {
		highBloom(true, layer, draw);
	}

	public static void highBloom(boolean bloom, Runnable draw) {
		highBloom(bloom, Draw.z(), draw);
	}

	public static void highBloom(boolean bloom, float layer, Runnable draw) {
		if (bloom) {
			bloomQueue.add(new QueuedBloom(layer, draw));
		} else {
			float z = Draw.z();
			Draw.z(z + 0.01f);
			draw.run();
			Draw.z(z);
		}
	}

	public static void shadow(Runnable draw) {
		shadowQueue.add(draw);
	}

	public static void rect(Mat3D mat3D, TextureRegion region, float x, float y, float width, float height, float rotation) {
		float originX = width / 2f;
		float originY = height / 2f;
		rect(mat3D, region, x, y, width, height, rotation, originX, originY);
	}

	public static void rect(Mat3D mat3D, TextureRegion region, float x, float y, float width, float height, float rotation, float originX, float originY) {
		int idx = 0;
		//bottom left and top right corner points relative to origin
		float worldOriginX = x + originX;
		float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		// rotate
		float cos = Mathf.cosDeg(rotation);
		float sin = Mathf.sinDeg(rotation);
		v5.set(worldOriginX, worldOriginY, 0);
		setPoint(mat3D, cos * fx - sin * fy, sin * fx + cos * fy, v1);
		setPoint(mat3D, cos * fx - sin * fy2, sin * fx + cos * fy2, v2);
		setPoint(mat3D, cos * fx2 - sin * fy2, sin * fx2 + cos * fy2, v3);
		setPoint(mat3D, cos * fx2 - sin * fy, sin * fx2 + cos * fy, v4);
		setPoint(mat3D, 0, 0, v6);


		float u = region.u;
		float v = region.v2;
		float u2 = region.u2;
		float _v2 = region.v;

		float color = Draw.getColor().toFloatBits();
		float mixColor = Draw.getMixColor().toFloatBits();

		float u5 = (u + u2) / 2f;
		float _v5 = (v + _v2) / 2f;
		idx = vertex(idx, v1, u, v, color, mixColor);
		idx = vertex(idx, v2, u, _v2, color, mixColor);
		doubleLast(idx, v6, color, mixColor, u5, _v5);
		Draw.vert(region.texture, vertices, 0, vertices.length);
		idx = 0;
		idx = vertex(idx, v2, u, _v2, color, mixColor);
		idx = vertex(idx, v3, u2, _v2, color, mixColor);
		doubleLast(idx, v6, color, mixColor, u5, _v5);
		Draw.vert(region.texture, vertices, 0, vertices.length);
		idx = 0;
		idx = vertex(idx, v3, u2, _v2, color, mixColor);
		idx = vertex(idx, v4, u2, v, color, mixColor);
		doubleLast(idx, v6, color, mixColor, u5, _v5);
		Draw.vert(region.texture, vertices, 0, vertices.length);
		idx = 0;
		idx = vertex(idx, v4, u2, v, color, mixColor);
		idx = vertex(idx, v1, u, v, color, mixColor);
		doubleLast(idx, v6, color, mixColor, u5, _v5);
		Draw.vert(region.texture, vertices, 0, vertices.length);
	}

	private static void doubleLast(int idx, Vec3 vector, float color, float mixColor, float u5, float _v5) {
		idx = vertex(idx, vector, u5, _v5, color, mixColor);
		idx = vertex(idx, vector, u5, _v5, color, mixColor);
	}

	private static int vertex(int idx, Vec3 v1, float u, float v, float color, float mixColor) {
		vertices[idx++] = v1.x;
		vertices[idx++] = v1.y;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v;
		vertices[idx++] = mixColor;
		return idx;
	}

	private static void setPoint(Mat3D mat3D, float x, float y, Vec3 v) {
		v.set(x, y, 0);
		float len2 = v.len();
		Mat3D.prj(v, mat3D);
		v.x = transformCoord(v.x, v.z, len2);
		v.y = transformCoord(v.y, v.z, len2);
		v.add(v5);
	}

	private static float transformCoord(float coord, float z, float len2) {
		if (len2 == 0) return coord;
		return z > 0 ? coord / (z / len2 + 1) : coord * (-z / len2 + 1);
	}

	private static class QueuedBloom {
		public final float layer;
		public final Runnable draw;

		private QueuedBloom(float layer, Runnable draw) {
			this.layer = layer;
			this.draw = draw;
		}
	}
}
