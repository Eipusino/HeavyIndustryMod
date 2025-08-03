package heavyindustry.type;

import arc.graphics.g2d.Draw;
import heavyindustry.ai.NullAI;
import heavyindustry.type.unit.BaseUnitType;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

public class DummyUnitType extends BaseUnitType {
	public DummyUnitType(String name){
		super(name);

		controller = u -> new NullAI();
		envEnabled = Env.any;
		envDisabled = 0;
		isEnemy = false;
		allowedInPayloads = false;
		logicControllable = false;
		playerControllable = false;
		hidden = true;
		hoverable = false;
		canBoost = true;
		useUnitCap = false;
		killable = false;
	}

	@Override
	public void setStats(){
		super.setStats();

		stats.remove(Stat.health);
		stats.remove(Stat.armor);
		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.speed);
		stats.remove(Stat.range);
	}

	@Override
	public void drawBody(Unit unit){
		applyColor(unit);

		Drawf.spinSprite(region, unit.x, unit.y, unit.rotation - 90);

		Draw.reset();
	}
}
