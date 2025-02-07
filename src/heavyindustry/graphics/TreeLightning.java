package heavyindustry.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/**
 * @author Ovulam5480
 * @since 1.0.6
 */
public final class TreeLightning {
	public static final Effect chainLightning = new Effect(40f, 300f, e -> {
		if (!(e.data instanceof Position p)) return;
		float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
		Tmp.v1.set(p).sub(e.x, e.y).nor();

		float normx = Tmp.v1.x, normy = Tmp.v1.y;
		float range = 6f;
		int links = Mathf.ceil(dst / range);
		float spacing = dst / links;

		Lines.stroke(5f * e.fout() * e.rotation);
		Draw.color(Color.white, e.color, e.fin());

		Lines.beginLine();

		Lines.linePoint(e.x, e.y);

		Mathf.rand.setSeed(e.id);

		for (int i = 0; i < links; i++) {
			float nx, ny;
			if (i == links - 1) {
				nx = tx;
				ny = ty;
			} else {
				float len = (i + 1) * spacing;
				Tmp.v1.setToRandomDirection(Mathf.rand).scl(range / 2f);
				nx = e.x + normx * len + Tmp.v1.x;
				ny = e.y + normy * len + Tmp.v1.y;
			}

			Lines.linePoint(nx, ny);
		}

		Lines.endLine();
	}).followParent(false).rotWithParent(false);

	private static final Rand random = new Rand();
	private static final IntSet hit = new IntSet();
	private static final float hitRange = 30f;

	public static float change = 0.4f;
	public static float spreadSpeed = 8;

	private static boolean bhit = false;

	private TreeLightning() {}

	public static void create(float x, float y, Team team, float damage, int amount, float range) {
		create(x, y, team, Palf.surgeYellow, Palf.surgeYellow, damage, amount, range);
	}

	public static void create(Bullet bullet, float damage, int amount, float range) {
		create(bullet, Palf.surgeYellow, Palf.surgeYellow, damage, amount, range);
	}

	public static void create(float x, float y, Team team, Color roofColor, Color leafColor, float damage, int amount, float range) {
		Bullet bullet = Bullets.damageLightning.create(null, team, x, y, 0, 1, 600);
		create(bullet, x, y, team, roofColor, leafColor, damage, amount, range, Mathf.random(360), 30);
	}

	public static void create(Bullet bullet, Color roofColor, Color leafColor, float damage, int amount, float range) {
		create(bullet, bullet.x, bullet.y, bullet.team, roofColor, leafColor, damage, amount, range, Mathf.random(360), 30);
	}

	public static void create(Bullet bullet, float x, float y, Team team, Color roofColor, Color leafColor, float damage, int amount, float range, float rotation, float angle) {
		for (int i = 0; i < amount; i++) {
			float rotation1 = (float) 360 / amount * i + rotation;
			Seq<Vec2> points = createLightningInternal(bullet, bullet.id + i, team, roofColor,
					damage, x, y, rotation1, (int) range, 0.85f);

			points.each(vec2 -> Time.run(vec2.dst(x, y) / spreadSpeed, () -> {
				float indexFin = (float) points.indexOf(vec2) / points.size;
				if (indexFin > 0.2f && indexFin < 0.9f && Mathf.chance(change * (1 - indexFin))) {

					float angle1 = Mathf.angle(vec2.x - x, vec2.y - y);

					createLightningInternal(bullet, bullet.id, team, leafColor, damage, vec2.x, vec2.y,
							angle1 + (Mathf.range(angle) + angle) * Mathf.num(Mathf.chance(0.5f)),
							Math.round(range * (1 - indexFin)), 0.95f);
				}
			}));
		}
	}

	private static Seq<Vec2> createLightningInternal(Bullet hitter, int seed, Team team, Color color, float damage, float x, float y, float rotation, int length, float offset) {
		float lightX = x, lightY = y;
		float rot = rotation;

		random.setSeed(seed);
		hit.clear();

		BulletType hitCreate = hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
		Seq<Vec2> lines = new Seq<>();
		bhit = false;

		for (int i = 0; i < length / 2; i++) {

			lines.add(new Vec2(lightX + Mathf.range(3f), lightY + Mathf.range(3f)));

			if (lines.size <= 1) continue;

			bhit = false;
			Vec2 from = lines.get(lines.size - 2);
			Vec2 to = lines.get(lines.size - 1);

			if (hitter.type.absorbable) {
				World.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()), World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {
					Tile tile = Vars.world.tile(wx, wy);
					if (tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != team) {
						bhit = true;
						lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
						return true;
					}
					return false;
				});
			}

			Tmp.c1.set(lightX, lightY, rotation, i);

			Time.run(Mathf.dst(x, y, lightX, lightY) / spreadSpeed, () -> {
				hitCreate.create(null, team, Tmp.c1.r, Tmp.c1.g, Tmp.c1.b, damage * hitter.damageMultiplier(), 1f, 1f, hitter);
				chainLightning.at(from.x, from.y, Mathf.sqr(1 - Tmp.c1.a / length), color, to);
			});

			if (bhit) break;

			rotation = (Mathf.range(20f) + rotation) * offset + rot * (1 - offset);
			lightX += Angles.trnsx(rotation, hitRange / 2f);
			lightY += Angles.trnsy(rotation, hitRange / 2f);
		}
		return lines;
	}
}
