package heavyindustry.type.unit;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.gen.*;
import heavyindustry.graphics.Trails.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class SwordUnitType extends UnitTypef {
	public float travelSpeed = -1f, curveRnd = -1f;

	//Hit stuff
	public float baseY, tipY;
	public float damage = 1f;
	public Effect hitEffect = Fx.none;
	public StatusEffect status = StatusEffects.none;
	public float statusDuration = 10f * 60f;

	//Heat
	public float heatUpTime = 30f, cooldownTime = 60f;
	public Color heatColor = Color.red;
	public TextureRegion heatRegion;

	//Trail
	public float trailVel = 5f, trailInheritVel = 0.5f, trailAngle = 150f, trailY = Float.NEGATIVE_INFINITY;

	public SwordUnitType(String name) {
		super(name);
		aiController = FlyingAI::new;
		isEnemy = false;
		allowedInPayloads = logicControllable = playerControllable = false;
		envDisabled = 0;
		payloadCapacity = 0;

		hittable = targetable = physics = useUnitCap = false;
		flying = true;
		engineSize = -1;
		hidden = true;
		drawCell = false;
		engineLayer = Layer.effect;
		outlineColor = Pal.darkerMetal; //Block outline color

		speed = 8f;
		rotateSpeed = 3f;
		accel = 0.05f;
		drag = 0.03f;

		trailColor = Color.red.cpy().a(0.25f);
		trailLength = 5;
		trailScl = 2;
	}

	@Override
	public void display(Unit unit, Table table) {
		table.table(t -> {
			t.left();
			t.add(new Image(uiIcon)).size(iconMed).scaling(Scaling.fit);
			t.labelWrap(localizedName).left().width(190f).padLeft(5);
		}).growX().left();
	}

	@Override
	public void init() {
		super.init();

		if (!flying) flying = true;
		if (travelSpeed <= 0f) travelSpeed = speed / 2f;
		if (trailY == Float.NEGATIVE_INFINITY) trailY = tipY - trailScl;
		if (curveRnd < 0) curveRnd = rotateSpeed * 2;
	}

	@Override
	public void load() {
		super.load();

		heatRegion = Core.atlas.find(name + "-heat");
	}

	@Override
	public void drawBody(Unit unit) {
		super.drawBody(unit);

		if (heatRegion.found() && unit instanceof Swordc sword && sword.heat() > 0.01f) {
			Drawf.additive(heatRegion, heatColor.write(Tmp.c1).mulA(sword.heat()), unit.x, unit.y, unit.rotation - 90f, Draw.z());
		}
	}

	@Override
	public void drawTrail(Unit unit) {
		if (!(unit instanceof Swordc sword)) return;

		if (trailLength > 0) {
			if (sword.driftTrails() == null) {
				sword.driftTrails(new DriftTrail[]{
						new DriftTrail(trailLength),
						new DriftTrail(trailLength)
				});
			}

			for (DriftTrail trail : sword.driftTrails()) {
				trail.draw(trailColor == null ? unit.team.color : trailColor, trailScl);
			}
		}
	}

	@Override
	public void drawSoftShadow(float x, float y, float rotation, float alpha) {
		//I CAN'T SEE
	}
}
