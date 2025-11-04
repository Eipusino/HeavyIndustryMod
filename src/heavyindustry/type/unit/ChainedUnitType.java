package heavyindustry.type.unit;

import arc.Core;
import arc.audio.Sound;
import arc.func.Func;
import arc.func.Intf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.gen.ChainMechc;
import heavyindustry.gen.Chainedc;
import heavyindustry.math.Mathm;
import heavyindustry.type.ChainedDecal;
import mindustry.content.Blocks;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.Legsc;
import mindustry.gen.Payloadc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Weapon;
import mindustry.world.blocks.environment.Floor;

public class ChainedUnitType extends UnitType2 {
	//Legs extra
	protected static Vec2 legOffsetB = new Vec2();

	public final Seq<Seq<Weapon>> chainWeapons = new Seq<>(Seq.class);
	public TextureRegion
			segmentRegion, tailRegion,
			segmentCellRegion, tailCellRegion,
			segmentOutline, tailOutline;
	public Func<Unit, AIController> segmentAI = u -> new AIController();
	/**
	 * Decal used on unit death
	 */
	public ChainedDecal chainedDecal;
	/**
	 * Min amount of segments required for this chain, any less and everything dies.
	 */
	public int minSegments = 3;
	/**
	 * Max amount of segments that this chain can grow to.
	 */
	public int growLength = 9;
	/**
	 * Max amount of segments that this chain can be. Will not chain if total amount of the resulting chain is bigger.
	 */
	public int maxSegments = 18;
	/**
	 * Offset between each segment of the chain.
	 */
	public float segmentOffset = -1f;
	/**
	 * Max difference of angle that one segment can be from the parent.
	 */
	public float angleLimit = 30f;
	/**
	 * Time taken for a new segment to grow. If -1 it will not grow.
	 */
	public float regenTime = -1f;
	/**
	 * Time taken for 2 chains to connect to each-other. If -1 will not connect.
	 */
	public float chainTime = -1f;
	/**
	 * Sound played when growing/chaining.
	 */
	public Sound chainSound = Sounds.door;
	/**
	 * Sound played when splitting. Splittable must be true.
	 */
	public Sound splitSound = Sounds.door;
	/**
	 * If true, this unit can split when one of it's segments die. If applicable.
	 */
	public boolean splittable = false;
	//Should reduce the "Whip" effect.
	public int segmentCast = 4;
	public float segmentDamageScl = 6f;
	public float segmentLayerOffset = 0f;
	public Intf<Unit> weaponsIndex = unit -> {
		if (unit instanceof Chainedc chain) return chain.countForward();
		else return 0;
	};

	public ChainedUnitType(String name) {
		super(name);
	}

	@Override
	public Unit create(Team team) {
		return super.create(team);
	}

	@Override
	public void load() {
		super.load();
		//worm
		if (chainedDecal != null) chainedDecal.load();
		segmentRegion = Core.atlas.find(name + "-segment");
		tailRegion = Core.atlas.find(name + "-tail");
		segmentCellRegion = Core.atlas.find(name + "-segment-cell", cellRegion);
		tailCellRegion = Core.atlas.find(name + "-tail-cell", cellRegion);
		segmentOutline = Core.atlas.find(name + "-segment-outline");
		tailOutline = Core.atlas.find(name + "-tail-outline");

		for (Seq<Weapon> ws : chainWeapons) {
			for (Weapon w : ws) {
				w.load();
			}
		}
	}

	@Override
	public void init() {
		super.init();

		if (segmentOffset < 0) segmentOffset = hitSize * 2f;

		for (Seq<Weapon> ws : chainWeapons) {
			sortSegWeapons(ws);
			if (weapons.isEmpty() && ws.any()) weapons.add(ws.first());
			ws.each(Weapon::init);
		}
	}

	public void sortSegWeapons(Seq<Weapon> weaponSeq) {
		Seq<Weapon> mapped = new Seq<>(Weapon.class);
		for (int i = 0, len = weaponSeq.size; i < len; i++) {
			Weapon w = weaponSeq.get(i);
			if (w.recoilTime < 0f) {
				w.recoilTime = w.reload;
			}
			mapped.add(w);

			if (w.mirror) {
				Weapon copy = w.copy();
				copy.x *= -1;
				copy.shootX *= -1;
				copy.flipSprite = !copy.flipSprite;
				mapped.add(copy);

				w.reload *= 2;
				copy.reload *= 2;
				w.recoilTime *= 2;
				copy.recoilTime *= 2;
				w.otherSide = mapped.size - 1;
				copy.otherSide = mapped.size - 2;
			}
		}

		weaponSeq.set(mapped);
	}

	public void drawWorm(Unit unit, Chainedc chained) {
		float z = (unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathm.clamp(hitSize / 4000f, 0, 0.01f)) + (chained.countForward() * segmentLayerOffset);

		if (unit.isFlying() || shadowElevation > 0) {
			TextureRegion tmpShadow = shadowRegion;
			if (!chained.isHead() || chained.isTail()) {
				shadowRegion = chained.isTail() ? tailRegion : segmentRegion;
			}

			Draw.z(Math.min(Layer.darkness, z - 1f));
			drawShadow(unit);
			shadowRegion = tmpShadow;
		}

		Draw.z(z - 0.02f);
		if (unit instanceof ChainMechc mech) {
			Draw.reset();
			float e = unit.elevation;
			float sin = Mathf.lerp(Mathf.sin(mech.walk() + (mech.countForward() + 0.5f) * Mathf.pi, 0.63661975f * 8f, 1f), 0, e);
			float extension = Mathf.lerp(sin, 0f, e);
			float boostTrns = e * 2f;
			Floor floor = unit.isFlying() ? Blocks.air.asFloor() : unit.floorOn();
			if (floor.isLiquid) {
				Draw.color(Color.white, floor.mapColor, 0f);
			}

			for (int i : Mathf.signs) {
				Draw.mixcol(Tmp.c1.set(mechLegColor).lerp(Color.white, Mathm.clamp(unit.hitTime)), Math.max(Math.max(0f, i * extension / mechStride), unit.hitTime));
				Draw.rect(
						legRegion,
						unit.x + Angles.trnsx(mech.baseRotation(), extension * i - boostTrns, -boostTrns * i),
						unit.y + Angles.trnsy(mech.baseRotation(), extension * i - boostTrns, -boostTrns * i),
						legRegion.width * legRegion.scl() * i,
						legRegion.height * legRegion.scl() * (1f - Math.max(-sin * i, 0f) * 0.5f),
						mech.baseRotation() - 90f + 35f * i * e
				);
			}

			Draw.mixcol(Color.white, unit.hitTime);
			if (unit.lastDrownFloor != null) {
				Draw.color(Color.white, Tmp.c1.set(unit.lastDrownFloor.mapColor).mul(0.83F), unit.drownTime * 0.9F);
			} else {
				Draw.color(Color.white);
			}

			Draw.rect(baseRegion, unit, mech.baseRotation() - 90f);
			Draw.mixcol();
		}
		if (unit instanceof Legsc) drawLegs((Unit & Legsc) unit);

		Draw.z(Math.min(z - 0.01f, Layer.groundUnit - 1f));

		if (unit instanceof Payloadc) {
			drawPayload((Unit & Payloadc) unit);
		}

		drawSoftShadow(unit);

		Draw.z(z - 0.02f);

		TextureRegion tmp = region, tmpCell = cellRegion, tmpOutline = outlineRegion;
		if (!chained.isHead()) {
			region = chained.isTail() ? tailRegion : segmentRegion;
			cellRegion = chained.isTail() ? tailCellRegion : segmentCellRegion;
			outlineRegion = chained.isTail() ? tailOutline : segmentOutline;
		}

		drawOutline(unit);
		drawWeaponOutlines(unit);

		if (chained.isTail()) {
			Draw.draw(z + 0.01f, () -> {
				Tmp.v1.trns(unit.rotation + 180f, segmentOffset).add(unit);
				Drawf.construct(Tmp.v1.x, Tmp.v1.y, tailRegion, unit.rotation - 90f, chained.growTime() / regenTime, chained.growTime() / regenTime, Time.time);
				Drawf.construct(unit.x, unit.y, segmentRegion, unit.rotation - 90f, chained.growTime() / regenTime, chained.growTime() / regenTime, Time.time);
			});
		}

		Draw.z(z - 0.02f);

		drawBody(unit);
		if (drawCell) drawCell(unit);
		if (chainedDecal != null) chainedDecal.draw(unit, chained.parent());

		cellRegion = tmpCell;
		region = tmp;
		outlineRegion = tmpOutline;

		drawWeapons(unit);

		if (unit.shieldAlpha > 0 && drawShields) {
			drawShield(unit);
		}

//		if (mech != null) {
//			unit.trns(-legOffsetB.x, -legOffsetB.y);
//		}

		if (unit.abilities.length > 0) {
			for (Ability a : unit.abilities) {
				Draw.reset();
				a.draw(unit);
			}

			Draw.reset();
		}
	}

	@Override
	public void draw(Unit unit) {
		if (unit instanceof Chainedc chained) {
			drawWorm(unit, chained);
		} else {
			super.draw(unit);
		}
	}

	@Override
	public boolean hasWeapons() {
		return chainWeapons.contains(Seq::any);
	}
}
