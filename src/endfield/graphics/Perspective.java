package endfield.graphics;

import arc.Core;
import arc.Events;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import endfield.math.Math3d;
import endfield.math.Mathm;
import mindustry.game.EventType.Trigger;

import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;

public final class Perspective {
	private static final Vec2 offsetPos = new Vec2();
	private static final Vec3 scalingPos = new Vec3();
	/** z values below this are considered on the ground, and bypass calculations. */
	private static final float groundTolerance = 0.001f;
	private static final Vec2 viewportSize = new Vec2();

	/** Viewport offset from the camera height in world units. */
	public static float viewportOffset = 80f;
	/** Minimum z value for the viewport. */
	public static float minViewportZ = 20f;
	/** Field of View in degrees */
	public static float fov = -1f;
	public static float fadeDst = 1024f;

	private static float lastScale;
	private static float cameraZ;

	static {
		if (!headless) {
			Events.run(Trigger.preDraw, () -> {
				int newFov = Core.settings.getInt("fov", 60);

				//Recalculate viewport size on scale or fov change
				if (renderer.getDisplayScale() != lastScale || newFov != fov) {
					lastScale = renderer.getDisplayScale();
					fov = newFov;
					cameraZ = calcCameraZ();
					viewportSize();
				}
			});
		}
	}

	private Perspective() {}

	/** @return If the z coordinate is below the viewport height. */
	public static boolean canDraw(float z) {
		return z < viewportZ();
	}

	/** @return Perspective projected coordinates to draw at. */
	public static Vec2 drawPos(float x, float y, float z) {
		if (z <= groundTolerance) return offsetPos.set(x, y);

		//viewport
		float vw = viewportSize.x, vh = viewportSize.y;
		float cx = Core.camera.position.x, cy = Core.camera.position.y;
		Vec3 scaled = scaleToViewport(x, y, z);

		offsetPos.set(scaled.x / vw * Core.camera.width, scaled.y / vh * Core.camera.height).add(cx, cy);
		return offsetPos;
	}

	/** Multiplicative size scale at a point. */
	public static float scale(float x, float y, float z) {
		if (z <= groundTolerance) return 1f;

		float cx = Core.camera.position.x, cy = Core.camera.position.y;
		float cz = cameraZ;

		x -= cx;
		y -= cy;
		float zz = cz - z;

		float px = x / zz * cz; //Position scaled to far plane.
		float py = y / zz * cz;

		float vx = x / zz * viewportOffset; //Position scaled to near plane.
		float vy = y / zz * viewportOffset;

		float d1 = Math3d.dst(vx, vy, cz - viewportOffset, x, y, z);
		float d2 = Math3d.dst(vx, vy, cz - viewportOffset, px, py, 0);

		return 1f + (1 / viewportSize.x * Core.camera.width - 1) * (1f - d1 / d2);
	}

	/** Fade out based on distance to viewport. */
	public static float alpha(float x, float y, float z) {
		if (z <= groundTolerance) return 1f;

		float vz = viewportZ();

		float dst = dstToViewport(x, y, z);
		float fade = Math.min(fadeDst, vz);

		if (dst > fade) {
			return 1f;
		} else if (z > vz) { //Behind viewport, should be 0
			return 0f;
		} else {
			return Interp.pow5In.apply(Mathm.clamp(dst / fade));
		}
	}

	/**
	 * @return camera z coordinate
	 */
	public static float cameraZ() {
		return cameraZ;
	}

	/**
	 * Never returns a negative, max to 0. If that's happening, calculations are probably breaking, so don't zoom in so much.
	 *
	 * @return viewport z coordinate
	 */
	public static float viewportZ() {
		return Math.max(cameraZ - viewportOffset, 0f);
	}

	public static Vec3 scaleToViewport(float x, float y, float z) {
		if (z <= groundTolerance) return scalingPos.set(x, y, 0);

		float cx = Core.camera.position.x, cy = Core.camera.position.y;

		x -= cx;
		y -= cy;
		float zz = cameraZ - z;

		return scalingPos.set(x / zz * viewportOffset, y / zz * viewportOffset, viewportZ());
	}

	public static float dstToViewport(float x, float y, float z) {
		Vec3 scaled = scaleToViewport(x, y, z);
		return scaled.dst(x - Core.camera.position.x, y - Core.camera.position.y, z);
	}

	public static float maxZoom() {
		float minCZ = minViewportZ + viewportOffset;
		float minWidth = (float) (minCZ * Math.tan(fov / 2f * Mathf.degRad)) * 2f;
		float maxScale = Math.max(Core.graphics.getHeight(), Core.graphics.getWidth()) / minWidth;

		return Math.min(24f, maxScale);
	}

	/** Calculates the size of the viewport. */
	private static void viewportSize() {
		float v1 = (float) (Math.tan(fov / 2f * Mathf.degRad) * viewportOffset * 2f);
		if (Core.camera.width >= Core.camera.height) {
			float v2 = v1 * (Core.camera.height / Core.camera.width);
			viewportSize.set(v1, v2);
		} else {
			float v2 = v1 * (Core.camera.width / Core.camera.height);
			viewportSize.set(v2, v1);
		}
	}

	/**
	 * Calculates the camera z coordinate based on FOV and the size of the vanilla camera.
	 *
	 * @return camera z coordinate
	 */
	private static float calcCameraZ() {
		float width = Math.max(Core.camera.width, Core.camera.height) / 2f;
		//TOA
		return (float) (width / Math.tan(fov / 2f * Mathf.degRad));
	}
}
