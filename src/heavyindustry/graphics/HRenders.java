package heavyindustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

public final class HRenders {
	private static float
			//reduction rate of screen flash
			flashIntensity,
			//reduction rate of screen flash
			flashReduction,
			//current duration of screen flash
			flashTime;

	private HRenders() {}

	public static void init() {
		DimRenderer.load();
		SlashRenderer.init();

		Events.on(ResetEvent.class, e -> {
			flashReduction = flashTime = 0;
		});

		Events.run(Trigger.update, () -> {
			flashIntensity -= flashReduction * Time.delta;
		});

		Events.run(Trigger.drawOver, () -> {
			Draw.draw(Layer.playerName + 1.5f, () -> {
				DimRenderer.draw();

				if (flashIntensity > 0.001f) {
					Draw.color(Color.white, flashIntensity);
					Draw.rect();
					Draw.color();
				}
			});

			SlashRenderer.draw();
		});
	}

	public static void flash(float intensity, float duration) {
		flashIntensity = intensity;
		flashTime = Math.max(flashTime, duration);
		flashReduction = flashIntensity / flashTime;
	}

	public static void flash(float duration) {
		flash(1f, duration);
	}

	public static void dimAlpha(float a) {
		DimRenderer.updateAlpha(a);
	}

	public static void dimGlow(float x, float y, float radius, float opacity) {
		DimRenderer.add(x, y, radius, opacity);
	}

	public static void dimGlow(Position pos, float radius, float opacity) {
		dimGlow(pos.getX(), pos.getY(), radius, opacity);
	}

	public static void dimGlow(float x, float y, TextureRegion region, float opacity) {
		dimGlow(x, y, region, 0f, opacity);
	}

	public static void dimGlow(float x, float y, TextureRegion region, float rotation, float opacity) {
		DimRenderer.add(x, y, region, rotation, opacity);
	}

	public static void dimGlowLine(float x, float y, float x2, float y2) {
		DimRenderer.line(x, y, x2, y2, 30, 0.3f);
	}

	public static void dimGlowLine(float x, float y, float x2, float y2, float stroke, float alpha) {
		DimRenderer.line(x, y, x2, y2, stroke, alpha);
	}
}
