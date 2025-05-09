package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;

import static heavyindustry.util.Utils.reverse;
import static mindustry.Vars.itemSize;

public class TubeDistributor extends Router {
	public TextureRegion bottomRegion, topRegion, rotatorRegion, lockedRegion1, lockedRegion2;

	public TubeDistributor(String name) {
		super(name);
		rotate = true;
	}

	@Override
	public void load() {
		super.load();
		bottomRegion = Core.atlas.find(name + "-bottom");
		topRegion = Core.atlas.find(name + "-top");
		rotatorRegion = Core.atlas.find(name + "-rotator");
		lockedRegion1 = Core.atlas.find(name + "-locked-side1");
		lockedRegion2 = Core.atlas.find(name + "-locked-side2");
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
		Draw.rect(rotatorRegion, plan.drawx(), plan.drawy());
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
		Draw.rect(plan.rotation > 1 ? lockedRegion2 : lockedRegion1, plan.drawx(), plan.drawy(), plan.rotation * 90);
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	public class TubeDistributorBuild extends RouterBuild {
		public Item lastItem;
		public Tile lastInput;
		public int lastTargetAngle, lastSourceAngle;
		public float time, rot, angle, lastRot;
		public int lastRotation = rotation;

		@Override
		public void updateTile() {
			if (lastItem == null && items.any()) {
				lastItem = items.first();
			}

			if (lastItem != null) {
				Building target = getTileTarget(lastItem, lastInput, false);

				if (blockValidInDirection(targetAngle())) {
					if (target != null || time < 1f) {
						time += 1f / speed * delta();
						if (time > 1f) time = 1f;
					}
				} else if (time < 0.4f) {
					time += 0.4f / speed * delta();
					if (time > 0.4f) time = 0.4f;
				}

				if (target != null && time >= 1f) {
					getTileTarget(lastItem, lastInput, true);
					target.handleItem(this, lastItem);
					items.remove(lastItem, 1);
					lastItem = null;
					time = 0f;
				}

				if (lastInput != null && lastItem != null) {
					int sa = sourceAngle(), ta = targetAngle();
					angle = computeAngle(sa, ta);
				}

				if (items.total() > 0 && !Vars.state.isPaused() && (!(time >= 1f) && (blockValidInDirection(targetAngle())))
						|| (!blockValidInDirection(targetAngle()) && !(time >= 0.4f))) {
					lastRot = rot;
					rot += speed * angle * delta();
				}
			}
		}

		protected float computeAngle(int sa, int ta) {
			return (sa == 0) ? ((ta == 2) ? 1 : ((ta == 0 || ta == 3) ? -1 : 1)) :
					(sa == 2) ? ((ta == 0 || ta == 1) ? -1 : 1) :
							(sa == 1) ? ((ta == 0 || ta == 3) ? -1 : 1) :
									((ta == 0 || ta == 1) ? 1 : -1);
		}

		public boolean blockValidInDirection(int direction) {
			Tile targetTile = tile.nearby(direction);
			return targetTile != null && (targetTile.block().hasItems || targetTile.block().acceptsItems);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return team == source.team && lastItem == null && items.total() == 0 && front() != source;
		}

		@Override
		public void handleItem(Building source, Item item) {
			items.add(item, 1);
			lastItem = item;
			time = 0f;
			lastInput = source.tile;
		}

		@Override
		public int removeStack(Item item, int amount) {
			int result = super.removeStack(item, amount);
			if (result != 0 && item == lastItem) {
				lastItem = null;
			}
			return result;
		}

		public int sourceAngle() {
			for (int sourceAngle = 0; sourceAngle < 4; sourceAngle++) {
				if (nearby(sourceAngle) == lastInput.build) {
					lastSourceAngle = sourceAngle;
					return sourceAngle;
				}
			}
			return lastSourceAngle;
		}

		public int targetAngle() {
			if (lastItem == null) return lastTargetAngle;
			Building target = getTileTarget(lastItem, lastInput, false);
			if (target != null) {
				for (int targetAngle = 0; targetAngle < 4; targetAngle++) {
					if (nearby(targetAngle) == target) {
						lastTargetAngle = targetAngle;
						return targetAngle;
					}
				}
			}
			return lastTargetAngle;
		}

		public void drawItem() {
			if (lastInput != null && lastInput.build != null && lastItem != null) {
				boolean isf = reverse(sourceAngle()) == targetAngle() || sourceAngle() == targetAngle();
				boolean alignment = targetAngle() == 0 || targetAngle() == 2;
				float ox, oy, s = size * 4, s2 = s * 2;
				float linearMove = (float) Math.sin(Math.PI * time) / 2.4f * s;

				if (alignment) {
					if (isf) {
						if (sourceAngle() == targetAngle()) {
							oy = time >= 0.5f ? linearMove : -linearMove;
							ox = time >= 0.5f ? (time * s2 - s) * (targetAngle() == 0 ? 1 : -1)
									: (time * s2 - s) * (targetAngle() == 0 ? -1 : 1);
						} else {
							oy = linearMove;
							ox = (time * s2 - s) * (targetAngle() == 0 ? 1 : -1);
						}
					} else {
						oy = sourceAngle() == 1 ? (time * -s + s) : (time * s - s);
						ox = time * s * (targetAngle() == 0 ? 1 : -1);
					}
				} else {
					if (isf) {
						if (sourceAngle() == targetAngle()) {
							ox = time >= 0.5f ? linearMove : -linearMove;
							oy = time >= 0.5f ? (time * s2 - s) * (targetAngle() == 1 ? 1 : -1)
									: (time * s2 - s) * (targetAngle() == 1 ? -1 : 1);
						} else {
							ox = (float) Math.sin(Math.PI * time) / 2.4f * s;
							oy = (time * s2 - s) * (targetAngle() == 1 ? 1 : -1);
						}
					} else {
						ox = sourceAngle() == 0 ? (time * -s + s) : (time * s - s);
						oy = time * s * (targetAngle() == 1 ? 1 : -1);
					}
				}

				Draw.rect(lastItem.fullIcon, x + ox, y + oy, itemSize, itemSize);
			}
		}

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			Draw.rect(bottomRegion, x, y);
			Draw.z(Layer.block - 0.2f);
			drawItem();
			Draw.z(Layer.block - 0.15f);
			Drawf.spinSprite(rotatorRegion, x, y, rot % 360);
			Draw.rect(topRegion, x, y);
			Draw.rect(rotation > 1 ? lockedRegion2 : lockedRegion1, x, y, rotdeg());
		}

		public Building getTileTarget(Item item, Tile from, boolean set) {
			int counter = lastRotation;
			for (int i = 0; i < proximity.size; i++) {
				Building other = proximity.get((i + counter) % proximity.size);
				if (set) lastRotation = ((byte) ((lastRotation + 1) % proximity.size));
				if (other.tile == from && from.block() == Blocks.overflowGate) continue;
				if (other.acceptItem(this, item)) {
					return other;
				}
			}
			return null;
		}
	}
}
