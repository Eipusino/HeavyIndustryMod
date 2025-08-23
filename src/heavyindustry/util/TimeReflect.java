package heavyindustry.util;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Time.DelayRun;
import arc.util.pooling.Pools;

import java.lang.reflect.Field;

/**
 * Class whose whole purpose is to change how Time.run() work
 */
public final class TimeReflect {
	static Field runs, delay, finish;
	static Seq<DelayRun> trueRuns;
	static Seq<DelayRun> removes = new Seq<>();

	private TimeReflect() {}

	public static void init() {
		runs = Reflects.findField(Time.class, "runs", true);
		trueRuns = Reflects.getField(null, runs);

		delay = Reflects.findField(DelayRun.class, "delay", true);
		finish = Reflects.findField(DelayRun.class, "finish", true);
	}

	public static void swapRuns(Seq<DelayRun> newRuns) {
		try {
			runs.set(null, newRuns);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void resetRuns() {
		try {
			runs.set(null, trueRuns);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void updateDelays(Seq<DelayRun> runSeq) {
		removes.clear();
		for (DelayRun r : runSeq) {
			updateDelay(r);
		}
		runSeq.removeAll(removes);
	}

	static void updateDelay(DelayRun run) {
		try {
			float time = delay.getFloat(run);
			time -= Time.delta;
			if (time <= 0f) {
				Runnable r = Reflects.getField(run, finish);
				r.run();
				removes.add(run);
				Pools.free(run);
			} else {
				delay.setFloat(run, time);
			}
		} catch (Exception e) {
			Log.err(e);
		}
	}
}
