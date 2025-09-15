package heavyindustry.entities.effect;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.geom.Rect;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.world.blocks.defense.turrets.BaseTurret.BaseTurretBuild;

import static mindustry.Vars.headless;

public class ParentEffect extends Effect {
	public ParentEffect(float life, Cons<EffectContainer> renderer) {
		super(life, renderer);
	}

	public ParentEffect(float life, float clipSize, Cons<EffectContainer> renderer) {
		super(life, clipSize, renderer);
	}

	public static void create(Effect effect, float x, float y, float rotation, Color color, Object data) {
		if (headless || effect == Fx.none) return;
		if (Core.settings.getBool("effects")) {
			Rect view = Core.camera.bounds(Tmp.r1);
			Rect pos = Tmp.r2.setSize(effect.clip).setCenter(x, y);

			if (view.overlaps(pos)) {
				ParentEffectState entity = createState();
				entity.effect = effect;
				entity.rotation = rotation;
				entity.originalRotation = rotation;
				entity.data = (data);
				entity.lifetime = (effect.lifetime);
				entity.set(x, y);
				entity.color.set(color);
				float rotationA = 0f;
				if (data instanceof Rotc rotc) {
					rotationA = rotc.rotation();
				} else if (data instanceof BaseTurretBuild turret) {
					rotationA = turret.rotation;
				}
				if (data instanceof Posc posc) {
					entity.parent = posc;
					//entity.positionRotation = (posc.angleTo(entity) - rotation);
					entity.positionRotation = (posc.angleTo(entity) - rotationA);
				}
				entity.add();
			}
		}
	}

	public static ParentEffectState createState() {
		return Pools.obtain(ParentEffectState.class, ParentEffectState::new);
	}

	@Override
	public void at(float x, float y, float rotation, Object data) {
		at(x, y, rotation, Color.white, data);
	}

	@Override
	public void at(float x, float y, float rotation, Color color, Object data) {
		create(this, x, y, rotation, color, data);
	}

	public static class ParentEffectState extends EffectState {
		public float originalRotation = 0f;
		public float positionRotation = 0f;

		@Override
		public void update() {
			super.update();

			if (parent != null) {
				float rot = 0f;
				if (parent instanceof Rotc rotc) {
					rot = rotc.rotation();
				} else if (parent instanceof BaseTurretBuild turret) {
					rot = turret.rotation;
				}
				rotation = rot - originalRotation;
				//float angle = Mathf.angle(offsetX, offsetY);
				float len = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
				Tmp.v1.trns(rot - positionRotation, len).add(parent);
				x = Tmp.v1.x;
				y = Tmp.v1.y;
			}
		}
	}
}
