package heavyindustry.entities.part;

import arc.graphics.g2d.Draw;
import arc.math.geom.Vec2;
import heavyindustry.math.Mathm;
import heavyindustry.util.CollectionList;
import mindustry.entities.part.DrawPart;

import java.util.List;

public class CustomPart extends DrawPart {
	protected final Vec2 vec = new Vec2();
	protected final Vec2 vec2 = new Vec2();

	public Drawer draw = (x, y, rotation, progress) -> {};
	public List<PartMove> moves = new CollectionList<>(PartMove.class);
	public PartProgress progress = HPartProgress.one;

	public float layer = -1;
	public float x, y, drawRadius, rotation;
	public float moveX, moveY, drawRadiusTo, moveRot;
	public boolean mirror;

	@Override
	public void draw(PartParams params) {
		float prog = Mathm.clamp(progress.get(params));
		float z = Draw.z();

		float dx = 0, dy = 0, dr = 0;

		for (int i = 0; i < moves.size(); i++) {
			PartMove move = moves.get(i);

			dx += move.x * move.progress.get(params);
			dy += move.y * move.progress.get(params);
			dr += move.rot * move.progress.get(params);
		}

		float rot = rotation + moveRot * prog + dr;
		vec.set(x + moveX * prog + dx, y + moveY * prog + dy).rotate(params.rotation - 90);
		vec2.set(drawRadius + (drawRadiusTo - drawRadius) * prog, 0).setAngle(rot).rotate(params.rotation);

		float drawX = vec.x + vec2.x;
		float drawY = vec.y + vec2.y;

		if (layer >= 0) Draw.z(layer);
		draw.draw(params.x + drawX, params.y + drawY, params.rotation + rot, prog);

		if (mirror) {
			vec.setAngle(2 * params.rotation - vec.angle());
			vec2.setAngle(-rot).rotate(params.rotation);
			drawX = vec.x + vec2.x;
			drawY = vec.y + vec2.y;
			draw.draw(params.x + drawX, params.y + drawY, params.rotation - rot, prog);
		}
		Draw.z(z);
	}

	@FunctionalInterface
	public interface Drawer {
		void draw(float x, float y, float rotation, float progress);
	}
}
