package heavyindustry.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mat;
import arc.math.Rand;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import mindustry.entities.Effect;
import mindustry.entities.Effect.EffectContainer;

/**
 * Scene effects tool, a layout tool used to display {@link Effect} in the UI.
 * <p>This tool has been pooled and uses internal static methods to obtain and add special effect instances,
 * which will be automatically reclaimed after playback.
 *
 * <p>The tool uses the {@linkplain Effect fx} commonly used in the world for display,
 * Generally speaking, the pixel length ratio between world coordinates and screen coordinates is 1:4.
 * When using special effects written for in-game environments, it is recommended to provide a 4x magnification ratio:
 * <pre>{@code
 * SceneEffect effect = SceneEffect.showOnStage(Fx.regenSuppressParticle, x, y);
 * effect.scaleX = 4;
 * effect.scaleY = 4;
 * }</pre>
 */
public class SceneEffect extends Element implements Poolable {
	protected static final Rand idRand = new Rand();

	protected final EffectContainer container = new EffectContainer();
	protected final Mat transform = new Mat(), last = new Mat();
	protected final Color tmpColor = new Color();

	public int id;
	public Effect effect;
	public float lifetime;
	public float time;
	public Object data;

	/// Don't let anyone instantiate this class.
	private SceneEffect() {}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnStage(Effect fx, float x, float y) {
		return showOnStage(fx, x, y, 0, Color.white, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation) {
		return showOnStage(fx, x, y, rotation, Color.white, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Color color) {
		return showOnStage(fx, x, y, rotation, color, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Object data) {
		return showOnStage(fx, x, y, rotation, Color.white, data);
	}

	/**
	 * Add a special effect element to the root UI element using the given effects and parameters,
	 * with the coordinate system being the screen coordinates and the origin being the bottom left corner of the screen.
	 *
	 * @param fx	   The displayed target effect
	 * @param x		The x display coordinates of the effect
	 * @param y		The y display coordinates of the effect
	 * @param rotation Rotation angle of effect
	 * @param color	Color parameters of effect
	 * @param data	 Effect parameter data
	 */
	public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Color color, Object data) {
		SceneEffect e = Pools.obtain(SceneEffect.class, SceneEffect::new);
		Core.scene.add(e);

		return setDefAttr(fx, x, y, rotation, color, data, e);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y) {
		return showOnGroup(target, fx, x, y, 0, Color.white, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation) {
		return showOnGroup(target, fx, x, y, rotation, Color.white, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Color color) {
		return showOnGroup(target, fx, x, y, rotation, color, null);
	}

	/** @see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
	public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Object data) {
		return showOnGroup(target, fx, x, y, rotation, Color.white, data);
	}

	/**
	 * Add a special effect element to the target display container using the given effects and parameters,
	 * with coordinates in the local coordinate system of the target parent element and the origin in the lower left corner of the container boundary.
	 *
	 * @param target   The parent container displayed by the effect
	 * @param fx	   The displayed target effect
	 * @param x		The x-display coordinate offset of the effect
	 * @param y		The y-display coordinate offset of the effect
	 * @param rotation Rotation angle of effect
	 * @param color	Color parameters of effect
	 * @param data	 Effect parameter data
	 */
	public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Color color, Object data) {
		SceneEffect e = Pools.obtain(SceneEffect.class, SceneEffect::new);
		target.addChild(e);

		return setDefAttr(fx, x, y, rotation, color, data, e);
	}

	protected static SceneEffect setDefAttr(Effect fx, float x, float y, float rotation, Color color, Object data, SceneEffect e) {
		idRand.setSeed(System.nanoTime());
		e.id = idRand.nextInt();
		e.effect = fx;
		e.lifetime = fx.lifetime;
		e.data = data;
		e.time = 0f;
		e.setPosition(x, y, Align.center);
		e.setRotation(rotation);
		e.color.set(color);

		return e;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		time += Time.delta;
		if (time > lifetime) {
			remove();
		}
	}

	@Override
	public void draw() {
		super.draw();
		transform.set(last.set(Draw.trans()));
		transform.translate(x + width / 2, y + height / 2);
		transform.scale(Scl.scl(scaleX), Scl.scl(scaleY));

		Draw.reset();
		Draw.trans(transform);
		container.set(id, tmpColor.set(color).a(color.a * parentAlpha), time, lifetime, rotation, 0, 0, data);
		effect.render(container);
		Draw.reset();
		Draw.trans(last);
	}

	@Override
	public boolean remove() {
		boolean b = super.remove();
		if (b) Pools.free(this);
		return b;
	}

	@Override
	public void reset() {
		effect = null;
		lifetime = 0;
		time = 0;
		data = null;
		width = 0;
		height = 0;
		scaleX = 1;
		scaleY = 1;
		color.set(Color.white);
		x = 0;
		y = 0;
		rotation = 0;
		id = 0;
	}
}
