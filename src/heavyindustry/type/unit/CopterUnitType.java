package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import heavyindustry.gen.Copterc;
import mindustry.gen.Unit;

public class CopterUnitType extends ExtraUnitType {
	public final Seq<Rotor> rotors = new Seq<>(2);
	public float rotorDeathSlowdown = 0.01f;
	public float fallRotateSpeed = 2.5f;

	public CopterUnitType(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		for (Rotor rotor : rotors) rotor.load();
	}

	@Override
	public void init() {
		super.init();

		Seq<Rotor> mapped = new Seq<>();
		for (Rotor rotor : rotors) {
			mapped.add(rotor);
			if (rotor.mirror) {
				Rotor copy = rotor.copy();
				if (copy == null) continue;
				copy.x *= -1f;
				copy.speed *= -1f;
				copy.shadeSpeed *= -1f;
				copy.rotOffset += 360f / (copy.bladeCount * 2);

				mapped.add(copy);
			}
		}
		rotors.set(mapped);
	}

	@Override
	public void draw(Unit unit) {
		super.draw(unit);

		if (unit instanceof Copterc copter) drawRotors(unit, copter);
	}

	public void drawRotors(Unit unit, Copterc copter) {
		applyColor(unit);

		RotorMount[] rotors = copter.rotors();
		for (RotorMount mount : rotors) {
			Rotor rotor = mount.rotor;
			float x = unit.x + Angles.trnsx(unit.rotation - 90f, rotor.x, rotor.y);
			float y = unit.y + Angles.trnsy(unit.rotation - 90f, rotor.x, rotor.y);

			float alpha = Mathf.curve(copter.rotorSpeedScl(), 0.2f, 1f);
			Draw.color(0f, 0f, 0f, rotor.shadowAlpha);
			float rad = 1.2f;
			float size = Math.max(rotor.bladeRegion.width, rotor.bladeRegion.height) * Draw.scl;

			Draw.rect(softShadowRegion, x, y, size * rad * Draw.xscl, size * rad * Draw.yscl);

			Draw.color();
			Draw.alpha(alpha * rotor.ghostAlpha);

			Draw.rect(rotor.bladeGhostRegion, x, y, mount.rotorRot);
			Draw.rect(rotor.bladeShadeRegion, x, y, mount.rotorShadeRot);

			Draw.alpha(1f - alpha * rotor.bladeFade);
			for (int j = 0; j < rotor.bladeCount; j++) {
				Draw.rect(rotor.bladeRegion, x, y,
						unit.rotation - 90f
								+ 360f / rotor.bladeCount * j
								+ mount.rotorRot
				);
			}
		}

		for (RotorMount mount : rotors) {
			Rotor rotor = mount.rotor;
			float x = unit.x + Angles.trnsx(unit.rotation - 90f, rotor.x, rotor.y);
			float y = unit.y + Angles.trnsy(unit.rotation - 90f, rotor.x, rotor.y);

			Draw.alpha(1f - Mathf.curve(copter.rotorSpeedScl(), 0.2f, 1f) * rotor.bladeFade);
			for (int j = 0; j < rotor.bladeCount; j++) {
				Draw.rect(rotor.bladeRegion, x, y,
						unit.rotation - 90f
								+ 360f / rotor.bladeCount * j
								+ mount.rotorRot
				);
			}

			Draw.color();
			Draw.rect(rotor.topRegion, x, y, unit.rotation - 90f);
		}

		Draw.reset();
	}

	public static class Rotor implements Cloneable {
		public final String name;

		public TextureRegion bladeRegion, bladeGhostRegion, bladeShadeRegion, topRegion;

		public boolean mirror;
		public float x;
		public float y;

		public float rotOffset = 0f;
		public float speed = 29f;
		public float shadeSpeed = 3f;
		public float ghostAlpha = 0.6f;
		public float shadowAlpha = 0.4f;
		public float bladeFade = 1f;

		public int bladeCount = 4;

		public Rotor(String nam) {
			name = nam;
		}

		public void load() {
			bladeRegion = Core.atlas.find(name + "-blade");
			bladeGhostRegion = Core.atlas.find(name + "-blade-ghost");
			bladeShadeRegion = Core.atlas.find(name + "-blade-shade");
			topRegion = Core.atlas.find(name + "-top");
		}

		public Rotor copy() {
			try {//This is definitely the most disgusting, stupid, and insulting design in Java!
				return (Rotor) super.clone();
			} catch (CloneNotSupportedException suck) {
				throw new RuntimeException("very good language design", suck);
			}
		}
	}

	/** Rotor entities that are mounted in units or other stuff. */
	public static class RotorMount {
		public final Rotor rotor;
		public float rotorRot;
		public float rotorShadeRot;

		public RotorMount(Rotor rot) {
			rotor = rot;
		}
	}
}
