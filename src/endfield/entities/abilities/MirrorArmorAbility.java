package endfield.entities.abilities;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import endfield.graphics.Draws;
import endfield.graphics.Layer2;
import endfield.graphics.Pal2;
import endfield.graphics.Shaders2;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class MirrorArmorAbility extends MirrorShieldAbility {
	protected static final int drawId = Draws.nextTaskId();
	protected static final int maskId = Draws.nextTaskId();
	//protected static final int fukId = Draws.nextTaskId();

	protected static final FrameBuffer drawBuffer = new FrameBuffer();
	protected static final FrameBuffer pingPongBuffer = new FrameBuffer();

	@Override
	public String getBundle() {
		return "ability.mirror-armor";
	}

	@Override
	public boolean shouldReflect(Unit unit, Bullet bullet) {
		return bullet.dst(unit) < unit.hitSize + bullet.vel.len() * 2 * Time.delta;
	}

	@Override
	public void eachNearBullets(Unit unit, Cons<Bullet> cons) {
		float nearRadius = unit.hitSize;
		Groups.bullet.intersect(unit.x - nearRadius, unit.y - nearRadius, nearRadius * 2, nearRadius * 2, b -> {
			if (unit.team != b.team) cons.get(b);
		});
	}

	@Override
	public void draw(Unit unit) {
		if (unit.shield <= 0) return;

		float z = Draw.z();

		if (Core.settings.getBool("animated-shields")) {
			Draw.z(Layer.shields - 5f);
			Draws.drawToBuffer(drawId, drawBuffer, unit, b -> {
				Shaders2.mirrorField.waveMix = Tmp.c1.set(Pal2.matrixNet);
				Shaders2.mirrorField.stroke = 1.2f;
				Shaders2.mirrorField.sideLen = 5;
				Shaders2.mirrorField.waveScl = 0.03f;
				Shaders2.mirrorField.gridStroke = 0.6f;
				Shaders2.mirrorField.maxThreshold = 1f;
				Shaders2.mirrorField.minThreshold = 0.7f;
				Shaders2.mirrorField.offset.set(Time.time * 0.1f, Time.time * 0.1f);

				pingPongBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
				pingPongBuffer.begin(Color.clear);
				Blending.disabled.apply();
				b.blit(Shaders2.mirrorField);
				Blending.normal.apply();
				pingPongBuffer.end();
			}, u -> {
				Draw.mixcol(Tmp.c1.set(u.team.color).lerp(Color.white, alpha), 1f);
				Draw.scl(1.1f);
				Draw.rect(u.type.shadowRegion, u.x, u.y, u.rotation - 90f);
				Draw.reset();
			});

			Draws.drawTask(maskId, unit, Shaders2.alphaMask, s -> Shaders2.alphaMask.texture = pingPongBuffer.getTexture(), u -> {
				Draw.color(Color.white, Math.max(alpha, Mathf.absin(6, 0.6f)));
				Draw.scl(1.15f);
				Draw.rect(u.type.shadowRegion, u.x, u.y, u.rotation - 90f);
				Draw.reset();
			});

			//Yes, this code doesn't do anything, but it won't work properly without this code
			//fu*k off arc GL
			/*Draws.drawTask(fukId, unit, u -> {}, (Unit u) -> Draw.draw(Draw.z(), () -> {
				pingPongBuffer.resize(2, 2);
				pingPongBuffer.begin(Color.clear);
				pingPongBuffer.end();
				pingPongBuffer.blit(Draw.getShader());
			}), u -> {});*/
		} else {
			Draw.z(Layer2.mirrorField + 1);
			Draw.mixcol(Tmp.c1.set(unit.team.color).lerp(Color.white, alpha), 1f);
			Draw.alpha(0.3f * Math.max(alpha, Mathf.absin(6, 0.6f)));
			Draw.scl(1.1f);
			Draw.rect(unit.type.shadowRegion, unit.x, unit.y, unit.rotation - 90);
			Draw.reset();
		}

		Draw.z(z);
		Draw.reset();
	}
}
