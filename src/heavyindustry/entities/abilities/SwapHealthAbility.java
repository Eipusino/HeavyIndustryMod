package heavyindustry.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;

public class SwapHealthAbility extends Ability {
	public float percent = 3600f, reload = 0.05f;
	public Effect chainLightningFade = Fx.none;

	public Seq<Unit> all = new Seq<>(Unit.class);
	public Interval timer = new Interval();

	public SwapHealthAbility() {}

	public SwapHealthAbility(float per, float rel) {
		percent = per;
		reload = rel;
	}

	@Override
	public void update(Unit unit) {
		if (!timer.get(reload) || unit.healthf() >= percent) {
			all.clear();
			return;
		}

		Units.nearby(null, unit.x, unit.y, unit.type.range, u -> {
			if (u.team != unit.team) all.add(u);
		});

		all.sort(e -> 1 - e.healthf());

		if (all.size > 0) {
			Unit other = all.get(0);
			chainLightningFade.at(unit.x, unit.y, 0, Pal.heal, other);
			float heal = other.healthf();
			other.health = unit.healthf() * other.maxHealth;
			unit.health = heal * unit.maxHealth;
		}
	}

	@Override
	public void displayBars(Unit unit, Table bars) {
		bars.add(new Bar(Core.bundle.get("ability.swap-health.bar"), Pal.accent, () -> 60f / reload)).row();
	}

	@Override
	public String getBundle() {
		return "ability.swap-health";
	}
}
