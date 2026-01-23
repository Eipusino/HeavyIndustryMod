package endfield.world.blocks.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.content.Fx2;
import endfield.net.Call2;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;

public class ReleaseShieldWall extends Wall {
	public float chargeChance = 0.8f;
	public float maxHandle = 180;
	public float lifetime = 150;

	public Color color = Color.white;

	public ReleaseShieldWall(String name) {
		super(name);
		update = true;
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("charge", (ReleaseShieldWallBuild tile) ->
				new Bar(() ->
						Core.bundle.get("bar.charge"),
						() -> color,
						tile::getCharge
				)
		);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.abilities, Core.bundle.format("stat.charge", maxHandle, chargeChance * 100));
	}

	public static void setDamage(Tile tile, float damage) {
		if (tile == null || !(tile.build instanceof ReleaseShieldWallBuild)) return;
		((ReleaseShieldWallBuild) tile.build).setDamage(damage);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ReleaseShieldWallBuild::new;
	}

	public class ReleaseShieldWallBuild extends WallBuild {
		public float totalDamage = 0;
		public float clientDamage = 0;
		public float shieldLife = 0;
		public Bullet shieldBullet = null;
		public boolean acceptDamage = true;
		public float rePacketTimer = 0;

		public float getCharge() {
			return (Vars.net.client() ? clientDamage : totalDamage) / maxHandle;
		}

		@Override
		public void updateTile() {
			rePacketTimer = Math.min(rePacketTimer + Time.delta, 60);
			timeScale = getCharge();
			if (totalDamage > maxHandle) {
				Call2.releaseShieldWallBuildSync(tile, totalDamage);
				shieldBullet = new ShieldBullet(size * 64).create(tile.build, team, x, y, 0);
				shieldLife = lifetime;
				acceptDamage = false;
				totalDamage = 0;
				clientDamage = 0;
			}
			if (shieldLife > 0) {
				if (shieldBullet != null) {
					shieldBullet.set(x, y);
					shieldBullet.time = 0;
				}
				shieldLife -= Time.delta;
			} else {
				shieldBullet = null;
				acceptDamage = true;
			}
		}

		@Override
		public void damage(float damage) {
			super.damage(damage);
			if (acceptDamage) {
				if (!Vars.net.client()) {
					if (Mathf.chance(chargeChance)) totalDamage += damage;
				} else {
					if (Mathf.chance(chargeChance)) clientDamage += damage;
				}
			}
		}

		public void setDamage(float v) {
			if (Vars.net.client()) {
				totalDamage = v;
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(totalDamage);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			totalDamage = read.f();
		}
	}

	public static class ShieldBullet extends BulletType {
		public float range;
		public Effect openEffect;

		public Color color = Color.white;

		public ShieldBullet(float ran) {
			range = ran;
			openEffect = new Effect(35, e -> {
				Draw.color(e.color);
				Lines.stroke(e.fout() * 4);
				Lines.poly(e.x, e.y, 6, range * 0.525f + 75 * e.fin());
			});
			hittable = false;
			absorbable = false;
			hitEffect = despawnEffect = Fx.none;
			lifetime = 60;
			speed = damage = 0;
			collides = false;
			collidesAir = false;
			collidesGround = false;
			keepVelocity = false;
			reflectable = false;
		}

		@Override
		public void update(Bullet b) {
			float realRange = range * b.fout();
			Groups.bullet.intersect(b.x - realRange, b.y - realRange, realRange * 2, realRange * 2, trait -> {
				if (trait.type.absorbable && trait.team != b.team && Intersector.isInsideHexagon(trait.getX(), trait.getY(), realRange, b.x, b.y)) {
					trait.absorb();
					Fx2.shieldDefense.at(trait.getX(), trait.getY(), color);
				}
			});
		}

		@Override
		public void init(Bullet b) {
			if (b == null) return;
			openEffect.at(b.x, b.y, b.fout(), color);
		}

		@Override
		public void draw(Bullet b) {
			Draw.color(color);
			float fout = Math.min(b.fout(), 0.5f) * 2;
			Lines.stroke(fout * 3);
			Lines.poly(b.x, b.y, 6, (range * 0.525f) * fout * fout);
		}
	}
}
