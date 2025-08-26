package heavyindustry.entities.abilities;

import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.IntFloatMap;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class RegenProjectorAbility extends Ability {
	protected static final IntSet taken = new IntSet();
	//map building pos to mend amount (TODO just use buildings as keys? no lookup)
	protected static final IntFloatMap mendMap = new IntFloatMap();
	protected static long lastUpdateFrame = -1;

	public float range = 60f;

	public float healPercent = 12f / 60f;

	public float effectChance = 0.003f;
	public Effect effect = Fx.regenParticle;

	public Seq<Building> targets = new Seq<>(Building.class);
	public int lastChange = -2;
	public boolean anyTargets = false;
	public boolean didRegen = false;

	@Override
	public void addStats(Table t) {
		super.addStats(t);

		t.add(abilityStat("range", Strings.autoFixed((int) (1f / (healPercent / 100f) / 60f), 2)));
		t.row();
		t.add(abilityStat("repair-time", Strings.autoFixed(range  / tilesize, 1)));
		t.row();
	}

	public void updateTargets(Unit unit) {
		targets.clear();
		taken.clear();
		indexer.eachBlock(unit.team, Tmp.r1.setCentered(unit.x, unit.y, range * tilesize), b -> true, targets::add);
	}

	@Override
	public void update(Unit unit) {
		if (lastChange != world.tileChanges) {
			lastChange = world.tileChanges;
			updateTargets(unit);
		}

		didRegen = false;
		anyTargets = false;

		anyTargets = targets.contains(Building::damaged);

		//use Math.max to prevent stacking
		for (Building build : targets) {
			if (!build.damaged() || build.isHealSuppressed()) continue;

			didRegen = true;

			int pos = build.pos();
			//TODO periodic effect
			float value = mendMap.get(pos);
			mendMap.put(pos, Math.min(Math.max(value, healPercent * Time.delta * build.block.health / 100f), build.block.health - build.health));

			if (value <= 0 && Mathf.chanceDelta(effectChance * build.block.size * build.block.size)) {
				effect.at(build.x + Mathf.range(build.block.size * tilesize / 2f - 1f), build.y + Mathf.range(build.block.size * tilesize / 2f - 1f));
			}
		}

		if (lastUpdateFrame != state.updateId) {
			lastUpdateFrame = state.updateId;

			for (IntFloatMap.Entry entry : mendMap.entries()) {
				Building build = world.build(entry.key);
				if (build != null) {
					build.heal(entry.value);
					build.recentlyHealed();
				}
			}
			mendMap.clear();
		}
	}

	@Override
	public String getBundle() {
		return "ability.regen-projector";
	}
}
