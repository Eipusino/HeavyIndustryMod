package heavyindustry.gen;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawn;
import heavyindustry.type.unit.NucleoidUnitType;
import mindustry.content.Fx;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class NucleoidUnit extends BaseUnit implements Nucleoidc {
	public float recentDamage = 0f;
	public float reinforcementsReload = 0f;

	private NucleoidUnitType nucleoidType;

	public NucleoidUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(NucleoidUnit.class);
	}

	@Override
	public NucleoidUnitType type() {
		return nucleoidType;
	}

	@Override
	public void setType(UnitType type) {
		nucleoidType = checkType(type);

		super.setType(type);

		recentDamage = nucleoidType.maxDamagedPerSec;
		reinforcementsReload = nucleoidType.reinforcementsSpacing;
	}

	@Override
	public float mass() {
		return nucleoidType.mass;
	}

	@Override
	public void update() {
		super.update();

		recentDamage += nucleoidType.recentDamageResume * Time.delta;
		if (recentDamage >= nucleoidType.maxDamagedPerSec) {
			recentDamage = nucleoidType.maxDamagedPerSec;
		}

		reinforcementsReload += Time.delta;
		if (healthf() < 0.3f && reinforcementsReload >= nucleoidType.reinforcementsSpacing) {
			reinforcementsReload = 0;
			for (int i : Mathf.signs) {
				Tmp.v1.trns(rotation + 60 * i, -hitSize * 1.85f).add(x, y);
			}
		}
	}

	@Override
	public void draw() {
		super.draw();

		if (!nucleoidType.drawArrow) return;

		float z = Draw.z();
		Draw.z(Layer.bullet);

		Tmp.c1.set(team.color).lerp(Color.white, Mathf.absin(4f, 0.15f));
		Draw.color(Tmp.c1);
		Lines.stroke(3f);
		Drawn.circlePercent(x, y, hitSize * 1.15f, reinforcementsReload / nucleoidType.reinforcementsSpacing, 0);

		float scl = Interp.pow3Out.apply(Mathf.curve(reinforcementsReload / nucleoidType.reinforcementsSpacing, 0.96f, 1f));
		TextureRegion arrowRegion = nucleoidType.arrowRegion;

		for (int l : Mathf.signs) {
			float angle = 90 + 90 * l;
			for (int i = 0; i < 4; i++) {
				Tmp.v1.trns(angle, i * 50 + hitSize * 1.32f);
				float f = (100 - (Time.time + 25 * i) % 100) / 100;

				Draw.rect(arrowRegion, x + Tmp.v1.x, y + Tmp.v1.y, arrowRegion.width * f * scl, arrowRegion.height * f * scl, angle + 90);
			}
		}

		Draw.z(z);

	}

	@Override
	public void rawDamage(float amount) {
		boolean hadShields = shield > 0.0001f;
		if (hadShields) {
			shieldAlpha = 1f;
		}

		float a = amount * nucleoidType.damageMultiplier;

		a = Math.min(a, nucleoidType.maxOnceDamage);

		float shieldDamage = Math.min(Math.max(shield, 0f), a);
		shield -= shieldDamage;
		hitTime = 1f;

		a -= shieldDamage;
		a = Math.min(recentDamage / healthMultiplier, a);
		recentDamage -= a * 1.5f * healthMultiplier;

		if (a > 0f && type.killable) {
			health -= a;
			if (health <= 0f && !dead) {
				kill();
			}

			if (hadShields && shield <= 0.0001f) {
				Fx.unitShieldBreak.at(x, y, 0f, team.color, this);
			}
		}
	}

	@Override
	public void read(Reads read) {
		reinforcementsReload = read.f();

		super.read(read);
	}

	@Override
	public void write(Writes write) {
		write.f(reinforcementsReload);

		super.write(write);
	}

	@Override
	public void readSync(Reads read) {
		super.readSync(read);

		if (!isLocal()) {
			reinforcementsReload = read.f();
		} else {
			read.f();
		}
	}

	@Override
	public void writeSync(Writes write) {
		super.writeSync(write);

		write.f(reinforcementsReload);
	}

	@Override
	public float recentDamage() {
		return recentDamage;
	}

	@Override
	public float reinforcementsReload() {
		return reinforcementsReload;
	}

	@Override
	public void recentDamage(float value) {
		recentDamage = value;
	}

	@Override
	public void reinforcementsReload(float value) {
		reinforcementsReload = value;
	}
}
