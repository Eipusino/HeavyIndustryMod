package heavyindustry.world.blocks.power;

import arc.Core;
import arc.Graphics.Cursor;
import arc.Graphics.Cursor.SystemCursor;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import heavyindustry.ui.UIUtils;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.BlockStatus;

import static heavyindustry.HVars.modName;
import static mindustry.Vars.player;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class PowerAnalyzer extends PowerBlock {
	public float displayLength = 8f;
	public float displaySpacing = 4f;
	public float displayThickness = 2f;
	public boolean horizontal = false;

	public float changeTolerance = 5f;

	public Color produceColor = Pal.heal;
	public Color consumeColor = Pal.remove;
	public Color storedColor = Pal.power;

	public TextureRegion topRegion, arrowRegion;

	public PowerAnalyzer(String name) {
		super(name);
		update = false; //Does not need to update
		destructible = true;
		enableDrawStatus = false; //Don't draw by default. Obscures the normal display.
		conductivePower = true;
		consumesTap = true;
	}

	@Override
	public void load() {
		super.load();

		topRegion = Core.atlas.find(name + "-top");
		arrowRegion = Core.atlas.find(name + "-arrow", modName + "-power-analyzer-arrow");
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region, topRegion};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("power", PowerNode.makePowerBalance());
		addBar("batteries", PowerNode.makeBatteryBalance());
	}

	public class PowerAnalyzerBuild extends Building {
		public PowerAnalyzerBuild() {}

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			float produced = power.graph.getLastScaledPowerIn() * 60f, //per tick -> per sec
					consumed = power.graph.getLastScaledPowerOut() * 60f,
					stored = power.graph.getLastPowerStored(),
					cap = power.graph.getLastCapacity();

			Lines.stroke(displayThickness);
			if (produced + consumed > 0.001f) drawUsage(produced, consumed);
			drawStorage(stored, cap, produced - consumed);
			Draw.color();

			Draw.rect(topRegion, x, y);

			if (renderer.drawStatus) {
				drawStatus();
			}
		}

		public void drawUsage(float produced, float consumed) {
			float total = produced + consumed;
			if (horizontal) {
				Draw.color(produceColor);
				Lines.lineAngle(x - displayLength / 2f, y + displaySpacing / 2f, 0, displayLength * (produced / total), false);
				Draw.color(consumeColor);
				Lines.lineAngle(x + displayLength / 2f, y + displaySpacing / 2f, 180f, displayLength * (consumed / total), false);
			} else {
				Draw.color(produceColor);
				Lines.lineAngle(x - displaySpacing / 2f, y - displayLength / 2f, 90f, displayLength * (produced / total), false);
				Draw.color(consumeColor);
				Lines.lineAngle(x - displaySpacing / 2f, y + displayLength / 2f, -90f, displayLength * (consumed / total), false);
			}
		}

		public void drawStorage(float stored, float capacity, float net) {
			float powLen = displayLength * (stored / capacity);
			float alpha = Mathf.absin(25f / Mathf.PI2, 1f);
			boolean changing = !Mathf.zero(net, changeTolerance)
					&& !(net > 0 && Mathf.equal(stored, capacity, changeTolerance))
					&& !(net < 0 && Mathf.equal(stored, 0, changeTolerance));

			Draw.color(storedColor);
			if (horizontal) {
				Lines.lineAngle(x - displayLength / 2f, y - displaySpacing / 2f, 0f, powLen, false);

				float netLen = Math.min(displayLength - powLen, displayLength * (net / capacity));
				netLen = Math.max(-powLen, netLen);

				Draw.color(net < 0 ? consumeColor : produceColor);
				if (changing)
					Draw.rect(arrowRegion, x - displayLength / 2f + powLen, y - displaySpacing / 2f, Mathf.sign(net) * 90f - 90f);
				Draw.alpha(alpha);
				Lines.lineAngle(x - displayLength / 2f + powLen, y - displaySpacing / 2f, 0f, netLen, false);
			} else {
				Lines.lineAngle(x + displaySpacing / 2f, y - displayLength / 2f, 90f, powLen, false);

				float netLen = Math.min(displayLength - powLen, displayLength * (net / capacity));
				netLen = Math.max(-powLen, netLen);

				Draw.color(net < 0 ? consumeColor : produceColor);
				if (changing)
					Draw.rect(arrowRegion, x + displaySpacing / 2f, y - displayLength / 2f + powLen, Mathf.sign(net) * 90f);
				Draw.alpha(alpha);
				Lines.lineAngle(x + displaySpacing / 2f, y - displayLength / 2f + powLen, 90f, netLen, false);
			}
		}

		@Override
		public void drawStatus() { //Literally just removing the requirement of having a consumer
			if (enableDrawStatus) {
				float multiplier = block.size > 1 ? 1f : 0.64f;
				float brcx = x + (float) (block.size * 8) / 2f - 8f * multiplier / 2f;
				float brcy = y - (float) (block.size * 8) / 2f + 8f * multiplier / 2f;
				Draw.z(71f);
				Draw.color(Pal.gray);
				Fill.square(brcx, brcy, 2.5f * multiplier, 45f);
				Draw.color(status().color);
				Fill.square(brcx, brcy, 1.5f * multiplier, 45f);
				Draw.color();
			}
		}

		@Override
		public void drawSelect() {
			Drawf.select(x, y, size * tilesize / 2f + 2f, Pal.power);
		}

		@Override
		public BlockStatus status() {
			float net = (power.graph.getLastScaledPowerIn() - power.graph.getLastScaledPowerOut()) * 60f;

			if (Mathf.zero(net, changeTolerance)) return BlockStatus.noOutput;
			if (net < 0) return BlockStatus.noInput;
			if (net > 0) return BlockStatus.active;

			return BlockStatus.noInput;
		}

		@Override
		public Cursor getCursor() {
			return interactable(player.team()) ? SystemCursor.hand : SystemCursor.arrow;
		}

		@Override
		public void tapped() {
			UIUtils.powerInfoDialog.show(power.graph);
		}
	}
}
