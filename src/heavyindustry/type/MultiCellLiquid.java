package heavyindustry.type;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.entities.Puddles;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Puddle;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import java.util.Objects;

public class MultiCellLiquid extends CellLiquid {
	public Seq<Liquid> spreadTargets = new Seq<>();

	public MultiCellLiquid(String name, Color color) {
		super(name, color);
	}

	public MultiCellLiquid(String name) {
		super(name);
	}

	@Override
	public void init() {
		super.init();
		spreadTargets.removeAll(Objects::isNull);
	}

	@Override
	public void update(Puddle puddle) {
		if (!Vars.state.rules.fire) return;

		if (spreadTargets.any()) {
			float scaling = Mathf.pow(Mathf.clamp(puddle.amount / Puddles.maxLiquid), 2f);
			boolean reacted = false;

			for (Point2 point : Geometry.d4c) {
				Tile tile = puddle.tile.nearby(point);
				if (tile != null && tile.build != null && tile.build.liquids != null) {
					for (Liquid liquid : spreadTargets)
						if (tile.build.liquids.get(liquid) > 0.0001f) {
							float amount = Math.min(tile.build.liquids.get(liquid), maxSpread * Time.delta * scaling);
							tile.build.liquids.remove(liquid, amount * removeScaling);
							Puddles.deposit(tile, this, amount * spreadConversion);
							reacted = true;
						}
				}
			}

			//damage thing it is on
			if (spreadDamage > 0 && puddle.tile.build != null && puddle.tile.build.liquids != null) {
				for (Liquid liquid : spreadTargets)
					if (puddle.tile.build.liquids.get(liquid) > 0.0001f) {
						reacted = true;

						//spread in 4 adjacent directions around thing it is on
						float amountSpread = Math.min(puddle.tile.build.liquids.get(liquid) * spreadConversion, maxSpread * Time.delta) / 2f;
						for (Point2 dir : Geometry.d4) {
							Tile other = puddle.tile.nearby(dir);
							if (other != null) {
								Puddles.deposit(puddle.tile, other, puddle.liquid, amountSpread);
							}
						}

						puddle.tile.build.damage(spreadDamage * Time.delta * scaling);
					}
			}

			//spread to nearby puddles
			for (Point2 point : Geometry.d4) {
				Tile tile = puddle.tile.nearby(point);
				if (tile != null) {
					Puddle other = Puddles.get(tile);
					if (other != null && spreadTargets.contains(other.liquid)) {
						//TODO looks somewhat buggy when outputs are occurring
						float amount = Math.min(other.amount, Math.max(maxSpread * Time.delta * scaling, other.amount * 0.25f * scaling));
						other.amount -= amount;
						puddle.amount += amount;
						reacted = true;
						if (other.amount <= Puddles.maxLiquid / 3f) {
							other.remove();
							Puddles.deposit(tile, puddle.tile, this, Math.max(amount, Puddles.maxLiquid / 3f));
						}
					}
				}
			}

			if (reacted && this == Liquids.neoplasm) {
				Events.fire(Trigger.neoplasmReact);
			}
		}
	}

	@Override
	public float react(Liquid other, float amount, Tile tile, float x, float y) {
		return spreadTargets.contains(other) ? amount : 0f;
	}

	@Override
	public void drawPuddle(Puddle puddle) {
		super.drawPuddle(puddle);
		//TODO shader
	}
}
