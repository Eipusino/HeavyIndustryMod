package heavyindustry.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class Thorns extends Block {
	public final int timerDamage = timers++;

	public float cooldown = 30f;
	public float damage = 8f;

	public boolean damaged = true;
	public float damagedMultiplier = 0.3f;

	public Thorns(String name) {
		super(name);

		sync = true;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.damage, 60f / cooldown * damage, StatUnit.perSecond);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ThornsBuild::new;
	}

	public class ThornsBuild extends Building {
		@Override
		public void draw() {
			Draw.color(team.color);
			Draw.alpha(0.22f);
			Fill.rect(x, y, 2f, 2f);
			Draw.color();
		}

		@Override
		public void unitOn(Unit unit) {
			if (timer.get(timerDamage, cooldown)) {
				unit.damage(damage);
				if (damaged) damage(damage * damagedMultiplier);
			}
		}
	}
}
