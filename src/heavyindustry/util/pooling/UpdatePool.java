package heavyindustry.util.pooling;

import arc.Events;
import heavyindustry.util.CollectionObjectMap;
import mindustry.game.EventType.Trigger;

import java.util.Map;

public final class UpdatePool {
	private static final Map<String, Runnable> updateTasks = new CollectionObjectMap<>(String.class, Runnable.class);

	static {
		Events.run(Trigger.update, UpdatePool::update);
	}

	private UpdatePool() {}

	public static void receive(String key, Runnable task) {
		updateTasks.put(key, task);
	}

	public static boolean remove(String key) {
		return updateTasks.remove(key) != null;
	}

	public static void update() {
		for (Runnable task : updateTasks.values()) {
			task.run();
		}
	}
}
