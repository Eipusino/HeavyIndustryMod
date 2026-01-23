package endfield.entities.abilities;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Table;
import arc.struct.IntFloatMap;
import arc.util.Strings;
import arc.util.Time;
import endfield.math.Mathm;
import endfield.util.Constant;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class RegenProjectorAbility extends Ability {
	protected static final IntFloatMap mendMap = new IntFloatMap();
	protected static Rect tmpRect = new Rect();

	public static long lastUpdateFrame = -1;

	public boolean suppressed = false;

	public float range = 60f;

	public float healPercent = 12f / 60f;

	public float effectChance = 0.003f;
	public Effect effect = Fx.regenParticle;
	public Color color = Pal.regen;

	public RegenProjectorAbility() {}

	@Override
	public void addStats(Table t) {
		super.addStats(t);

		t.add(abilityStat("range", Strings.autoFixed((int) (1f / (healPercent / 100f) / 60f), 2)));
		t.row();
		t.add(abilityStat("repair-time", Strings.autoFixed(range  / Vars.tilesize, 1)));
		t.row();
	}

	@Override
	public void update(Unit unit) {
		//use Math.max to prevent stacking
		Vars.indexer.eachBlock(unit.team, tmpRect.setCentered(unit.x, unit.y, range * Vars.tilesize), Constant.BOOLF_BUILDING_TRUE, build -> {
			if (!build.damaged() || (suppressed && build.isHealSuppressed())) return;

			int pos = build.pos();
			float value = mendMap.get(pos);
			mendMap.put(pos, Mathm.clamp(value, build.block.health - build.health, healPercent * Time.delta * build.block.health / 100f));

			if (value <= 0 && Mathf.chanceDelta(effectChance * build.block.size * build.block.size)) {
				effect.at(build.x + Mathf.range(build.block.size * Vars.tilesize / 2f - 1f), build.y + Mathf.range(build.block.size * Vars.tilesize / 2f - 1f), color);
			}
		});

		if (lastUpdateFrame != Vars.state.updateId) {
			lastUpdateFrame = Vars.state.updateId;

			for (var entry : mendMap.entries()) {
				Building build = Vars.world.build(entry.key);
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
