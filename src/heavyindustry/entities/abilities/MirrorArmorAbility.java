package heavyindustry.entities.abilities;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class MirrorArmorAbility extends BaseMirrorShieldAbility {
	protected static final int drawId = Draws.nextTaskId();
	protected static final int maskId = Draws.nextTaskId();
	protected static final int fukId = Draws.nextTaskId();

	protected static final FrameBuffer drawBuffer = new FrameBuffer();
	protected static final FrameBuffer pingPongBuffer = new FrameBuffer();

	@Override
	public String localized() {
		return Core.bundle.get("ability.mirror-armor");
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

		if (Core.settings.getBool("hi-animated-shields")) {
			Draw.z(Layer.shields - 2f);
			Draws.drawToBuffer(drawId, drawBuffer, unit, b -> {
				HIShaders.mirrorField.waveMix = Tmp.c1.set(HIPal.matrixNet);
				HIShaders.mirrorField.stroke = 1.2f;
				HIShaders.mirrorField.sideLen = 5;
				HIShaders.mirrorField.waveScl = 0.03f;
				HIShaders.mirrorField.gridStroke = 0.6f;
				HIShaders.mirrorField.maxThreshold = 1f;
				HIShaders.mirrorField.minThreshold = 0.7f;
				HIShaders.mirrorField.offset.set(Time.time * 0.1f, Time.time * 0.1f);

				pingPongBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
				pingPongBuffer.begin(Color.clear);
				Blending.disabled.apply();
				b.blit(HIShaders.mirrorField);
				Blending.normal.apply();
				pingPongBuffer.end();
			}, e -> {
				Draw.mixcol(Tmp.c1.set(e.team.color).lerp(Color.white, alpha), 1f);
				Draw.scl(1.1f);
				Draw.rect(e.type.shadowRegion, e.x, e.y, e.rotation - 90);
				Draw.reset();
			});

			Draws.drawTask(maskId, unit, HIShaders.alphaMask, s -> HIShaders.alphaMask.texture = pingPongBuffer.getTexture(), e -> {
				Draw.color(Color.white, Math.max(alpha, Mathf.absin(6, 0.6f)));
				Draw.scl(1.15f);
				Draw.rect(e.type.shadowRegion, e.x, e.y, e.rotation - 90);
				Draw.reset();
			});

			//Yes, this code doesn't do anything, but it won't work properly without this code
			//fu*k off arc GL
			Draws.drawTask(fukId, unit, u -> {}, (Unit u) -> Draw.draw(Draw.z(), () -> {
				pingPongBuffer.resize(2, 2);
				pingPongBuffer.begin(Color.clear);
				pingPongBuffer.end();
				pingPongBuffer.blit(Draw.getShader());
			}), u -> {});
		} else {
			Draw.z(Draws.mirrorField + 1);
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
