package heavyindustry.world.blocks.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;

public class AdjustableShieldWall extends Wall {
	// radius of shield. 1 = default shield wall radiuss
	public float radius = 2f;
	// shield health
	public float shieldHealth = 400f;
	// break cooldown
	public float breakCooldown = 850f;
	public float regenSpeed = 2f;
	public Color glowColor = new Color(0xff753188);
	public float glowMag = 0.6f;
	public float glowScl = 8f;
	public TextureRegion glowRegion;

	public AdjustableShieldWall(String name) {
		super(name);
		update = true;
		configurable = true;
		saveConfig = true;
		canOverdrive = false;
	}

	@Override
	public void load() {
		super.load();
		glowRegion = Core.atlas.find(name + "-glow");
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.shieldHealth, shieldHealth);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AdjustableShieldWallBuild::new;
	}

	public class AdjustableShieldWallBuild extends WallBuild {
		public boolean enabled = true;
		public float shield;
		public float shieldRadius;
		public float breakTimer;

		@Override
		public void draw() {
			Draw.rect(block.region, x, y);
			if (enabled) {
				if (shieldRadius > 0f) {
					float radiusN = shieldRadius * 8f * (float) size / 2f * radius;
					Draw.z(125f);
					Draw.color(team.color, Color.white, Mathf.clamp(hit));
					if (Vars.renderer.animateShields) {
						Fill.square(x, y, radiusN);
					} else {
						Lines.stroke(1.5f);
						Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
						Fill.square(x, y, radiusN);
						Draw.alpha(1f);
						Lines.poly(x, y, 4, radiusN, 45f);
						Draw.reset();
					}

					Draw.reset();
					Drawf.additive(glowRegion, glowColor, (1f - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius, x, y, 0f, 31f);
				}
			}
		}

		@Override
		public void updateTile() {
			if (enabled) {
				if (breakTimer > 0f) {
					breakTimer -= Time.delta;
				} else {
					shield = Mathf.clamp(shield + regenSpeed * edelta(), 0f, shieldHealth);
				}

				if (hit > 0f) {
					hit -= Time.delta / 10f;
					hit = Math.max(hit, 0f);
				}

				shieldRadius = Mathf.lerpDelta(shieldRadius, broken() ? 0f : 1f, 0.12f);
			}
		}

		public boolean broken() {
			return breakTimer > 0f || !canConsume();
		}

		@Override
		public void damage(float damage) {
			if (enabled) {
				float shieldTaken = broken() ? 0f : Math.min(shield, damage);
				shield -= shieldTaken;
				if (shieldTaken > 0f) {
					hit = 1f;
				}

				if (shield <= 0.00001f && shieldTaken > 0f) {
					breakTimer = breakCooldown;
				}

				if (damage - shieldTaken > 0f) {
					super.damage(damage - shieldTaken);
				}
			} else {
				super.damage(damage);
			}
		}

		@Override
		public void buildConfiguration(Table table) {
			table.button(Icon.defense, Styles.logici, () -> {
				enabled = true;
				deselect();
			}).size(40f);
			table.button(Icon.cancel, Styles.logici, () -> {
				enabled = false;
				deselect();
			}).size(40f);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(shield);
			write.bool(enabled);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			shield = read.f();
			if (shield > 0f) {
				shieldRadius = 1f;
			}
			enabled = read.bool();
		}
	}
}