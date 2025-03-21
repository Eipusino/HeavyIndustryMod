package heavyindustry.world.draw;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import heavyindustry.graphics.Draw3d;
import heavyindustry.graphics.Drawe;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class Draw3dSpin extends DrawBlock {
	protected static final Mat3D transformation = new Mat3D();
	protected static final FloatSeq transformationQueue = new FloatSeq();
	protected static final Vec2 tmpPixelOffset = new Vec2();
	protected static final Vec2 tmpRotatedBaseOffset = new Vec2();
	protected static final Quat tmpQuat1 = new Quat();
	protected static final Quat tmpQuat2 = new Quat();
	protected static final Mat3D tmpMat1 = new Mat3D();
	protected static final FrameBuffer shadowBuffer = new FrameBuffer();

	public static final Seq<Runnable> runs = new Seq<>();

	public static Cons<Runnable> proc;

	static {
		proc = (run) -> {
			if (run != null) runs.add(run);
		};

		Events.run(Trigger.draw, () -> {
			shadowBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			if (runs.isEmpty()) return;
			Seq<Runnable> buffer = runs.copy();
			runs.clear();

			Draw.draw(Layer.blockProp + 1, () -> {
				Draw.flush();
				shadowBuffer.begin(Color.clear);
				buffer.each(Runnable::run);
				shadowBuffer.end();
				Draw.color(Pal.shadow, Pal.shadow.a);
				Drawe.drawBuffer(shadowBuffer);
				Draw.flush();
				Draw.color();
			});
		});

		Events.run(Trigger.postDraw, () -> {
			transformationQueue.size = 0;
		});
	}

	public final Vec2 baseOffset = new Vec2();
	public final Vec3 scale = new Vec3(1, 1, 1);

	public float rotateSpeed = 4f;
	public float startRotationOffset = 90 + 90 + 45;
	public float pixelSize = 0.125f;

	public Floatf<Building> rotationProvider;
	public Vec3 axis = Vec3.Y;

	public float rotationAroundAxis = -45.0f;
	public int regionWidth = 6;
	public int shadowElevation = Vars.tilesize;

	public TextureRegion baseRegion, rotorRegion;
	public String baseSuffix, rotorSuffix;

	public Draw3dSpin(String base, String rotor) {
		baseSuffix = base;
		rotorSuffix = rotor;
	}

	public static void draw(Runnable run) {
		if (proc != null) proc.get(run);
	}

	protected static void setScale(float[] val, float x, float y, float z) {
		val[0] = x;
		val[5] = y;
		val[10] = z;
		val[15] = 1;
		for (int i = 0; i < 4; i++) {
			val[i + 1] = 0;
			val[i + 6] = 0;
			val[i + 11] = 0;
		}
	}

	public float baseRotation(Building building) {
		return rotationProvider == null ? 0f : (360 - (rotationProvider.get(building) % 360)) % 360f;
	}

	@SuppressWarnings("unchecked")
	public <T extends Building> Draw3dSpin rotationProvider(Floatf<T> rotationProvider) {
		this.rotationProvider = (Floatf<Building>) rotationProvider;
		return this;
	}

	@Override
	public void load(Block block) {
		baseRegion = Core.atlas.find(block.name + baseSuffix);
		rotorRegion = Core.atlas.find(block.name + rotorSuffix);
	}

	@Override
	public void draw(Building build) {
		float startZ = Draw.z();
		try {
			float realWidth = rotorRegion.width * rotorRegion.scl() * Draw.xscl;
			float realHeight = rotorRegion.height * rotorRegion.scl() * Draw.yscl;
			float baseRotation = baseRotation(build);

			float time = build.totalProgress() * rotateSpeed;
			float mainRotation = time % 90 + startRotationOffset;
			float subRotation = time % 90 - 90 + startRotationOffset;
			Mat baseRotationMatrix;

			{
				transformation.idt();
				float baseCos = Mathf.cosDeg(baseRotation);
				float baseSin = Mathf.sinDeg(baseRotation);
				baseRotationMatrix = Tmp.m1.setToRotation(Vec3.Z, baseCos, baseSin);

				Vec3 tmp = Tmp.v31;
				tmp.set(axis).mul(baseRotationMatrix);
				tmpQuat2.setFromAxis(tmp, rotationAroundAxis);

				tmpQuat1.setFromAxis(tmpQuat2.transform(tmp.set(0, 0, 1)), baseRotation);
				transformation.rotate(tmpQuat2);
				tmp.set(1f, 1f, 1f).sub(scale).mul(baseRotationMatrix);
				Mat3D mat3D = tmpMat1;
				setScale(mat3D.val, 1f - tmp.x * tmp.x, 1f - tmp.y * tmp.y, 1f - tmp.z * tmp.z);

				transformation.mul(mat3D);
				mat3D.idt();
				mat3D.rotate(Vec3.Z, baseRotation);
				transformation.mul(mat3D);
				baseRotationMatrix.scale(1f, -1f);
			}

			float alpha = Mathf.mod(time, 90) / 90f;
			baseRotation = Mathf.mod(baseRotation, 180f);
			final float finalBaseRotation = baseRotation;
			Vec2 pixelOffset = tmpPixelOffset.set(-pixelSize, 0).mul(baseRotationMatrix);
			Vec2 baseOffset = tmpRotatedBaseOffset.set(this.baseOffset).mul(baseRotationMatrix);

			int halfRegionWidth = regionWidth / 2;
			float drawX = build.x + baseOffset.x + pixelOffset.x * halfRegionWidth - realWidth / 2f;
			float drawY = build.y + baseOffset.y + pixelOffset.y * halfRegionWidth - realHeight / 2f;

			int myIndex = transformationQueue.size;
			transformationQueue.addAll(transformation.val);

			draw(() -> {
				Draw.color();
				System.arraycopy(transformationQueue.items, myIndex, transformation.val, 0, transformation.val.length);

				float localDrawX = drawX, localDrawY = drawY;
				for (int i = halfRegionWidth; i >= -halfRegionWidth; i--) {
					Draw3d.rect(transformation, rotorRegion, localDrawX - shadowElevation, localDrawY - shadowElevation, realWidth, realHeight, mainRotation);
					Draw3d.rect(transformation, rotorRegion, localDrawX - shadowElevation, localDrawY - shadowElevation, realWidth, realHeight, subRotation);
					localDrawX -= pixelOffset.x;
					localDrawY -= pixelOffset.y;
				}
				Lines.stroke(2);
				Draw.rect(baseRegion, build.x - shadowElevation, build.y - shadowElevation, -finalBaseRotation);
				Lines.line(build.x, build.y, build.x - shadowElevation, build.y - shadowElevation);
				Draw.color();
			});

			Draw.z(Layer.power + 0.1f);
			float a = Draw.getColor().a;
			Draw.rect(baseRegion, build.x, build.y, -finalBaseRotation);
			Draw.alpha(finalBaseRotation / 180f * a);
			Draw.rect(baseRegion, build.x, build.y, -finalBaseRotation - 180f);
			float localDrawX = drawX, localDrawY = drawY;
			for (int i = halfRegionWidth; i >= -halfRegionWidth; i--) {
				Draw.alpha(1f);
				Draw3d.rect(transformation, rotorRegion, localDrawX, localDrawY, realWidth, realHeight, mainRotation);
				Draw.alpha(alpha);
				Draw3d.rect(transformation, rotorRegion, localDrawX, localDrawY, realWidth, realHeight, subRotation);
				localDrawX -= pixelOffset.x;
				localDrawY -= pixelOffset.y;
			}
		} finally {
			Draw.z(startZ);
		}
	}
}
