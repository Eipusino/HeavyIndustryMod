package heavyindustry.entities.effect;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import heavyindustry.graphics.HTrails.LightTrail;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.world.blocks.defense.turrets.BaseTurret.BaseTurretBuild;

/**
 * A particle with a trail that falls into the center.
 * When spawning, input the radius into the rotation param.
 * Inputting a negative value makes the particle swirl counter-clockwise instead of clockwise.
 */
public class SwirlEffect extends Effect {
	/** How many points long the trail is. */
	public int length = 8;
	/** Radius of the trail. */
	public float width = 3;
	/** How much the particle will revolve around the center in degrees. */
	public float minRot = 120, maxRot = 480;
	/**
	 * If set to values >= 0, the radius will be a random amount between minDst and maxDst,
	 * however the sign of the input is still used to determine rotation direction.
	 */
	public float minDst = -1f, maxDst = -1f;
	/** If < 0, don't emit light. */
	public float lightOpacity = 0.8f;
	/** If null, uses effect color. */
	public @Nullable Color colorFrom;
	/** If null, uses effect color. */
	public @Nullable Color colorTo;

	public Interp fallterp = Interp.pow2Out;
	public Interp spinterp = Interp.pow3Out;
	/** Overrides spin direction from radius provided by bullet rotation. >1 for clockwise, <1 for counter-clockwise */
	public float spinDirectionOverride = 0f;

	public SwirlEffect(float lifetime, float clipsize, Color colorFrom, int length, float width, float minRot, float maxRot, float minDst, float maxDst) {
		this();
		this.lifetime = lifetime;
		this.clip = clipsize;
		this.colorFrom = colorFrom;
		this.length = length;
		this.width = width;
		this.minRot = minRot;
		this.maxRot = maxRot;
		this.minDst = minDst;
		this.maxDst = maxDst;
	}

	public SwirlEffect(float lifetime, Color colorFrom, int length, float width, float minRot, float maxRot, float minDst, float maxDst) {
		this(lifetime, 400f, colorFrom, length, width, minRot, maxRot, minDst, maxDst);
	}

	public SwirlEffect(float lifetime, int length, float width, float minRot, float maxRot) {
		this(lifetime, Color.black, length, width, minRot, maxRot, -1, -1);
	}

	public SwirlEffect(Color colorFrom) {
		this();
		this.colorFrom = colorFrom;
	}

	public SwirlEffect() {
		super();
		lifetime = 90f;
		followParent = rotWithParent = true;
		layer = Layer.effect + 0.005f; //Black doesn't work with bloom so draw slightly above the bloom layer by default.
	}

	public SwirlEffect setInterps(Interp fallterp, Interp spinterp) {
		this.fallterp = fallterp;
		this.spinterp = spinterp;
		return this;
	}

	public SwirlEffect setInterps(Interp interp) {
		return setInterps(interp, interp);
	}

	@Override
	public void init() {
		clip = Math.max(clip, maxDst * 2f);
	}

	@Override
	public void render(EffectContainer e) {
		float lifetime = e.lifetime - length;
		float dst;
		if (minDst < 0 || maxDst < 0) {
			dst = Math.abs(e.rotation);
		} else {
			dst = Mathf.randomSeed(e.id, minDst, maxDst);
		}
		float l = Mathm.clamp(e.time / lifetime);
		if (colorFrom != null || colorTo != null) {
			Tmp.c1.set(colorFrom == null ? e.color : colorFrom).lerp(colorTo == null ? e.color : colorTo, l);
		} else {
			Tmp.c1.set(e.color);
		}

		float width = Mathm.clamp(e.time / (e.lifetime - length)) * this.width;
		float dir = spinDirectionOverride != 0 ? Mathf.sign(spinDirectionOverride) : Mathf.sign(e.rotation);
		float baseRot = Mathf.randomSeed(e.id + 1, 360f), addRot = Mathf.randomSeed(e.id + 2, minRot, maxRot) * dir;

		Trail trail = (Trail) e.data;
		if (!Vars.state.isPaused()) {
			float f = 1f - (e.time / lifetime);
			if (f > 0f) {
				Tmp.v1.trns(baseRot + addRot * spinterp.apply(f), Mathf.maxZero(dst * fallterp.apply(f))).add(e.x, e.y);
				trail.update(Tmp.v1.x, Tmp.v1.y);
			} else {
				trail.shorten();
			}
		}

		trail.drawCap(Tmp.c1, width);

		if (trail instanceof LightTrail lightTrail) {
			lightTrail.draw(Tmp.c1, width, l);
		} else {
			trail.draw(Tmp.c1, width);
		}
	}

	@Override
	protected void add(float x, float y, float rotation, Color color, Object data) {
		BlackHoleEffectState state = BlackHoleEffectState.createBlackHole();
		state.effect = this;
		state.rotation = baseRotation + rotation;
		state.lifetime = lifetime;
		state.set(x, y);
		state.color.set(color);
		if (followParent && data instanceof Posc posc) {
			state.parent = posc;
			state.rotWithParent = rotWithParent;
		}
		state.data = lightOpacity > 0f ? new LightTrail(length, lightOpacity) : new Trail(length);
		state.add();
	}

	public static class BlackHoleEffectState extends EffectState {
		public static BlackHoleEffectState createBlackHole() {
			return Pools.obtain(BlackHoleEffectState.class, BlackHoleEffectState::new);
		}

		@Override
		public void add() {
			if (!added) {
				index__all = Groups.all.addIndex(this);
				index__draw = Groups.draw.addIndex(this);
				if (parent != null) {
					offsetX = x - parent.x();
					offsetY = y - parent.y();
					if (rotWithParent) {
						if (parent instanceof Rotc r) {
							offsetPos = -r.rotation();
							offsetRot = rotation - r.rotation();
						} else if (parent instanceof BaseTurretBuild build) {
							offsetPos = -build.rotation;
							offsetRot = rotation - build.rotation;
						}
					}
				}

				added = true;
			}
		}

		@Override
		public void update() {
			followParent:
			if (parent != null) {
				if (rotWithParent) {
					if (parent instanceof Rotc r) {
						x = parent.x() + Angles.trnsx(r.rotation() + offsetPos, offsetX, offsetY);
						y = parent.y() + Angles.trnsy(r.rotation() + offsetPos, offsetX, offsetY);
						//Do not change rotation. It is used for radius.
						break followParent;
					} else if (parent instanceof BaseTurretBuild build) {
						x = parent.x() + Angles.trnsx(build.rotation + offsetPos, offsetX, offsetY);
						y = parent.y() + Angles.trnsy(build.rotation + offsetPos, offsetX, offsetY);
						//Do not change rotation. It is used for radius.
						break followParent;
					}
				}

				x = parent.x() + offsetX;
				y = parent.y() + offsetY;
			}

			time = Math.min(time + Time.delta, lifetime);
			if (time >= lifetime) {
				remove();
			}
		}
	}
}
