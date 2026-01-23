package endfield.gen;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.pooling.Pools;
import endfield.content.Bullets2;
import endfield.content.StatusEffects2;
import endfield.math.Mathm;
import endfield.util.Constant;
import endfield.util.Sprites;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Puddles;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Fire;
import mindustry.gen.Groups;
import mindustry.gen.Puddle;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

import static endfield.Vars2.MOD_NAME;

public class UltFire extends Fire {
	public static final float baseLifetime = 1200f;
	public static TextureRegion[] ultRegion;
	public static final Effect remove = new Effect(70f, e -> {
		Draw.alpha(e.fout());
		Draw.rect(ultRegion[((int) (e.rotation + e.fin() * Fire.frames)) % Fire.frames], e.x + Mathf.randomSeedRange((int) e.y, 2), e.y + Mathf.randomSeedRange((int) e.x, 2));
		Drawf.light(e.x, e.y, 50f + Mathf.absin(5f, 5f), Pal.techBlue, 0.6f * e.fout());
	});

	static {
		ultRegion = Sprites.split(MOD_NAME + "-ult-fire", 160, 10, 4);
	}

	public static void create(float x, float y, Team team) {
		Tile tile = Vars.world.tile(World.toTile(x), World.toTile(y));

		if (tile != null && tile.build != null && tile.build.team != team) create(tile);
	}

	public static void createChance(Position pos, double chance) {
		if (Mathf.chanceDelta(chance)) UltFire.create(pos);
	}

	public static void createChance(float x, float y, float range, float chance, Team team) {
		Vars.indexer.eachBlock(null, x, y, range, other -> other.team != team && Mathf.chanceDelta(chance), other -> UltFire.create(other.tile));
	}

	public static void createChance(Teamc teamc, float range, float chance) {
		Vars.indexer.eachBlock(null, teamc.x(), teamc.y(), range, other -> other.team != teamc.team() && Mathf.chanceDelta(chance), other -> UltFire.create(other.tile));
	}

	public static void create(float x, float y, float range, Team team) {
		Vars.indexer.eachBlock(null, x, y, range, other -> other.team != team, other -> UltFire.create(other.tile));
	}

	public static void create(float x, float y, float range) {
		Vars.indexer.eachBlock(null, x, y, range, Constant.BOOLF_BUILDING_TRUE, other -> UltFire.create(other.tile));
	}

	public static void create(Teamc teamc, float range) {
		Vars.indexer.eachBlock(null, teamc.x(), teamc.y(), range, other -> other.team != teamc.team(), other -> UltFire.create(other.tile));
	}

	public static void create(Position position) {
		create(World.toTile(position.getX()), World.toTile(position.getY()));
	}

	public static void create(int x, int y) {
		create(Vars.world.tile(x, y));
	}

	public static void create(Tile tile) {
		if (Vars.net.client() || tile == null || !Vars.state.rules.fire) return; //not clientside.

		Fire fire = Fires.get(tile.x, tile.y);

		if (fire instanceof UltFire) {
			fire.lifetime = baseLifetime;
			fire.time = 0f;
		} else {
			fire = UltFire.createUlt();
			fire.tile = tile;
			fire.lifetime = baseLifetime;
			fire.set(tile.worldx(), tile.worldy());
			fire.add();
			Fires.register(fire);
		}
	}

	public static UltFire createUlt() {
		return Pools.obtain(UltFire.class, UltFire::new);
	}

	@Override
	public void draw() {
		Draw.alpha(0.35f);
		Draw.alpha(Mathm.clamp(warmup / 20f));
		Draw.z(110f);
		Draw.rect(ultRegion[Math.min((int) animation, ultRegion.length - 1)], x + Mathf.randomSeedRange((int) y, 2f), y + Mathf.randomSeedRange((int) x, 2f));
		Draw.reset();
		Drawf.light(x, y, 50f + Mathf.absin(5f, 5f), Pal.techBlue, 0.6f * Mathm.clamp(warmup / 20f));
	}

	@Override
	public void update() {
		animation += Time.delta / 2.25f;
		warmup += Time.delta;
		animation %= 40f;
		if (!Vars.headless) {
			Vars.control.sound.loop(Sounds.loopFire, this, 0.07F);
		}

		float speedMultiplier = 1f + Math.max(Vars.state.envAttrs.get(Attribute.water) * 10f, 0f);
		time = Mathm.clamp(time + Time.delta * speedMultiplier, 0f, lifetime);
		if (!Vars.net.client()) {
			if (!(time >= lifetime) && tile != null && !Float.isNaN(lifetime)) {
				Building entity = tile.build;
				boolean damage = entity != null;

				float flammability = puddleFlammability;
				if (!damage && flammability <= 0f) {
					time += Time.delta * 8f;
				}

				if (damage) {
					lifetime += Mathm.clamp(flammability / 16f, 0.1f, 0.5f) * Time.delta;
				}

				if (flammability > 1f && (spreadTimer += Time.delta * Mathm.clamp(flammability / 5f, 0.5f, 1f)) >= 22f) {
					spreadTimer = 0f;
					Point2 p = Geometry.d4[Mathf.random(3)];
					Tile other = Vars.world.tile(tile.x + p.x, tile.y + p.y);
					UltFire.create(other);
				}

				if (flammability > 0f && (fireballTimer += Time.delta * Mathm.clamp(flammability / 10f, 0f, 1.5f)) >= 40f) {
					fireballTimer = 0f;

					if (Bullets2.ultFireball != null) Bullets2.ultFireball.createNet(Team.derelict, x, y, Mathf.random(360f), 1f, 1f, 1f);
				}

				if ((damageTimer += Time.delta) >= 40f) {
					damageTimer = 0f;
					Puddle puddle = Puddles.get(tile);
					puddleFlammability = puddle == null ? 0f : puddle.getFlammability() / 3f;
					if (damage) {
						entity.damage(25f);
					}

					Damage.damageUnits(null, tile.worldx(), tile.worldy(), 8f, 15f, (unit) -> StatusEffects2.ultFireBurn != null && !unit.isFlying() && !unit.isImmune(StatusEffects2.ultFireBurn), (unit) -> {
						unit.apply(StatusEffects2.ultFireBurn, 300f);
					});
				}
			} else {
				remove();
			}
		}

		if (Vars.net.client() && !isLocal() || isRemote()) {
			interpolate();
		}

		time = Math.min(time + Time.delta, lifetime);
		if (time >= lifetime) {
			remove();
		}

	}

	@Override
	public int classId() {
		return Entitys.getId(UltFire.class);
	}

	@Override
	public void remove() {
		if (added) {
			Groups.all.remove(this);
			Groups.sync.remove(this);
			Groups.draw.remove(this);
			Groups.fire.remove(this);
			removeEffect();

			if (Vars.net.client()) {
				Vars.netClient.addRemovedEntity(id());
			}

			added = false;
			Groups.queueFree(this);
			Fires.remove(tile);
		}
	}

	public void removeEffect() {
		remove.at(x, y, animation);
	}
}
