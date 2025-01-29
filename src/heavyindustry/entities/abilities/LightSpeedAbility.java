package heavyindustry.entities.abilities;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;

public class LightSpeedAbility extends Ability { //Combined of V5 and V7 coding just formatted to be compatible!
	public float damage = 1f;
	public float minSpeed = 1f;
	public float maxSpeed = 10f;

	protected Color color = Pal.lancerLaser;
	protected TextureRegion speedEffect;

	public LightSpeedAbility(float dmg, float minSpd, float maxSpd) {
		damage = dmg;
		minSpeed = minSpd;
		maxSpeed = maxSpd;
	}

	public LightSpeedAbility() {}

	@Override
	public void update(Unit unit) {
		super.update(unit);
		if (speedEffect == null) {
			Core.atlas.find(unit.type.name + "-shield");
		}
		float scl = scld(unit);
		if (Mathf.chance(Time.delta * (0.15 * scl))) {
			Fx.hitLancer.at(unit.x, unit.y, Pal.lancerLaser);
			Lightning.create(unit.team(), Pal.lancerLaser, damage * Vars.state.rules.unitDamageMultiplier, unit.x + unit.vel().x, unit.y + unit.vel().y, unit.rotation, 14);
		}
	}

	@Override
	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + damage);
	}

	protected float scld(Unit unit) { //make a similar method
		return Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
	}

	@Override
	public void draw(Unit unit) {
		float scl = scld(unit);
		TextureRegion region = Core.atlas.find(unit.type.name + "-shield");
		if (Core.atlas.isFound(region) && !(scl < 0.01f)) {
			Draw.color(color);
			Draw.alpha(scl / 2f);
			Draw.blend(Blending.additive);
			Draw.rect(region, unit.x + Mathf.range(scl / 2f), unit.y + Mathf.range(scl / 2f), unit.rotation - 90);
			Draw.blend();
		}
	}

	@Override
	public String localized() {
		return Core.bundle.format("ability.lightspeedability", damage);
	}
}
