package endfield.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.g3d.Camera3D;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.game.EventType.TileChangeEvent;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Tiles;

public class RenderContext {
	public float fovX = 60f;
	public Camera3D camera = new Camera3D() {{
		near = 1f;

		up.set(0f, 0f, -1f);
		direction.set(0f, -1f, 0f);
	}};

	protected float[] darkness;

	public RenderContext() {
		Events.run(Trigger.draw, () -> {
			camera.resize(Core.camera.width, Core.camera.height);

			double y = camera.width / 2d / Math.tan(fovX / 2d * Mathf.doubleDegRad);
			camera.position.set(Core.camera.position.x, (float) y, -Core.camera.position.y);
			camera.fov = (float) (2d * Math.atan2(camera.height, 2d * y) * Mathf.doubleRadDeg);
			camera.far = Math.max(150f, camera.position.y * 1.5f);
			camera.update();
		});

		Events.on(WorldLoadEvent.class, event -> {
			darkness = new float[Vars.world.width() * Vars.world.height()];
			Vars.world.tiles.each(this::updateDarkness);
		});

		Events.on(TileChangeEvent.class, event -> updateDarkness(event.tile.x, event.tile.y));
	}

	protected void updateDarkness(int x, int y) {
		float dark = Vars.world.getDarkness(x, y);
		if (dark > 0f) {
			darkness[Vars.world.packArray(x, y)] = 1f - Math.min((dark + 0.5f) / 4f, 1f);
		} else {
			darkness[Vars.world.packArray(x, y)] = 1f;
		}
	}

	public float darkness(float x, float y) {
		x /= Vars.tilesize;
		y /= Vars.tilesize;

		int x1 = (int) x, x2 = x1 + 1,
				y1 = (int) y, y2 = y1 + 1;

		float out = Vars.state.rules.borderDarkness ? 0f : 1f;
		Tiles t = Vars.world.tiles;

		return Mathf.lerp(
				Mathf.lerp(t.in(x1, y1) ? darkness[Vars.world.packArray(x1, y1)] : out, t.in(x2, y1) ? darkness[Vars.world.packArray(x2, y1)] : out, x % 1f),
				Mathf.lerp(t.in(x1, y2) ? darkness[Vars.world.packArray(x1, y2)] : out, t.in(x2, y2) ? darkness[Vars.world.packArray(x2, y2)] : out, x % 1f),
				y % 1f
		);
	}
}
