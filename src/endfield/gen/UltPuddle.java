package endfield.gen;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.pooling.Pools;
import endfield.math.Mathm;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

public class UltPuddle extends Puddle {
	public static UltPuddle createUlt() {
		return Pools.obtain(UltPuddle.class, UltPuddle::new);
	}

	public static void deposit(Tile tile, Tile source, Liquid liquid, float amount, boolean initial) {
		deposit(tile, source, liquid, amount, initial, false);
	}

	public static void deposit(Tile tile, Tile source, Liquid liquid, float amount, boolean initial, boolean cap) {
		if (tile == null) return;

		float ax = (tile.worldx() + source.worldx()) / 2f, ay = (tile.worldy() + source.worldy()) / 2f;

		if (liquid.willBoil()) {
			if (Mathf.chanceDelta(0.16f)) {
				liquid.vaporEffect.at(ax, ay, liquid.gasColor);
			}
			return;
		}

		if (Vars.state.rules.hasEnv(Env.space)) {
			if (Mathf.chanceDelta(0.11f) && tile != source) {
				Bullets.spaceLiquid.create(null, source.team(), ax, ay, source.angleTo(tile) + Mathf.range(50f), -1f, Mathf.random(0f, 0.2f), Mathf.random(0.6f, 1f), liquid);
			}
			return;
		}

		if (tile.floor().isLiquid && !liquid.canStayOn.contains(tile.floor().liquidDrop)) {
			reactPuddle(tile.floor().liquidDrop, liquid, amount, tile, ax, ay);

			Puddle p = Puddles.get(tile);

			if (initial && p != null && p.lastRipple <= Time.time - 40f) {
				Fx.ripple.at(ax, ay, 1f, tile.floor().liquidDrop.color);
				p.lastRipple = Time.time;
			}
			return;
		}

		if (tile.floor().solid) return;

		Puddle p = Puddles.get(tile);
		if (p == null || p.liquid == null) {
			if (!Vars.net.client()) {
				//do not create puddles clientside as that destroys syncing
				UltPuddle puddle = createUlt();
				puddle.tile = tile;
				puddle.liquid = liquid;
				puddle.amount = Math.min(amount, Puddles.maxLiquid);
				puddle.set(ax, ay);
				Puddles.register(puddle);
				puddle.add();
			}
		} else if (p.liquid == liquid) {
			p.accepting = Math.max(amount, p.accepting);

			if (initial && p.lastRipple <= Time.time - 40f && p.amount >= Puddles.maxLiquid / 2f) {
				Fx.ripple.at(ax, ay, 1f, p.liquid.color);
				p.lastRipple = Time.time;
			}
		} else {
			float added = reactPuddle(p.liquid, liquid, amount, p.tile, (p.x + source.worldx()) / 2f, (p.y + source.worldy()) / 2f);

			if (cap) {
				added = Mathm.clamp(Puddles.maxLiquid - p.amount, 0f, added);
			}

			p.amount += added;
		}
	}

	/** Reacts two liquids together at a location. */
	static float reactPuddle(Liquid dest, Liquid liquid, float amount, Tile tile, float x, float y) {
		if (dest == null) return 0f;

		if ((dest.flammability > 0.3f && liquid.temperature > 0.7f) ||
				(liquid.flammability > 0.3f && dest.temperature > 0.7f)) { //flammable liquid + hot liquid
			UltFire.create(tile);
			if (Mathf.chance(0.006 * amount)) {
				Bullets.fireball.createNet(Team.derelict, x, y, Mathf.random(360f), -1f, 1f, 1f);
			}
		} else if (dest.temperature > 0.7f && liquid.temperature < 0.55f) { //cold liquid poured onto hot Puddle
			if (Mathf.chance(0.5f * amount)) {
				Fx.steam.at(x, y);
			}
			return -0.1f * amount;
		} else if (liquid.temperature > 0.7f && dest.temperature < 0.55f) { //hot liquid poured onto cold Puddle
			if (Mathf.chance(0.8f * amount)) {
				Fx.steam.at(x, y);
			}
			return -0.7f * amount;
		}
		return dest.react(liquid, amount, tile, x, y);
	}

	@Override
	public int classId() {
		return Entitys.getId(UltPuddle.class);
	}

	@Override
	public void update() {
		if (liquid == null || tile == null) {
			remove();
			return;
		}

		float addSpeed = accepting > 0 ? 3f : 0f;

		amount -= Time.delta * (1f - liquid.viscosity) / (5f + addSpeed);
		amount += accepting;
		amount = Math.min(amount, Puddles.maxLiquid);
		accepting = 0f;

		if (amount >= Puddles.maxLiquid / 1.5f) {
			float deposited = Math.min((amount - Puddles.maxLiquid / 1.5f) / 4f, 0.3f * Time.delta);
			int targets = 0;
			for (Point2 point : Geometry.d4) {
				Tile other = Vars.world.tile(tile.x + point.x, tile.y + point.y);
				if (other != null && (other.block() == Blocks.air || liquid.moveThroughBlocks)) {
					targets++;
					Puddles.deposit(other, tile, liquid, deposited, false);
				}
			}
			amount -= deposited * targets;
		}

		if (liquid.capPuddles) {
			amount = Mathm.clamp(amount, 0f, Puddles.maxLiquid);
		}

		if (amount <= 0f) {
			remove();
			return;
		}

		if (Puddles.get(tile) != this && added) {
			//force removal without pool free
			Groups.all.remove(this);
			Groups.draw.remove(this);
			Groups.puddle.remove(this);
			added = false;
			return;
		}

		//effects-only code
		if (amount >= Puddles.maxLiquid / 2f && updateTime <= 0f) {
			paramPuddle = this;

			Units.nearby(rect.setSize(Mathm.clamp(amount / (Puddles.maxLiquid / 1.5f)) * 10f).setCenter(x, y), unitCons);

			if (liquid.temperature > 0.7f && tile.build != null && Mathf.chance(0.5)) {
				UltFire.create(tile);
			}

			updateTime = 40f;

			if (tile.build != null) {
				tile.build.puddleOn(this);
			}
		}

		if (!Vars.headless && liquid.particleEffect != Fx.none) {
			if ((effectTime += Time.delta) >= liquid.particleSpacing) {
				float size = Mathm.clamp(amount / (Puddles.maxLiquid / 1.5f)) * 4f;
				liquid.particleEffect.at(x + Mathf.range(size), y + Mathf.range(size));
				effectTime = 0f;
			}
		}

		updateTime -= Time.delta;

		liquid.update(this);
	}
}
