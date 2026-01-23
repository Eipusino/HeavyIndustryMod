package endfield.gen;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;

import static endfield.Vars2.boardTimeTotal;

public class DPSMechUnit extends MechUnit2 {
	public float totalOrigin, totalReal, hits, firstHitTime, lastHitTime, showBoardTime;

	@Override
	public int classId() {
		return Entitys.getId(DPSMechUnit.class);
	}

	public void recordDamage(float amount, boolean ignoreArmor) {
		float real = (ignoreArmor ? amount : Math.max(amount - armor, Vars.minArmorDamage * amount)) / healthMultiplier;
		totalOrigin += amount;
		totalReal += real;
		hits += 1;
		if (firstHitTime == 0) {
			firstHitTime = Time.time;
		}
		showBoardTime = boardTimeTotal;
		lastHitTime = Time.time;
	}

	@Override
	public void damagePierce(float amount, boolean withEffect) {
		float pre = hitTime;
		recordDamage(amount, true);
		if (!withEffect) {
			hitTime = pre;
		}
	}

	@Override
	public void rawDamage(float amount) {
		recordDamage(amount, false);
	}

	@Override
	public void damage(float amount, boolean withEffect) {
		float pre = hitTime;
		recordDamage(amount, false);
		if (!withEffect) {
			hitTime = pre;
		}
	}

	@Override
	public void update() {
		super.update();

		showBoardTime = Math.max(showBoardTime - Time.delta, 0);
		if (showBoardTime == 0 && totalOrigin > 0) {
			totalOrigin = 0;
			totalReal = 0;
			hits = 0;
			firstHitTime = 0;
			lastHitTime = 0;
			showBoardTime = 0;
		}
	}

	@Override
	public void draw() {
		super.draw();

		if (showBoardTime > 0) {
			Font font = Fonts.def;
			Color color = Color.yellow.cpy();
			float fontSize = 12f / 60f;
			float gap = Vars.mobile ? fontSize / 0.04f : fontSize / 0.06f;
			float dx = x - 20f;
			float dy = y + (Vars.mobile ? 52f : 40f);

			float gameDuration = lastHitTime - firstHitTime;
			float realDuration = gameDuration / 60f;
			float originDamage = totalOrigin;
			float realDamage = totalReal;
			float originDps = originDamage / (realDuration == 0 ? 1 : realDuration);
			float realDps = realDamage / (realDuration == 0 ? 1 : realDuration);

			Draw.z(Layer.weather + 1);
			color.a = Math.min(showBoardTime / boardTimeTotal * 3, 1);

			font.draw(Core.bundle.format("text.dps-info-armor", armor), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-hitsize", hitSize), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-hits", hits), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-duration-frame", Mathf.round(gameDuration)), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-duration-real", Mathf.round(realDuration)), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-origin-damage", Mathf.round(originDamage)), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-real-damage", Mathf.round(realDamage)), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-dps-origin", Mathf.round(originDps)), dx, (dy -= gap), color, fontSize, false, Align.left);
			font.draw(Core.bundle.format("text.dps-info-dps-real", Mathf.round(realDps)), dx, dy - gap, color, fontSize, false, Align.left);
			Draw.reset();
		}
	}
}
