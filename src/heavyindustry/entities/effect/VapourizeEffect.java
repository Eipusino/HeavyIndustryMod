package heavyindustry.entities.effect;

import arc.Events;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Time;
import heavyindustry.gen.VapourizeEffectState;
import mindustry.content.Liquids;
import mindustry.entities.Puddles;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;

public final class VapourizeEffect {
	private static final Seq<BuildQueue> vapourizeQueue = new Seq<>(512);
	private static final IntMap<BuildQueue> buildQMap = new IntMap<>();
	private static final IntMap<VapourizeEffectState> vapourizeMap = new IntMap<>();

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
			tmp.extraAlpha = Mathf.clamp((strength * Time.delta) + tmp.extraAlpha);
			tmp.time = Mathf.lerpDelta(tmp.time, tmp.lifetime / 2f, 0.3f);
		}
	}

	public static void removeEvaporation(int id) {
		vapourizeMap.remove(id);
	}

	static class BuildQueue {
		Building build;
		float time;

		public BuildQueue(Building bul, float tim) {
			build = bul;
			time = tim;
		}

		public BuildQueue(Building bul) {
			this(bul, 14.99f);
		}
	}
}
