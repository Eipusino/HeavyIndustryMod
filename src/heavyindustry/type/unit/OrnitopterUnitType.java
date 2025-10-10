package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import heavyindustry.gen.Ornitopterc;
import heavyindustry.graphics.Outliner;
import heavyindustry.util.ReflectUtils;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.world.meta.Env;

public class OrnitopterUnitType extends BaseUnitType {
	public final Seq<Blade> blades = new Seq<>(Blade.class);

	public float bladeDeathMoveSlowdown = 0.01f, fallDriftScl = 60f;
	public float fallSmokeX = 0f, fallSmokeY = 0f, fallSmokeChance = 0.1f;

	public OrnitopterUnitType(String name) {
		super(name);
		engineSize = 0f;
		outlineColor = new Color(0x454552ff);
		envDisabled = Env.space;
	}

	public void drawBlade(Unit unit) {
		float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

		applyColor(unit);

		if (unit instanceof Ornitopterc copter) {
			for (int sign : Mathf.signs) {
				long seedOffset = 0;
				for (BladeMount mount : copter.blades()) {
					Blade blade = mount.blade;
					float rx = unit.x + Angles.trnsx(unit.rotation - 90, blade.x * sign, blade.y);
					float ry = unit.y + Angles.trnsy(unit.rotation - 90, blade.x * sign, blade.y);
					float bladeScl = Draw.scl * blade.bladeSizeScl;
					float shadeScl = Draw.scl * blade.shadeSizeScl;


					if (blade.bladeRegion.found()) {
						Draw.z(z + blade.layerOffset);
						Draw.alpha(blade.blurRegion.found() ? 1 - (copter.bladeMoveSpeedScl() / 0.8f) : 1);
						Draw.rect(
								blade.bladeOutlineRegion, rx, ry,
								blade.bladeOutlineRegion.width * bladeScl * sign,
								blade.bladeOutlineRegion.height * bladeScl,
								unit.rotation - 90 + sign * Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
						);
						Draw.mixcol(Color.white, unit.hitTime);
						Draw.rect(blade.bladeRegion, rx, ry,
								blade.bladeRegion.width * bladeScl * sign,
								blade.bladeRegion.height * bladeScl,
								unit.rotation - 90 + sign * Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
						);
						Draw.reset();
					}

					if (blade.blurRegion.found()) {
						Draw.z(z + blade.layerOffset);
						Draw.alpha(copter.bladeMoveSpeedScl() * blade.blurAlpha * (copter.dead() ? copter.bladeMoveSpeedScl() * 0.5f : 1));
						Draw.rect(
								blade.blurRegion, rx, ry,
								blade.blurRegion.width * bladeScl * sign,
								blade.blurRegion.height * bladeScl,
								unit.rotation - 90 + sign * Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
						);
						Draw.reset();
					}

					if (blade.shadeRegion.found()) {
						Draw.z(z + blade.layerOffset + 0.001f);
						Draw.alpha(copter.bladeMoveSpeedScl() * blade.blurAlpha * (copter.dead() ? copter.bladeMoveSpeedScl() * 0.5f : 1));
						Draw.rect(
								blade.shadeRegion, rx, ry,
								blade.shadeRegion.width * shadeScl * sign,
								blade.shadeRegion.height * shadeScl,
								unit.rotation - 90 + sign * Mathf.randomSeed(copter.drawSeed() + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
						);
						Draw.mixcol(Color.white, unit.hitTime);
						Draw.reset();
					}
				}
			}
		}
	}

	@Override
	public void createIcons(MultiPacker packer) {
		super.createIcons(packer);

		for (Blade blade : blades) {
			Outliner.outlineRegion(packer, blade.bladeRegion, outlineColor, blade.spriteName + "-outline", outlineRadius);
			Outliner.outlineRegion(packer, blade.shadeRegion, outlineColor, blade.spriteName + "-top-outline", outlineRadius);
		}
	}

	@Override
	public void draw(Unit unit) {
		super.draw(unit);
		drawBlade(unit);
	}

	@Override
	public void load() {
		super.load();
		for (Blade b : blades) {
			b.load();
		}
	}

	public static class Blade implements Cloneable {
		public final String spriteName;

		public TextureRegion bladeRegion, blurRegion, bladeOutlineRegion, shadeRegion;

		public float x = 0f, y = 0f;

		public float bladeSizeScl = 1, shadeSizeScl = 1;

		/** Blade max moving distance. */
		public float bladeMaxMoveAngle = 12;

		/** Blade min moving distance. */
		public float bladeMinMoveAngle = 0f;

		public float layerOffset = 0.001f;

		public float blurAlpha = 0.9f;

		public Blade(String name) {
			spriteName = name;
		}

		public void load() {
			bladeRegion = Core.atlas.find(spriteName);
			blurRegion = Core.atlas.find(spriteName + "-blur");
			bladeOutlineRegion = Core.atlas.find(spriteName + "-outline");
			shadeRegion = Core.atlas.find(spriteName + "-blur-shade");
		}

		// For mirroring
		public Blade copy() {
			try {
				return (Blade) super.clone();
			} catch (CloneNotSupportedException e) {
				Log.err("java sucks", e);

				return ReflectUtils.copyProperties(this, new Blade(spriteName));
			}
		}
	}

	public static class BladeMount {
		public Blade blade;
		public float bladeRotation;

		public BladeMount(Blade bla) {
			blade = bla;
		}
	}
}
