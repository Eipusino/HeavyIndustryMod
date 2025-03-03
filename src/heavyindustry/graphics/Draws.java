package heavyindustry.graphics;

import arc.Core;
import arc.Events;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Angles;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Disposable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.func.Floatc3;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import java.util.Arrays;

import static heavyindustry.graphics.Drawn.c1;
import static heavyindustry.graphics.Drawn.v1;
import static heavyindustry.graphics.Drawn.v2;
import static heavyindustry.graphics.Drawn.v3;
import static heavyindustry.graphics.Drawn.v31;
import static heavyindustry.graphics.Drawn.v32;
import static heavyindustry.graphics.Drawn.v33;
import static heavyindustry.graphics.Drawn.v34;
import static heavyindustry.graphics.Drawn.v4;
import static heavyindustry.graphics.Drawn.v5;
import static heavyindustry.graphics.Drawn.v6;
import static heavyindustry.graphics.HShaders.MaskShader;
import static heavyindustry.graphics.HShaders.MirrorFieldShader;
import static heavyindustry.graphics.HShaders.dsv;
import static heavyindustry.graphics.HShaders.msf;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class Draws {
	public static final float mirrorField = 135f;
	public static final FrameBuffer effectBuffer = new FrameBuffer();

	public static final int sharedUnderBlockBloomId = nextTaskId();
	public static final int sharedUponFlyUnitBloomId = nextTaskId();
	public static final int sharedUnderFlyUnitBloomId = nextTaskId();

	private static final Rect rect = new Rect();
	private static DrawTask[] drawTasks = new DrawTask[16];
	private static FrameBuffer[] taskBuffer = new FrameBuffer[16];
	private static Bloom[] blooms = new Bloom[16];
	private static int idCount = 0;

	static {
		Events.run(Trigger.draw, () -> {
			Draw.draw(mirrorField - 0.01f, () -> {
				effectBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
				effectBuffer.begin(Color.clear);
			});
			Draw.draw(mirrorField + 0.51f, () -> {
				effectBuffer.end();

				HShaders.mirrorField.waveMix = Tmp.c1.set(HPal.matrixNet);
				HShaders.mirrorField.waveScl = 0.03f;
				HShaders.mirrorField.gridStroke = 0.8f;
				HShaders.mirrorField.maxThreshold = 1f;
				HShaders.mirrorField.minThreshold = 0.7f;
				HShaders.mirrorField.stroke = 2;
				HShaders.mirrorField.sideLen = 10;
				HShaders.mirrorField.offset.set(Time.time / 10, Time.time / 10);

				effectBuffer.blit(HShaders.mirrorField);
			});
		});
	}

	/** Don't let anyone instantiate this class. */
	private Draws() {}

	public static int nextTaskId() {
		if (idCount >= Integer.MAX_VALUE - 2) idCount = 0;
		return idCount++;
	}

	/**
	 * The task of publishing the cache and drawing it on the z-axis during the initial release, some of the parameters passed only have an effect during initialization and are selectively ignored afterwards.
	 *
	 * @param taskId	The identification ID of the task, used to distinguish the task cache.
	 * @param target	The data target passed to the drawing task is added to optimize the memory of lambda and avoid unnecessary memory usage caused by a large number of closure lambda instances.
	 * @param drawFirst <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, used to declare the operation that this task group needs to perform before execution.
	 * @param drawLast  <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, used to declare the operation that this task group will perform after completing the main drawing.
	 * @param draw	  The drawing task added to the task cache, which is the operation of this drawing.
	 */
	public static <T, D> void drawTask(int taskId, T target, D defTarget, DrawAcceptor<D> drawFirst, DrawAcceptor<D> drawLast, DrawAcceptor<T> draw) {
		while (taskId >= drawTasks.length) {
			drawTasks = Arrays.copyOf(drawTasks, drawTasks.length * 2);
		}

		DrawTask task = drawTasks[taskId];
		if (task == null) {
			task = drawTasks[taskId] = new DrawTask();
		}
		if (!task.init) {
			task.defaultFirstTask = drawFirst;
			task.defaultLastTask = drawLast;
			task.defaultTarget = defTarget;
			task.init = true;
			Draw.draw(Draw.z(), task::flush);
		}
		task.addTask(target, draw);
	}

	/**
	 * The task of publishing the cache and drawing it on the z-axis during the initial release, some of the parameters passed only have an effect during initialization and are selectively ignored afterwards.
	 *
	 * @param taskId	The identification ID of the task, used to distinguish the task cache.
	 * @param target	Handing over the data target for the drawing task, which was added to optimize the memory of lambda and avoid unnecessary memory usage caused by a large number of closure lambda instances.
	 * @param drawFirst <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, Used to declare the operations that this task group needs to perform before execution.
	 * @param drawLast  <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, Used to declare the operations to be performed by this task group after completing the main drawing.
	 * @param draw	  The drawing task added to the task cache, which is the operation of this drawing
	 */
	public static <T> void drawTask(int taskId, T target, DrawAcceptor<T> drawFirst, DrawAcceptor<T> drawLast, DrawAcceptor<T> draw) {
		drawTask(taskId, target, target, drawFirst, drawLast, draw);
	}

	/**
	 * The task of publishing the cache and drawing it on the z-axis during the initial release, some of the parameters passed only have an effect during initialization and are selectively ignored afterwards.
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param target Handing over the data target for the drawing task, which was added to optimize the memory of lambda and avoid unnecessary memory usage caused by a large number of closure lambda instances.
	 * @param shader <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, The shader used for drawing in this set of tasks.
	 * @param draw   The drawing task added to the task cache, which is the operation of this drawing
	 */
	public static <T, S extends Shader> void drawTask(int taskId, T target, S shader, DrawAcceptor<T> draw) {
		while (taskId >= taskBuffer.length) {
			taskBuffer = Arrays.copyOf(taskBuffer, taskBuffer.length * 2);
		}

		FrameBuffer buffer = taskBuffer[taskId];
		if (buffer == null) {
			buffer = taskBuffer[taskId] = new FrameBuffer();
		}
		FrameBuffer b = buffer;
		drawTask(taskId, target, shader, e -> {
			b.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			b.begin(Color.clear);
		}, e -> {
			b.end();
			b.blit(e);
		}, draw);
	}

	/**
	 * The task of publishing the cache and drawing it on the z-axis during the initial release, some of the parameters passed only have an effect during initialization and are selectively ignored afterwards.
	 *
	 * @param taskId	  The identification ID of the task, used to distinguish the task cache.
	 * @param target	  Handing over the data target for the drawing task, which was added to optimize the memory of lambda and avoid unnecessary memory usage caused by a large number of closure lambda instances.
	 * @param shader	  <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, The shader used for drawing in this set of tasks.
	 * @param applyShader <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, Operations performed on the colorimeter before drawing.
	 * @param draw		The drawing task added to the task cache, which is the operation of this drawing
	 */
	public static <T, S extends Shader> void drawTask(int taskId, T target, S shader, DrawAcceptor<S> applyShader, DrawAcceptor<T> draw) {
		drawTask(taskId, target, FrameBuffer::new, shader, applyShader, draw);
	}

	public static <T, S extends Shader> void drawTask(int taskId, T target, Prov<FrameBuffer> bufferProv, S shader, DrawAcceptor<S> applyShader, DrawAcceptor<T> draw) {
		while (taskId >= taskBuffer.length) {
			taskBuffer = Arrays.copyOf(taskBuffer, taskBuffer.length * 2);
		}

		if (taskBuffer[taskId] == null) {
			taskBuffer[taskId] = bufferProv.get();
		}
		drawTask(taskId, target, shader, e -> {
			taskBuffer[taskId].resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			taskBuffer[taskId].begin(Color.clear);
		}, e -> {
			taskBuffer[taskId].end();
			applyShader.draw(e);
			taskBuffer[taskId].blit(e);
		}, draw);
	}

	/**
	 * The task of publishing the cache and drawing it on the z-axis during the initial release, some of the parameters passed only have an effect during initialization and are selectively ignored afterwards.
	 * <p><strong>If the calling frequency of this method is very high and the lambda expression describing the drawing behavior needs to access local variables, then in order to optimize heap occupancy, please use{@link Draws#drawTask(int, Object, Shader, DrawAcceptor)}</strong>
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param shader <strong>Selective parameter, if the task has already been initialized, this parameter is invalid</strong>, The shader used for drawing in this set of tasks.
	 * @param draw   The drawing task added to the task cache, which is the operation of this drawing.
	 */
	public static void drawTask(int taskId, Shader shader, DrawDef draw) {
		drawTask(taskId, null, shader, draw);
	}

	/**
	 * Publish the task of caching and draw it on the z-axis during the initial release.
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param target Handing over the data target for the drawing task, which was added to optimize the memory of lambda and avoid unnecessary memory usage caused by a large number of closure lambda instances.
	 * @param draw   The drawing task added to the task cache, which is the operation of this drawing.
	 */
	public static <T> void drawTask(int taskId, T target, DrawAcceptor<T> draw) {
		while (taskId >= drawTasks.length) {
			drawTasks = Arrays.copyOf(drawTasks, drawTasks.length * 2);
		}

		DrawTask task = drawTasks[taskId];
		if (task == null) {
			task = drawTasks[taskId] = new DrawTask();
		}

		if (!task.init) {
			task.init = true;
			Draw.draw(Draw.z(), task::flush);
		}
		task.addTask(target, draw);
	}

	/**
	 * Publish the task of caching and draw it on the z-axis during the initial release.
	 * <p><strong>If the calling frequency of this method is very high and the lambda expression describing the drawing behavior needs to access local variables, then in order to optimize heap occupancy, please use{@link Draws#drawTask(int, Object, DrawAcceptor)}</strong>
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param draw   The drawing task added to the task cache, which is the operation of this drawing.
	 */
	public static void drawTask(int taskId, DrawDef draw) {
		while (taskId >= drawTasks.length) {
			drawTasks = Arrays.copyOf(drawTasks, drawTasks.length * 2);
		}

		DrawTask task = drawTasks[taskId];
		if (task == null) {
			task = drawTasks[taskId] = new DrawTask();
		}

		if (!task.init) {
			task.init = true;
			Draw.draw(Draw.z(), task::flush);
		}
		task.addTask(null, draw);
	}

	public static <T, B extends FrameBuffer> void drawToBuffer(int taskId, B buffer, T target, DrawAcceptor<T> draw) {
		drawToBuffer(taskId, buffer, target, b -> {
		}, draw);
	}

	public static <T, B extends FrameBuffer> void drawToBuffer(int taskId, B buffer, T target, DrawAcceptor<B> endBuffer, DrawAcceptor<T> draw) {
		drawTask(taskId, target, buffer, b -> {
			b.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			b.begin(Color.clear);
		}, b -> {
			b.end();
			endBuffer.draw(b);
		}, draw);
	}

	/**
	 * Publish a flood drawing task based on{@link Draws#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}implementation.
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param obj	The data object passed to the drawing task.
	 * @param draw   Draw task.
	 */
	public static <T> void drawBloom(int taskId, T obj, DrawAcceptor<T> draw) {
		while (taskId >= blooms.length) {
			blooms = Arrays.copyOf(blooms, blooms.length * 2);
		}

		Bloom bloom = blooms[taskId];
		if (bloom == null) {
			bloom = blooms[taskId] = new Bloom(true);
		}
		drawTask(taskId, obj, bloom, e -> {
			e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			e.setBloomIntensity(Core.settings.getInt("bloomintensity", 6) / 4f + 1f);
			e.blurPasses = Core.settings.getInt("bloomblur", 1);
			e.capture();
		}, Bloom::render, draw);
	}

	/** @see Draws#drawBloom(int, Object, DrawAcceptor) */
	public static void drawBloom(int taskId, DrawDef draw) {
		drawBloom(taskId, (DrawAcceptor<Bloom>) draw);
	}

	/** @see Draws#drawBloom(int, Object, DrawAcceptor) */
	public static void drawBloom(int taskId, DrawAcceptor<Bloom> draw) {
		while (taskId >= blooms.length) {
			blooms = Arrays.copyOf(blooms, blooms.length * 2);
		}

		Bloom bloom = blooms[taskId];
		if (bloom == null) {
			bloom = blooms[taskId] = new Bloom(true);
		}

		drawTask(taskId, bloom, e -> {
			e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			e.setBloomIntensity(Core.settings.getInt("bloomintensity", 6) / 4f + 1f);
			e.blurPasses = Core.settings.getInt("bloomblur", 1);
			e.capture();
		}, Bloom::render, draw);
	}

	/** @see Draws#drawBloomUnderBlock(Object, DrawAcceptor) */
	public static void drawBloomUnderBlock(DrawDef draw) {
		drawBloomUnderBlock(null, (DrawAcceptor<?>) draw);
	}

	/**
	 * Publish a flood drawing task in the shared flood drawing group, with the drawn layer located below the square({@link Layer#block}-1, 29)
	 * <p>Regarding the task of flood drawing, please refer to{@link Draws#drawBloom(int, Object, DrawAcceptor)}
	 *
	 * @param target The data object passed to the drawing task.
	 * @param draw   Draw task.
	 */
	public static <T> void drawBloomUnderBlock(T target, DrawAcceptor<T> draw) {
		float z = Draw.z();
		Draw.z(Layer.block + 1);
		drawBloom(sharedUnderBlockBloomId, target, draw);
		Draw.z(z);
	}

	/**
	 * @see Draws#drawBloomUponFlyUnit(Object, DrawAcceptor)
	 */
	public static void drawBloomUponFlyUnit(DrawDef draw) {
		drawBloomUponFlyUnit(null, draw);
	}

	/**
	 * Publish a flood drawing task in the shared flood drawing group, with the drawn layer located below the square({@link Layer#flyingUnit}+1, 116)
	 * <p>Regarding the task of flood drawing, please refer to{@link Draws#drawBloom(int, Object, DrawAcceptor)}
	 *
	 * @param target The data object passed to the drawing task.
	 * @param draw   Draw task.
	 */
	public static <T> void drawBloomUponFlyUnit(T target, DrawAcceptor<T> draw) {
		float z = Draw.z();
		Draw.z(Layer.flyingUnit + 1);
		drawBloom(sharedUponFlyUnitBloomId, target, draw);
		Draw.z(z);
	}

	/**
	 * @see Draws#drawBloomUnderFlyUnit(Object, DrawAcceptor)
	 */
	public static void drawBloomUnderFlyUnit(DrawDef draw) {
		drawBloomUnderFlyUnit(null, draw);
	}

	/**
	 * Publish a flood drawing task in the shared flood drawing group, with the drawn layer located below the low altitude unit(86, {@link Layer#flyingUnitLow}-1)
	 * <p>Regarding the task of flood drawing, please refer to{@link Draws#drawBloom(int, Object, DrawAcceptor)}
	 *
	 * @param target The data object passed to the drawing task.
	 * @param draw   Draw task.
	 */
	public static <T> void drawBloomUnderFlyUnit(T target, DrawAcceptor<T> draw) {
		float z = Draw.z();
		Draw.z(Layer.plans + 1);
		drawBloom(sharedUnderFlyUnitBloomId, target, draw);
		Draw.z(z);
	}

	/**
	 * Publish a distorted drawing task based on{@link Draws#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}implementation.
	 *
	 * @param taskId	 The identification ID of the task, used to distinguish the task cache.
	 * @param target	 The data object passed to the drawing task.
	 * @param distortion Twist drawing tool.
	 * @param draw	   Draw task.
	 */
	public static <T> void drawDistortion(int taskId, T target, Distortion distortion, DrawAcceptor<T> draw) {
		drawTask(taskId, target, distortion, e -> {
			e.resize();
			e.capture();
		}, Distortion::render, draw);
	}

	/**
	 * Publish a Gaussian fuzzy mask layer drawing task based on{@link Draws#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}implementation.
	 *
	 * @param taskId The identification ID of the task, used to distinguish the task cache.
	 * @param target The data object passed to the drawing task.
	 * @param blur   Blurring drawing objects.
	 * @param draw   Draw task.
	 */
	public static <T> void drawBlur(int taskId, T target, Blur blur, DrawAcceptor<T> draw) {
		drawTask(taskId, target, blur, e -> {
			e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			e.capture();
		}, Blur::render, draw);
	}

	public static <T> void drawMirrorField(int taskId, T target, DrawAcceptor<MirrorFieldShader> pre, DrawAcceptor<T> draw) {
		drawTask(taskId, target, HShaders.mirrorField, pre, draw);
	}

	public static <T> void drawMask(int taskID, MaskShader shader, GLFrameBuffer<? extends Texture> baseBuffer, T target, DrawAcceptor<T> draw) {
		drawTask(taskID, target, shader, s -> {
			baseBuffer.end();
			shader.texture = baseBuffer.getTexture();
		}, draw);
	}

	public static void drawTransform(float originX, float originY, Vec2 vec, float rotate, Floatc3 draw) {
		drawTransform(originX, originY, 0, vec.x, vec.y, rotate, draw);
	}

	public static void drawTransform(float originX, float originY, float dx, float dy, float rotate, Floatc3 draw) {
		drawTransform(originX, originY, 0, dx, dy, rotate, draw);
	}

	public static void drawTransform(float originX, float originY, float originAngle, float dx, float dy, float rotate, Floatc3 draw) {
		v1.set(dx, dy).rotate(rotate);
		draw.get(originX + v1.x, originY + v1.y, originAngle + rotate);
	}

	public static boolean clipDrawable(float x, float y, float clipSize) {
		Core.camera.bounds(rect);
		return rect.overlaps(x - clipSize / 2, y - clipSize / 2, clipSize, clipSize);
	}

	public static void drawLink(float origX, float origY, float othX, float othY, TextureRegion linkRegion, TextureRegion capRegion, float lerp) {
		drawLink(origX, origY, 0, othX, othY, 0, linkRegion, capRegion, lerp);
	}

	public static void drawLink(float origX, float origY, float offsetO, float othX, float othY, float offset, TextureRegion linkRegion, @Nullable TextureRegion capRegion, float lerp) {
		v1.set(othX - origX, othY - origY).setLength(offsetO);
		float ox = origX + v1.x;
		float oy = origY + v1.y;
		v1.scl(-1).setLength(offset);
		float otx = othX + v1.x;
		float oty = othY + v1.y;

		v1.set(otx, oty).sub(ox, oy);
		v2.set(v1).scl(lerp);
		v3.set(0, 0);

		if (capRegion != null) {
			v3.set(v1).setLength(capRegion.width / 4f);
			Draw.rect(capRegion, ox + v3.x / 2, oy + v3.y / 2, v2.angle());
			Draw.rect(capRegion, ox + v2.x - v3.x / 2, oy + v2.y - v3.y / 2, v2.angle() + 180);
		}

		Lines.stroke(8);
		Lines.line(linkRegion, ox + v3.x, oy + v3.y, ox + v2.x - v3.x, oy + v2.y - v3.y, false);
	}

	public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth) {
		Color color = Draw.getColor();
		drawDiamond(x, y, vertLength, vertWidth, 90, color, color);
		drawDiamond(x, y, horLength, horWidth, 0, color, color);
	}

	public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation) {
		Color color = Draw.getColor();
		drawDiamond(x, y, vertLength, vertWidth, 90 + rotation, color, color);
		drawDiamond(x, y, horLength, horWidth, 0 + rotation, color, color);
	}

	public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation, float gradientAlpha) {
		drawLightEdge(x, y, vertLength, vertWidth, horLength, horWidth, rotation, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
	}

	public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation, Color gradientTo) {
		Color color = Draw.getColor();
		drawDiamond(x, y, vertLength, vertWidth, 90 + rotation, color, gradientTo);
		drawDiamond(x, y, horLength, horWidth, 0 + rotation, color, gradientTo);
	}

	public static void drawLightEdge(float x, float y, Color color, float vertLength, float vertWidth, float rotationV, Color gradientV, float horLength, float horWidth, float rotationH, Color gradientH) {
		drawDiamond(x, y, vertLength, vertWidth, 90 + rotationV, color, gradientV);
		drawDiamond(x, y, horLength, horWidth, rotationH, color, gradientH);
	}

	public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float rotationV, float gradientV, float horLength, float horWidth, float rotationH, float gradientH) {
		Color color = Draw.getColor(), gradientColorV = color.cpy().a(gradientV), gradientColorH = color.cpy().a(gradientH);
		drawDiamond(x, y, vertLength, vertWidth, 90 + rotationV, color, gradientColorV);
		drawDiamond(x, y, horLength, horWidth, rotationH, color, gradientColorH);
	}

	public static void drawDiamond(float x, float y, float length, float width, float rotation) {
		drawDiamond(x, y, length, width, rotation, Draw.getColor());
	}

	public static void drawDiamond(float x, float y, float length, float width, float rotation, float gradientAlpha) {
		drawDiamond(x, y, length, width, rotation, Draw.getColor(), gradientAlpha);
	}

	public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color) {
		drawDiamond(x, y, length, width, rotation, color, 1);
	}

	public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color, float gradientAlpha) {
		drawDiamond(x, y, length, width, rotation, color, Tmp.c1.set(color).a(gradientAlpha));
	}

	public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color, Color gradient) {
		v1.set(length / 2, 0).rotate(rotation);
		v2.set(0, width / 2).rotate(rotation);

		float originColor = color.toFloatBits();
		float gradientColor = gradient.toFloatBits();

		Fill.quad(x, y, originColor, x, y, originColor, x + v1.x, y + v1.y, gradientColor, x + v2.x, y + v2.y, gradientColor);
		Fill.quad(x, y, originColor, x, y, originColor, x + v1.x, y + v1.y, gradientColor, x - v2.x, y - v2.y, gradientColor);
		Fill.quad(x, y, originColor, x, y, originColor, x - v1.x, y - v1.y, gradientColor, x + v2.x, y + v2.y, gradientColor);
		Fill.quad(x, y, originColor, x, y, originColor, x - v1.x, y - v1.y, gradientColor, x - v2.x, y - v2.y, gradientColor);
	}

	public static void drawCrystal(float x, float y, float length, float width, float height, float centOffX, float centOffY, float edgeStoke, float edgeLayer, float botLayer, float crystalRotation, float rotation, Color color, Color edgeColor) {
		v31.set(length / 2, 0, 0);
		v32.set(0, width / 2, 0).rotate(Vec3.X, crystalRotation);
		v33.set(centOffX, centOffY, height / 2).rotate(Vec3.X, crystalRotation);

		float w1, w2;
		float widthReal = Math.max(w1 = Math.abs(v32.y), w2 = Math.abs(v33.y));

		v31.rotate(Vec3.Z, -rotation);
		v32.rotate(Vec3.Z, -rotation);
		v33.rotate(Vec3.Z, -rotation);

		float z = Draw.z();
		Draw.z(botLayer);
		Draw.color(color);

		float mx = Angles.trnsx(rotation + 90, widthReal), my = Angles.trnsy(rotation + 90, widthReal);
		Fill.quad(x + v31.x, y + v31.y, x + mx, y + my, x - v31.x, y - v31.y, x - mx, y - my);

		if (edgeStoke > 0.01f && edgeColor.a > 0.01) {
			Lines.stroke(edgeStoke, edgeColor);
			crystalEdge(x, y, w1 >= widthReal, v32.z > v33.z, edgeLayer, botLayer, v32);
			crystalEdge(x, y, w2 >= widthReal, v33.z > v32.z, edgeLayer, botLayer, v33);
		}

		Draw.z(z);
	}

	private static void crystalEdge(float x, float y, boolean w, boolean r, float edgeLayer, float botLayer, Vec3 v) {
		Draw.z(r || w ? edgeLayer : botLayer - 0.01f);

		Lines.line(x + v.x, y + v.y, x + v31.x, y + v31.y);
		Lines.line(x + v.x, y + v.y, x - v31.x, y - v31.y);

		Draw.z(!r || w ? edgeLayer : botLayer - 0.01f);

		Lines.line(x - v.x, y - v.y, x + v31.x, y + v31.y);
		Lines.line(x - v.x, y - v.y, x - v31.x, y - v31.y);
	}

	public static void drawCornerTri(float x, float y, float rad, float cornerLen, float rotate, boolean line) {
		drawCornerPoly(x, y, rad, cornerLen, 3, rotate, line);
	}

	public static void drawCornerPoly(float x, float y, float rad, float cornerLen, float sides, float rotate, boolean line) {
		float step = 360 / sides;

		if (line) Lines.beginLine();
		for (int i = 0; i < sides; i++) {
			v1.set(rad, 0).setAngle(step * i + rotate);
			v2.set(v1).rotate90(1).setLength(cornerLen);

			if (line) {
				Lines.linePoint(x + v1.x - v2.x, y + v1.y - v2.y);
				Lines.linePoint(x + v1.x + v2.x, y + v1.y + v2.y);
			} else Fill.tri(x, y, x + v1.x - v2.x, y + v1.y - v2.y, x + v1.x + v2.x, y + v1.y + v2.y);
		}
		if (line) Lines.endLine(true);
	}

	public static void drawHaloPart(float x, float y, float width, float len, float rotate) {
		drawHaloPart(x, y, width * 0.2f, len * 0.7f, width, len * 0.3f, rotate);
	}

	public static void drawHaloPart(float x, float y, float interWidth, float interLen, float width, float len, float rotate) {
		Drawf.tri(x, y, interWidth, interLen, rotate + 180);
		Drawf.tri(x, y, width, len, rotate);
	}

	public static void gradientTri(float x, float y, float length, float width, float rotation) {
		gradientTri(x, y, length, width, rotation, Draw.getColor());
	}

	public static void gradientTri(float x, float y, float length, float width, float rotation, float gradientAlpha) {
		gradientTri(x, y, length, width, rotation, Draw.getColor(), gradientAlpha);
	}

	public static void gradientTri(float x, float y, float length, float width, float rotation, Color color) {
		gradientTri(x, y, length, width, rotation, color, color);
	}

	public static void gradientTri(float x, float y, float length, float width, float rotation, Color color, float gradientAlpha) {
		gradientTri(x, y, length, width, rotation, color, Tmp.c1.set(color).a(gradientAlpha));
	}

	public static void gradientTri(float x, float y, float length, float width, float rotation, Color color, Color gradient) {
		v1.set(length / 2, 0).rotate(rotation);
		v2.set(0, width / 2).rotate(rotation);

		float originColor = color.toFloatBits();
		float gradientColor = gradient.toFloatBits();

		Fill.quad(x, y, originColor, x, y, originColor, x + v1.x, y + v1.y, gradientColor, x + v2.x, y + v2.y, gradientColor);
		Fill.quad(x, y, originColor, x, y, originColor, x + v1.x, y + v1.y, gradientColor, x - v2.x, y - v2.y, gradientColor);
	}

	public static void gradientCircle(float x, float y, float radius, Color gradientColor) {
		gradientCircle(x, y, radius, x, y, gradientColor);
	}

	public static void gradientCircle(float x, float y, float radius, float gradientAlpha) {
		gradientCircle(x, y, radius, x, y, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
	}

	public static void gradientCircle(float x, float y, float radius, float offset, float gradientAlpha) {
		gradientCircle(x, y, radius, x, y, offset, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
	}

	public static void gradientCircle(float x, float y, float radius, float offset, Color gradientColor) {
		gradientCircle(x, y, radius, x, y, offset, gradientColor);
	}

	public static void gradientCircle(float x, float y, float radius, float gradientCenterX, float gradientCenterY, Color gradientColor) {
		gradientCircle(x, y, radius, gradientCenterX, gradientCenterY, -radius, gradientColor);
	}

	public static void gradientCircle(float x, float y, float radius, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor) {
		gradientPoly(x, y, Lines.circleVertices(radius), radius, Draw.getColor(), gradientCenterX, gradientCenterY, offset, gradientColor, 0);
	}

	public static void gradientSqrt(float x, float y, float radius, float rotation, float offset, Color gradientColor) {
		gradientSqrt(x, y, radius, x, y, offset, gradientColor, rotation);
	}

	public static void gradientSqrt(float x, float y, float radius, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor, float rotation) {
		gradientPoly(x, y, 4, 1.41421f * (radius / 2), Draw.getColor(), gradientCenterX, gradientCenterY, offset, gradientColor, rotation);
	}

	public static void gradientPoly(float x, float y, int edges, float radius, Color color, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor, float rotation) {
		gradientFan(x, y, edges, radius, color, gradientCenterX, gradientCenterY, offset, gradientColor, 360, rotation);
	}

	public static void drawFan(float x, float y, float radius, float fanAngle, float rotation) {
		gradientFan(x, y, radius, Draw.getColor().a, fanAngle, rotation);
	}

	public static void gradientFan(float x, float y, float radius, float gradientAlpha, float fanAngle, float rotation) {
		gradientFan(x, y, radius, -radius, gradientAlpha, fanAngle, rotation);
	}

	public static void gradientFan(float x, float y, float radius, float offset, float gradientAlpha, float fanAngle, float rotation) {
		gradientFan(x, y, radius, offset, c1.set(Draw.getColor()).a(gradientAlpha), fanAngle, rotation);
	}

	public static void gradientFan(float x, float y, float radius, float offset, Color gradientColor, float fanAngle, float rotation) {
		gradientFan(x, y, Lines.circleVertices(radius), radius, Draw.getColor(), x, y, offset, gradientColor, fanAngle, rotation);
	}

	public static void gradientFan(float x, float y, float radius, Color color, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor, float fanAngle, float rotation) {
		gradientFan(x, y, Lines.circleVertices(radius), radius, color, gradientCenterX, gradientCenterY, offset, gradientColor, fanAngle, rotation);
	}

	public static void gradientFan(float x, float y, int edges, float radius, Color color, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor, float fanAngle, float rotation) {
		fanAngle = Mathf.clamp(fanAngle, 0, 360);

		v1.set(gradientCenterX - x, gradientCenterY - y).rotate(rotation);
		gradientCenterX = x + v1.x;
		gradientCenterY = y + v1.y;

		v1.set(1, 0).setLength(radius).rotate(rotation - fanAngle % 360 / 2);
		float step = fanAngle / edges;

		float lastX = -1, lastY = -1;
		float lastGX = -1, lastGY = -1;

		for (int i = 0; i < edges + (fanAngle == 360 ? 1 : 0); i++) {
			v1.setAngle(i * step + rotation - fanAngle % 360 / 2);
			v2.set(v1).sub(gradientCenterX - x, gradientCenterY - y);

			if (lastX != -1) {
				v3.set(v2).setLength(offset).scl(offset < 0 ? -1 : 1);
				v4.set(lastGX, lastGY).setLength(offset).scl(offset < 0 ? -1 : 1);
				Fill.quad(lastX, lastY, color.toFloatBits(), x + v1.x, y + v1.y, color.toFloatBits(), gradientCenterX + v2.x + v3.x, gradientCenterY + v2.y + v3.y, gradientColor.toFloatBits(), gradientCenterX + lastGX + v4.x, gradientCenterY + lastGY + v4.y, gradientColor.toFloatBits());
			}

			lastX = x + v1.x;
			lastY = y + v1.y;
			lastGX = v2.x;
			lastGY = v2.y;
		}
	}

	public static void arc(float x, float y, float radius, float innerAngel, float rotate) {
		dashCircle(x, y, radius, 1, innerAngel, rotate);
	}

	public static void dashCircle(float x, float y, float radius) {
		dashCircle(x, y, radius, 0);
	}

	public static void dashCircle(float x, float y, float radius, float rotate) {
		dashCircle(x, y, radius, 1.8f, 6, 180, rotate);
	}

	public static void dashCircle(float x, float y, float radius, int dashes, float totalDashDeg, float rotate) {
		dashCircle(x, y, radius, 1.8f, dashes, totalDashDeg, rotate);
	}

	public static void dashCircle(float x, float y, float radius, float scaleFactor, int dashes, float totalDashDeg, float rotate) {
		int sides = 40 + (int) (radius * scaleFactor);
		if (sides % 2 == 1) sides++;

		v1.set(0, 0);
		float per = totalDashDeg < 0 ? -360f / sides : 360f / sides;
		totalDashDeg = Math.min(Math.abs(totalDashDeg), 360);

		float rem = 360 - totalDashDeg;
		float dashDeg = totalDashDeg / dashes;
		float empDeg = rem / dashes;

		for (int i = 0; i < sides; i++) {
			if (i * Math.abs(per) % (dashDeg + empDeg) > dashDeg) continue;

			v1.set(radius, 0).setAngle(rotate + per * i + 90);
			float x1 = v1.x;
			float y1 = v1.y;

			v1.set(radius, 0).setAngle(rotate + per * (i + 1) + 90);

			Lines.line(x1 + x, y1 + y, v1.x + x, v1.y + y);
		}
	}

	public static void drawLaser(float originX, float originY, float otherX, float otherY, TextureRegion linkRegion, TextureRegion capRegion, float stoke) {
		float rot = Mathf.angle(otherX - originX, otherY - originY);

		if (capRegion != null) Draw.rect(capRegion, otherX, otherY, rot);

		Lines.stroke(stoke);
		Lines.line(linkRegion, originX, originY, otherX, otherY, capRegion != null);
	}

	public static void gradientLine(float originX, float originY, float targetX, float targetY, Color origin, Color target, int gradientDir) {
		float halfWidth = Lines.getStroke() / 2;
		v1.set(halfWidth, 0).rotate(Mathf.angle(targetX - originX, targetY - originY) + 90);

		float c1, c2, c3, c4;
		switch (gradientDir) {
			case 0 -> {
				c1 = origin.toFloatBits();
				c2 = origin.toFloatBits();
				c3 = target.toFloatBits();
				c4 = target.toFloatBits();
			}
			case 1 -> {
				c1 = target.toFloatBits();
				c2 = origin.toFloatBits();
				c3 = origin.toFloatBits();
				c4 = target.toFloatBits();
			}
			case 2 -> {
				c1 = target.toFloatBits();
				c2 = target.toFloatBits();
				c3 = origin.toFloatBits();
				c4 = origin.toFloatBits();
			}
			case 3 -> {
				c1 = origin.toFloatBits();
				c2 = target.toFloatBits();
				c3 = target.toFloatBits();
				c4 = origin.toFloatBits();
			}
			default -> {
				return;
			}

		}

		Fill.quad(originX + v1.x, originY + v1.y, c1, originX - v1.x, originY - v1.y, c2, targetX - v1.x, targetY - v1.y, c3, targetX + v1.x, targetY + v1.y, c4);
	}

	public static void oval(float x, float y, float horLen, float vertLen, float rotation, float offset, Color gradientColor) {
		int sides = Lines.circleVertices(Math.max(horLen, vertLen));
		float step = 360f / sides;

		float c1 = Draw.getColor().toFloatBits();
		float c2 = gradientColor.toFloatBits();

		for (int i = 0; i < sides; i++) {
			float dx = horLen * Mathf.cosDeg(i * step);
			float dy = vertLen * Mathf.sinDeg(i * step);
			float dx1 = horLen * Mathf.cosDeg((i + 1) * step);
			float dy1 = vertLen * Mathf.sinDeg((i + 1) * step);

			v1.set(dx, dy).setAngle(rotation);
			v2.set(dx1, dy1).setAngle(rotation);
			v3.set(v1).setLength(v1.len() + offset);
			v4.set(v2).setLength(v2.len() + offset);

			Fill.quad(x + v1.x, y + v1.y, c1, x + v2.x, y + v2.y, c1, x + v4.x, y + v4.y, c2, x + v3.x, y + v3.y, c2);
		}
	}

	public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight, float cycRadius, float cycRotation, float rotation) {
		drawRectAsCylindrical(x, y, rowWidth, rowHeight, cycRadius, cycRotation, rotation, Draw.getColor());
	}

	public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight, float cycRadius, float cycRotation, float rotation, Color color) {
		drawRectAsCylindrical(x, y, rowWidth, rowHeight, cycRadius, cycRotation, rotation, color, color, Draw.z(), Draw.z() - 0.01f);
	}

	public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight, float cycRadius, float cycRotation, float rotation, Color color, Color dark, float lightLayer, float darkLayer) {
		if (rowWidth >= 2 * Mathf.pi * cycRadius) {
			v1.set(cycRadius, rowHeight).rotate(rotation);
			Draw.color(color);
			float z = Draw.z();
			Draw.z(lightLayer);
			Fill.quad(x + v1.x, y - v1.y, x + v1.x, y + v1.y, x - v1.x, y + v1.y, x - v1.x, y - v1.y);
			Draw.z(z);
			return;
		}

		cycRotation = Mathf.mod(cycRotation, 360);

		float phaseDiff = 180 * rowWidth / (Mathf.pi * cycRadius);
		float rot = cycRotation + phaseDiff;

		v31.set(cycRadius, rowHeight / 2, 0).rotate(Vec3.Y, cycRotation);
		v33.set(v31);
		v32.set(cycRadius, rowHeight / 2, 0).rotate(Vec3.Y, rot);
		v34.set(v32);

		if (cycRotation < 180) {
			if (rot > 180) v33.set(-cycRadius, rowHeight / 2, 0);
			if (rot > 360) v34.set(cycRadius, rowHeight / 2, 0);
		} else {
			if (rot > 360) v33.set(cycRadius, rowHeight / 2, 0);
			if (rot > 540) v34.set(-cycRadius, rowHeight / 2, 0);
		}

		float z = Draw.z();
		// A to C
		drawArcPart(v31.z > 0, color, dark, lightLayer, darkLayer, x, y, v31, v33, rotation);

		// B to D
		drawArcPart(v34.z > 0, color, dark, lightLayer, darkLayer, x, y, v32, v34, rotation);

		// C to D
		drawArcPart((v33.z > 0 && v34.z > 0) || (Mathf.zero(v33.z) && Mathf.zero(v34.z) && v31.z < 0 && v32.z < 0) || (Mathf.zero(v33.z) && v34.z > 0) || (Mathf.zero(v34.z) && v33.z > 0), color, dark, lightLayer, darkLayer, x, y, v33, v34, rotation);

		Draw.z(z);
		Draw.reset();
	}

	private static void drawArcPart(boolean light, Color colorLight, Color darkColor, float layer, float darkLayer, float x, float y, Vec3 vec1, Vec3 vec2, float rotation) {
		if (light) {
			Draw.color(colorLight);
			Draw.z(layer);
		} else {
			Draw.color(darkColor);
			Draw.z(darkLayer);
		}

		v1.set(vec1.x, vec1.y).rotate(rotation);
		v2.set(vec2.x, vec2.y).rotate(rotation);
		v3.set(vec1.x, -vec1.y).rotate(rotation);
		v4.set(vec2.x, -vec2.y).rotate(rotation);

		Fill.quad(x + v3.x, y + v3.y, x + v1.x, y + v1.y, x + v2.x, y + v2.y, x + v4.x, y + v4.y);
	}

	public static void gapTri(float x, float y, float width, float length, float insideLength, float rotation) {
		v1.set(0, width / 2).rotate(rotation);
		v2.set(length, 0).rotate(rotation);
		v3.set(insideLength, 0).rotate(rotation);

		Fill.quad(x + v1.x, y + v1.y, x + v2.x, y + v2.y, x + v3.x, y + v3.y, x + v1.x, y + v1.y);
		Fill.quad(x - v1.x, y - v1.y, x + v2.x, y + v2.y, x + v3.x, y + v3.y, x - v1.x, y - v1.y);
	}

	public interface DrawAcceptor<T> {
		void draw(T accept);
	}

	public interface DrawDef extends DrawAcceptor {
		@Override
		default void draw(Object accept) {
			draw();
		}

		void draw();
	}

	private static class DrawTask {
		protected Object defaultTarget;
		protected DrawAcceptor<?>[] tasks = new DrawAcceptor<?>[16];
		protected Object[] dataTarget = new Object[16];

		DrawAcceptor<?> defaultFirstTask, defaultLastTask;
		int taskCounter;
		boolean init;

		public <T> void addTask(T dataAcceptor, DrawAcceptor<T> task) {
			if (tasks.length <= taskCounter) {
				tasks = Arrays.copyOf(tasks, tasks.length + 1);
				dataTarget = Arrays.copyOf(dataTarget, tasks.length);
			}

			tasks[taskCounter] = task;
			dataTarget[taskCounter++] = dataAcceptor;
		}

		public void flush() {
			if (defaultFirstTask != null) ((DrawAcceptor) defaultFirstTask).draw(defaultTarget);

			for (int i = 0; i < taskCounter; i++) {
				((DrawAcceptor) tasks[i]).draw(dataTarget[i]);
			}

			if (defaultLastTask != null) ((DrawAcceptor) defaultLastTask).draw(defaultTarget);

			taskCounter = 0;
			init = false;
		}
	}

	public static class Distortion implements Disposable {
		static final FrameBuffer tmpBuffer = new FrameBuffer();

		Shader distortion;
		FrameBuffer buffer;
		boolean capturing, disposed;

		public Distortion() {
			distortion = new Shader(dsv("screenspace"), msf("distortion"));

			buffer = new FrameBuffer();

			init();
		}

		public static void drawVoidDistortion(float x, float y, float radius, float len) {
			drawVoidDistortion(x, y, radius, len, true, Lines.circleVertices(radius));
		}

		public static void drawVoidDistortion(float x, float y, float radius, float len, boolean inside) {
			drawVoidDistortion(x, y, radius, len, inside, Lines.circleVertices(radius));
		}

		public static void drawVoidDistortion(float x, float y, float radius, float len, boolean inside, int sides) {
			v1.set(radius, 0);
			v2.set(radius, 0);
			v3.set(radius + len, 0);
			v4.set(radius + len, 0);
			v5.set(inside ? -1 : 1, 0);
			v6.set(inside ? -1 : 1, 0);

			float step = 360f / sides;
			for (int i = 0; i < sides; i++) {
				v1.setAngle(step * i);
				v2.setAngle(step * (i + 1));
				v3.setAngle(step * i);
				v4.setAngle(step * (i + 1));
				v5.setAngle(step * i);
				v6.setAngle(step * (i + 1));

				float cf1 = c1.set((v5.x + 1) / 2, (v5.y + 1) / 2, inside ? 1 : 0, inside ? 1 : 0).toFloatBits();
				float cf2 = c1.set((v6.x + 1) / 2, (v6.y + 1) / 2, inside ? 1 : 0, inside ? 1 : 0).toFloatBits();
				float cf3 = c1.set((v5.x + 1) / 2, (v5.y + 1) / 2, inside ? 0 : 1, inside ? 0 : 1).toFloatBits();
				float cf4 = c1.set((v6.x + 1) / 2, (v6.y + 1) / 2, inside ? 0 : 1, inside ? 0 : 1).toFloatBits();

				Fill.quad(x + v1.x, y + v1.y, cf1, x + v2.x, y + v2.y, cf2, x + v4.x, y + v4.y, cf4, x + v3.x, y + v3.y, cf3);
			}
		}

		public void init() {
			distortion.bind();
			distortion.setUniformi("u_texture0", 0);
			distortion.setUniformi("u_texture1", 1);
			distortion.setUniformf("strength", -64);

			resize();
		}

		public void resize() {
			buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		}

		public void capture() {
			if (capturing) return;

			capturing = true;
			buffer.begin(Color.clear);
		}

		public void render() {
			capturing = false;

			buffer.end();

			tmpBuffer.resize(buffer.getWidth(), buffer.getHeight());
			ScreenSampler.getToBuffer(tmpBuffer, false);
			tmpBuffer.getTexture().bind(1);
			distortion.bind();
			distortion.setUniformf("width", buffer.getWidth());
			distortion.setUniformf("height", buffer.getHeight());
			buffer.blit(distortion);
		}

		public void setStrength(float strength) {
			distortion.bind();
			distortion.setUniformf("strength", strength);
		}

		@Override
		public void dispose() {
			buffer.dispose();
			distortion.dispose();
			disposed = true;
		}

		@Override
		public boolean isDisposed() {
			return disposed;
		}
	}

	public static class Blur {
		public static final float[] DEf_A = {
				0.0086973240159f, 0.0359949776755f, 0.1093610049784f,
				0.2129658870149f, 0.2659615230194f, 0.2129658870149f,
				0.1093610049784f, 0.0359949776755f, 0.0086973240159f,
		};
		public static final float[] DEf_B = {
				0.0444086447005f, 0.0779944219933f, 0.1159966211046f,
				0.1673080561213f, 0.1885769121606f, 0.1673080561213f,
				0.1159966211046f, 0.0779944219933f, 0.0444086447005f,
		};
		public static final float[] DEf_C = {
				0.0045418484119f, 0.0539998665132f, 0.2419867245191f,
				0.3989431211116f,
				0.2419867245191f, 0.0539998665132f, 0.0045418484119f,
		};
		public static final float[] DEf_D = {
				0.0245418484119f, 0.0639998665132f, 0.2519867245191f,
				0.3189431211116f,
				0.2519867245191f, 0.0639998665132f, 0.0245418484119f,
		};
		public static final float[] DEf_E = {
				0.019615710072f, 0.2054255182127f,
				0.5599175434306f,
				0.2054255182127f, 0.019615710072f,
		};
		public static final float[] DEf_F = {
				0.0702702703f, 0.3162162162f,
				0.2270270270f,
				0.3162162162f, 0.0702702703f,
		};
		public static final float[] DEf_G = {
				0.2079819330264f,
				0.6840361339472f,
				0.2079819330264f,
		};
		public static final float[] DEf_H = {
				0.2561736558128f,
				0.4876526883744f,
				0.2561736558128f,
		};
		public int blurScl = 4;
		public float blurSpace = 2.16f;
		Shader blurShader;
		FrameBuffer buffer, pingpong;
		boolean capturing;

		public Blur() {
			this(DEf_F);
		}

		public Blur(float... convolutions) {
			blurShader = genShader(convolutions);

			buffer = new FrameBuffer();
			pingpong = new FrameBuffer();

			blurShader.bind();
			blurShader.setUniformi("u_texture0", 0);
			blurShader.setUniformi("u_texture1", 1);
		}

		private Shader genShader(float... convolutions) {
			if (convolutions.length % 2 != 1) return null;

			int convLen = convolutions.length;

			StringBuilder varying = new StringBuilder();
			StringBuilder assignVar = new StringBuilder();
			StringBuilder convolution = new StringBuilder();

			int c = 0;
			int half = convLen / 2;
			for (float v : convolutions) {
				varying.append("varying vec2 v_texCoords").append(c).append(";").append(System.lineSeparator());

				assignVar.append("v_texCoords").append(c).append(" = ").append("a_texCoord0");
				if (c - half != 0)
					assignVar.append(c - half > 0 ? "+" : "-").append(Math.abs(c - half)).append(" * len");

				assignVar.append(";").append(System.lineSeparator()).append("  ");

				if (c > 0) convolution.append("		+ ");
				convolution.append(v).append(" * texture2D(u_texture1, v_texCoords").append(c).append(")").append(".rgb").append(System.lineSeparator());

				c++;
			}
			convolution.append(";");

			String vertexShader = """
					attribute vec4 a_position;
					attribute vec2 a_texCoord0;
					
					uniform vec2 dir;
					uniform vec2 size;
					
					varying vec2 v_texCoords;
					%varying%
					void main() {
						vec2 len = dir/size;
					
						v_texCoords = a_texCoord0;
						%assignVar%
						gl_Position = a_position;
					}
					""".replace("%varying%", varying).replace("%assignVar%", assignVar);
			String fragmentShader = """
					uniform lowp sampler2D u_texture0;
					uniform lowp sampler2D u_texture1;
					
					uniform lowp float def_alpha;
					
					varying vec2 v_texCoords;
					%varying%
					void main() {
						vec4 blur = texture2D(u_texture0, v_texCoords);
						vec3 color = texture2D(u_texture1, v_texCoords).rgb;
					
						if (blur.a > 0.0) {
							vec3 blurColor =
								%convolution%
					
							gl_FragColor.rgb = mix(color, blurColor, blur.a);
							gl_FragColor.a = 1.0;
						} else {
							gl_FragColor.rgb = color;
							gl_FragColor.a = def_alpha;
						}
					}
					""".replace("%varying%", varying).replace("%convolution%", convolution);

			return new Shader(vertexShader, fragmentShader);
		}

		public void resize(int width, int height) {
			width /= blurScl;
			height /= blurScl;

			buffer.resize(width, height);
			pingpong.resize(width, height);

			blurShader.bind();
			blurShader.setUniformf("size", width, height);
		}

		public void capture() {
			if (!capturing) {
				buffer.begin(Color.clear);

				capturing = true;
			}
		}

		public void render() {
			if (!capturing) return;
			capturing = false;
			buffer.end();

			Gl.disable(Gl.blend);
			Gl.disable(Gl.depthTest);
			Gl.depthMask(false);

			pingpong.begin();
			blurShader.bind();
			blurShader.setUniformf("dir", blurSpace, 0f);
			blurShader.setUniformi("def_alpha", 1);
			ScreenSampler.getSampler().bind(1);
			buffer.blit(blurShader);
			pingpong.end();

			blurShader.bind();
			blurShader.setUniformf("dir", 0f, blurSpace);
			blurShader.setUniformf("def_alpha", 0);
			pingpong.getTexture().bind(1);

			Gl.enable(Gl.blend);
			Gl.blendFunc(Gl.srcAlpha, Gl.oneMinusSrcAlpha);
			buffer.blit(blurShader);
		}

		public void directDraw(Runnable draw) {
			resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			capture();
			draw.run();
			render();
		}
	}

	public static final class ScreenSampler {
		private static FrameBuffer samplerBuffer = new FrameBuffer(), pingpong = new FrameBuffer();
		private static boolean setup = false;

		private ScreenSampler() {}

		/**
		 * Load Events for ScreenSampler.
		 * If you try to load it a second time, nothing will happen.
		 */
		public static void setup() {
			if (setup) return;

			Events.run(Trigger.preDraw, () -> flush(false));
			Events.run(Trigger.postDraw, () -> end(false));

			Events.run(Trigger.uiDrawBegin, () -> flush(true));
			Events.run(Trigger.uiDrawEnd, () -> end(true));
			setup = true;
		}

		/** @return Has whether set up. */
		public static boolean isSetup() {
			return setup;
		}

		private static void end(boolean blit) {
			samplerBuffer.end();
			if (blit) samplerBuffer.blit(HShaders.baseShader);
		}

		private static void flush(boolean legacy) {
			if (legacy) {
				getToBuffer(pingpong, true);
				FrameBuffer buffer = samplerBuffer;
				samplerBuffer = pingpong;
				pingpong = buffer; //swap
			}

			samplerBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			if (legacy) samplerBuffer.begin();
			else samplerBuffer.begin(Color.clear);
		}

		/**
		 * Get the current screen texture. The texture object is a reference or mapping of the current screen texture, which will change synchronously with the rendering process. Do not use this object to temporarily store screen data.
		 *
		 * @return Reference object for screen sampling texture.
		 */
		public static Texture getSampler() {
			Draw.flush();
			return samplerBuffer.getTexture();
		}

		/**
		 * Transfer the current screen texture to a{@linkplain FrameBuffer frame buffer}, This will become a copy that can be used to temporarily store screen content.
		 *
		 * @param target Target buffer for transferring screen textures.
		 * @param clear  Is the frame buffer cleared before transferring.
		 */
		public static void getToBuffer(FrameBuffer target, boolean clear) {
			if (clear) target.begin(Color.clear);
			else target.begin();

			samplerBuffer.blit(HShaders.baseShader);
			target.end();
		}
	}

	public static final class MathRenderer {
		public static final FrameBuffer buffer = new FrameBuffer();

		public static MathShader sinShader;

		public static MathShader ovalShader;

		public static MathShader curveCircle;

		static float dispersion = 0.02f;
		static float minThreshold = 0.01f;
		static float maxThreshold = 1;
		static float sclX = 1;
		static float sclY = 1;

		static TextureRegion blank;

		private MathRenderer() {}

		private static TextureRegion getBlank() {
			if (blank == null) {
				Pixmap pix = new Pixmap(128, 128);
				pix.fill(Color.white);
				blank = new TextureRegion(new Texture(pix));
			}

			return blank;
		}

		public static void load() {
			sinShader = new MathShader(1, 2,
					"y - sin(x * arg0 + arg1)",
					"sqrt(1.0 + pow(arg0 * cos(x * arg0 + arg1), 2.0))",
					"float", "float"
			);

			ovalShader = new MathShader(
					"x * x * arg0 + y * y * arg1 - arg2",
					"sqrt(4.0 * pow(arg0 * x, 2.0) + 4.0 * pow(arg1 * y, 2.0))",
					"float", "float", "float"
			);

			curveCircle = new MathShader(
					"float arctan = arg2 * atan(y / x);\nfloat costan = cos(arctan);", 2, 2,
					"x * x + y * y - arg0 - arg1 * sin(arctan)",
					"sqrt(pow((x * costan + 2.0 * y * y * y + 2.0 * x * x * y) / (x * x + y * y), 2.0) + pow((2.0 * x * x * x + 2.0 * x * y * y + arg1 * arg2 * y * costan) / (x * x + y * y), 2.0))",
					"float", "float", "float"
			);
		}

		public static void setDispersion(float dis) {
			dispersion = dis;
		}

		public static void setThreshold(float minThr, float maxThr) {
			minThreshold = minThr;
			maxThreshold = maxThr;
		}

		public static void drawSin(float x1, float y1, float x2, float y2, float max, float scl, float fine) {
			sinShader.setScl(scl * Mathf.degRad, 1);
			sinShader.setArg(0, 1f);
			sinShader.setArg(1, fine * Mathf.degRad);

			Draw.shader(sinShader);
			Lines.stroke(max * 4);
			Lines.line(getBlank(), x1, y1, x2, y2, false);
			Draw.shader();
		}

		public static void drawSin(float x1, float y1, float stoke1, float x2, float y2, float stoke2, float scl, float fine) {
			sinShader.setScl(scl * Mathf.degRad, 1);
			sinShader.setArg(0, 1f);
			sinShader.setArg(1, fine * Mathf.degRad);

			stoke1 *= 2;
			stoke2 *= 2;

			Draw.shader(sinShader);
			Tmp.v1.set(x2 - x1, y2 - y1);
			Tmp.v2.set(Tmp.v1).rotate90(1).setLength(stoke1);
			Tmp.v3.set(Tmp.v1).rotate90(1).setLength(stoke2);
			Fill.quad(getBlank(),
					x1 + Tmp.v2.x, y1 + Tmp.v2.y,
					x1 - Tmp.v2.x, y1 - Tmp.v2.y,
					x1 + Tmp.v1.x - Tmp.v3.x, y1 + Tmp.v1.y - Tmp.v3.y,
					x1 + Tmp.v1.x + Tmp.v3.x, y1 + Tmp.v1.y + Tmp.v3.y
			);
			Draw.shader();
		}

		public static void drawCos(float x1, float y1, float x2, float y2, float max, float omiga, float fine) {
			drawSin(x1, y1, x2, y2, max, omiga, fine + 90);
		}

		public static void drawCircle(float x, float y, float radius) {
			ovalShader.setScl(radius, radius);
			ovalShader.setArg(0, 1f);
			ovalShader.setArg(1, 1f);
			ovalShader.setArg(2, radius * radius);

			Draw.shader(ovalShader);
			float r = radius * 4;
			Draw.rect(getBlank(), x, y, r, r);
			Draw.shader();
		}

		// Actually, it seems like scaling directly is fine, but the width of the lines will be affected, so we still implemented a function drawing tool for ellipses.
		public static void drawOval(float x, float y, float horizon, float vert, float rotation) {
			float max = Math.max(horizon, vert);
			ovalShader.setScl(max, max);
			ovalShader.setArg(0, Mathf.pow(1 / horizon, 2));
			ovalShader.setArg(1, Mathf.pow(1 / vert, 2));
			ovalShader.setArg(2, 1f);

			Draw.shader(ovalShader);
			float r = max * 4;
			Draw.rect(getBlank(), x, y, r, r, rotation);
			Draw.shader();
		}

		public static void drawCurveCircle(float x, float y, float radius, int sinMulti, float scl, float rotate) {
			curveCircle.setScl(radius, radius);
			curveCircle.setArg(0, radius * radius);
			curveCircle.setArg(1, scl);
			curveCircle.setArg(2, sinMulti * 2f);

			Draw.shader(curveCircle);
			Draw.rect(getBlank(), x, y, radius * 4, radius * 4, rotate);
			Draw.shader();
		}

		public static class MathShader extends Shader {
			private static final String vert = """
				uniform mat4 u_projTrans;
				
				attribute vec4 a_position;
				attribute vec2 a_texCoord0;
				attribute vec4 a_color;
				
				varying vec4 v_color;
				varying vec2 v_texCoords;
				
				void main() {
					gl_Position = u_projTrans * a_position;
					v_texCoords = a_texCoord0;
					v_color = a_color;
				}
				""";

			private static final String frag = """
				#define HIGHP
				
				uniform sampler2D u_texture;
				uniform float dispersion;
				
				uniform float maxThreshold;
				uniform float minThreshold;
				
				uniform float sclX;
				uniform float sclY;
				
				%args%
				
				varying vec4 v_color;
				varying vec2 v_texCoords;
				
				void main() {
					float x = (v_texCoords.x - 0.5) * %wScl% * sclX;
					float y = (v_texCoords.y - 0.5) * %hScl% * sclY;
				
					vec4 c = texture2D(u_texture, v_texCoords);
					vec4 mixed = v_color*c;
				
					%perVar%
				
					float gradMod = %gradMod%;
					float alpha = dispersion * gradMod / (abs(%fx%) + dispersion * gradMod);
				
					alpha = max(min((alpha - minThreshold)/(maxThreshold - minThreshold), 1.0), 0.0) * mixed.a;
				
					gl_FragColor = vec4(mixed.r, mixed.g, mixed.b, alpha);
				}
				""";

			public final Object[] argsArr;
			public final String function;

			public MathShader(String function, String gradMod, String... argTypes) {
				this(2, 2, function, gradMod, argTypes);
			}

			public MathShader(float widthScl, float heightScl, String function, String gradMod, String... argTypes) {
				this("", widthScl, heightScl, function, gradMod, argTypes);
			}

			public MathShader(String perVar, float widthScl, float heightScl, String func, String gradMod, String... argTypes) {
				super(vert, genFrag(perVar, widthScl, heightScl, func, gradMod, argTypes));
				function = func;
				argsArr = new Object[argTypes.length];
			}

			public static String genFrag(String perVar, float widthScl, float heightScl, String function, String gradMod, String... argTypes) {
				return frag.replace("%fx%", function)
						.replace("%perVar%", perVar)
						.replace("%gradMod%", gradMod)
						.replace("%args%", genArgList(argTypes))
						.replace("%wScl%", Float.toString(widthScl * 2f))
						.replace("%hScl%", Float.toString(heightScl * 2f));
			}

			private static String genArgList(String... argsCount) {
				StringBuilder res = new StringBuilder();
				for (int i = 0; i < argsCount.length; i++) {
					res.append("uniform ")
							.append(argsCount[i])
							.append(" arg")
							.append(i)
							.append(";")
							.append(System.lineSeparator());
				}

				return res.toString();
			}

			public void setArg(int index, Object value) {
				argsArr[index] = value;
			}

			public void setScl(float sX, float sY) {
				sclX = sX;
				sclY = sY;
			}

			@Override
			public void apply() {
				super.apply();

				bind();
				setUniformf("dispersion", dispersion);

				setUniformf("minThreshold", minThreshold);
				setUniformf("maxThreshold", maxThreshold);
				setUniformf("sclX", sclX);
				setUniformf("sclY", sclY);

				for (int i = 0; i < argsArr.length; i++) {
					Object o = argsArr[i];
					if (o instanceof Float f) {
						setUniformf("arg" + i, f);
					} else if (o instanceof Integer in) {
						setUniformi("arg" + i, in);
					} else if (o instanceof Vec2 v) {
						setUniformf("arg" + i, v);
					} else if (o instanceof Vec3 v) {
						setUniformf("arg" + i, v);
					} else if (o instanceof Mat m) {
						setUniformMatrix("arg" + i, m);
					} else throw new IllegalArgumentException("invalid type: " + o.getClass());
				}
			}
		}
	}
}
