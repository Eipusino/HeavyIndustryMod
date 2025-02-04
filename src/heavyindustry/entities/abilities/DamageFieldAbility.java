package heavyindustry.entities.abilities;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DamageFieldAbility extends Ability {
	public float reload = 60, radius = 20, damage = 10;
	public float rotateSpeed = 1, sectors = 5, secRad = 0.14f;
	public Effect useEffect;
	public Color radColor;
	public StatusEffect effect;
	public float effectDuration = reload;

	protected float timer;

	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + Strings.autoFixed(damage * reload / 60, 2) + " " + StatUnit.perSecond.localized());
		t.row();
		t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" + Strings.autoFixed(radius / tilesize, 2) + " " + StatUnit.blocks.localized());
		t.row();
		t.add(effect.emoji() + " " + effect.localizedName);
	}

	@Override
	public void update(Unit unit) {
		if ((timer += Time.delta) >= reload && unit.isShooting) {
			Damage.damage(unit.team, unit.x, unit.y, radius, damage);
			timer = 0f;
			useEffect.at(unit.x, unit.y);
			if (effect != null) {
				Units.nearbyEnemies(unit.team, unit.x, unit.y, radius, other -> {
					other.apply(effect, effectDuration);
				});
			}
		}
	}

	@Override
	public void draw(Unit unit) {
		Draw.z(Layer.effect);
		Draw.color(radColor);

		if (unit.isShooting) {
			for (int i = 0; i < sectors; i++) {
				float rot = i * 360f / sectors - Time.time * rotateSpeed;
				Lines.arc(unit.x, unit.y, radius, secRad, rot);
			}
		}
	}

	public float timer() {
		return timer;
	}
}
