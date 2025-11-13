package heavyindustry.entities.effect;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.gen.Entitys;
import heavyindustry.math.Mathm;
import heavyindustry.util.IntMap2;
import heavyindustry.util.Get;
import mindustry.content.Liquids;
import mindustry.entities.Puddles;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.Building;
import mindustry.gen.EffectState;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public final class VapourizeEffect {
	private static final Seq<BuildQueue> vapourizeQueue = new Seq<>(true, 512, BuildQueue.class);
	private static final IntMap2<BuildQueue> buildQMap = new IntMap2<>(BuildQueue.class);
	private static final IntMap2<VapourizeEffectState> vapourizeMap = new IntMap2<>(VapourizeEffectState.class);

	static {
		Events.on(WorldLoadEvent.class, event -> {
			vapourizeQueue.clear();
			buildQMap.clear();
			vapourizeMap.clear();
		});
		Events.run(Trigger.update, () -> {
			vapourizeQueue.each(buildq -> {
				Building temp = buildq.build;
				if (!temp.isValid()) {
					int size = temp.block.size;
					Puddles.deposit(temp.tile, Liquids.slag, size * size * 2 + 6);
				}
				buildq.time -= Time.delta;
			});
			vapourizeQueue.removeAll(buildq -> {
				boolean b = buildq.build.dead || buildq.time <= 0f;
				if (b) buildQMap.remove(buildq.build.id);
				return b;
			});
		});
	}

	private VapourizeEffect() {}

	public static void addMoltenBlock(Building build) {
		BuildQueue tmp = buildQMap.get(build.id);
		if (tmp == null) {
			tmp = new BuildQueue(build);
			vapourizeQueue.add(tmp);
			buildQMap.put(build.id, tmp);
		} else {
			tmp.time = 14.99f;
		}
	}

	public static void createEvaporation(float x, float y, Unit host, Entityc influence) {
		createEvaporation(x, y, 0.001f, host, influence);
	}

	public static void createEvaporation(float x, float y, float strength, Unit host, Entityc influence) {
		if (host == null || influence == null) return;
		VapourizeEffectState tmp = vapourizeMap.get(host.id);
		if (tmp == null) {
			tmp = new VapourizeEffectState(x, y, host, influence);
			vapourizeMap.put(host.id, tmp);
			tmp.add();
		} else {
			tmp.extraAlpha = Mathm.clamp((strength * Time.delta) + tmp.extraAlpha);
			tmp.time = Mathf.lerpDelta(tmp.time, tmp.lifetime / 2f, 0.3f);
		}
	}

	public static void removeEvaporation(int id) {
		vapourizeMap.remove(id);
	}

	public static class VapourizeEffectState extends EffectState {
		public float extraAlpha = 0f;
		protected Entityc influence;

		public VapourizeEffectState() {
			lifetime = 50f;
		}

		public VapourizeEffectState(float a, float b, Unit par, Entityc inf) {
			this();
			x = a;
			y = b;
			parent = par;
			influence = inf;
		}

		@Override
		public int classId() {
			return Entitys.getId(VapourizeEffectState.class);
		}

		@Override
		public void add() {
			if (added) return;
			Groups.all.add(this);
			Groups.draw.add(this);
			added = true;
		}

		@Override
		public void update() {
			if (!(parent instanceof Hitboxc hit) || !(influence instanceof Posc temp)) return;
			float hitSize = hit.hitSize();
			if (Mathf.chanceDelta(0.2f * (1f - fin()) * hitSize / 10f)) {
				Tmp.v1.trns(Angles.angle(x, y, temp.x(), temp.y()) + 180f, 65f + Mathf.range(0.3f));
				Tmp.v1.add(parent);
				Tmp.v2.trns(Mathf.random(360f), Mathf.random(hitSize / 1.25f));
				HFx.vaporation.at(parent.x(), parent.y(), 0f, new Position[]{parent, Tmp.v1.cpy(), Tmp.v2.cpy()});
			}
			super.update();
		}

		@Override
		public float clipSize() {
			return parent instanceof Hitboxc hit ? hit.hitSize() * 2f : super.clipSize();
		}

		@Override
		public void draw() {
			if (!(parent instanceof Unit unit)) return;
			UnitType type = unit.type;
			float oz = Draw.z();
			float z = (unit.elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathm.clamp(type.hitSize / 4000f, 0, 0.01f)) + 0.001f;
			float slope = (0.5f - Math.abs(fin() - 0.5f)) * 2f;
			Draw.z(z);
			Tmp.c1.set(Color.black);
			Tmp.c1.a = Mathm.clamp(slope * ((1 - unit.healthf()) + extraAlpha) * 1.4f);
			Draw.color(Tmp.c1);
			Get.simpleUnitDrawer(unit);
			Draw.z(oz);
		}

		@Override
		public void remove() {
			if (!added) return;
			Groups.all.remove(this);
			Groups.draw.remove(this);
			VapourizeEffect.removeEvaporation(parent.id());
			added = false;
		}
	}

	public static class BuildQueue {
		public Building build;
		public float time;

		public BuildQueue(Building bul, float tim) {
			build = bul;
			time = tim;
		}

		public BuildQueue(Building bul) {
			this(bul, 14.99f);
		}
	}
}
