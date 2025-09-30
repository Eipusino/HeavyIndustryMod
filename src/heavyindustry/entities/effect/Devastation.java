package heavyindustry.entities.effect;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.content.HFx;
import heavyindustry.gen.BaseEntity;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import mindustry.world.Tile;

public class Devastation extends BaseEntity implements Poolable {
	TextureRegion main = new TextureRegion();
	public float rotation, width, height;
	public float vx, vy, vr;
	public float health;
	public float z, color;
	public float time, lifetime;
	public float contagiousChance = 0f;
	public float slowDownAmount = 1f;
	public Effect explosion = Fx.none;
	public Team team = Team.derelict;
	public boolean collides = true;

	float collisionDelay = 0f;
	float contagiousTime = 0f;

	public static Devastation create() {
		return Pools.obtain(Devastation.class, Devastation::new);
	}

	@Override
	public void update() {
		x += vx * Time.delta;
		y += vy * Time.delta;
		rotation += vr * Time.delta;

		Teamc teamc = Units.closestTarget(team, x, y, 8f, Unitc::isGrounded);
		Tile tile = Vars.world.tileWorld(x, y);

		if (collides && collisionDelay <= 0f && teamc instanceof Healthc healthc) {
			float scl = 0.01f + Mathf.sqrt(vx * vx + vy * vy) / 5f;
			float lh = healthc.health();
			float lh2 = health;

			healthc.damage(health * scl);
			health -= lh * scl;
			//health -= healthc.health() * scl;

			float ratio = Mathf.clamp((lh / lh2) * scl) * slowDownAmount;

			vx *= 1f - ratio;
			vy *= 1f - ratio;
			vr *= 1f - ratio;

			HFx.fragmentExplosionSpark.at(x, y, Math.min(width, height) / 5f);

			/*if (split) {
				HFx.fragmentExplosionSmoke.at(x, y, Math.min(width, height) / 5f);
			} else {
				explosion.at(x, y, Math.min(width, height) / 4f);
			}*/
		}
		if (collisionDelay > 0) collisionDelay -= Time.delta;
		if (contagiousTime > 0) contagiousTime -= Time.delta;
		if (tile != null && tile.block().isStatic() && tile.block().solid) {
			health = 0f;
		}

		time += Time.delta;
		if (time > lifetime || health <= 0f || (vx * vx + vy * vy) < 0.0001f) {
			if (contagiousTime <= 0f) {
				explosion.at(x, y, Math.min(width, height) / 2f);
			} else {
				//HFx.fragmentExplosionSmoke.at(x, y, Math.min(width, height) / 2f);
				HFx.fragmentExplosionSpark.at(x, y, Math.min(width, height) / 2f);
			}

			remove();
		}
	}

	@Override
	public void draw() {
		//Draw.color();
		Draw.color(color);
		Draw.z(z);
		Draw.rect(main, x, y, width, height, rotation);
	}

	@Override
	public float clipSize() {
		return Math.max(width, height) * 2.5f;
	}

	public void set(TextureRegion tex, float x, float y, float width, float height, float rotation) {
		//main = tex;
		main.set(tex);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
	}

	@Override
	public void reset() {
		//main = null;
		main.set(Core.atlas.white());
		rotation = width = height = health = vx = vy = vr = 0f;
		z = color = 0f;
		time = lifetime = 0f;
		contagiousChance = 0f;
		slowDownAmount = 1f;
		explosion = Fx.none;
		team = Team.derelict;
		collides = true;
		collisionDelay = contagiousTime = 0f;
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.draw.remove(this);
		Groups.all.remove(this);
		Groups.queueFree(this);
		added = false;
	}
}
