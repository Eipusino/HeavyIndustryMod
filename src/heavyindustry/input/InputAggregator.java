package heavyindustry.input;

import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.func.Cons;
import arc.input.GestureDetector;
import arc.input.GestureDetector.GestureListener;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.ui.TextField;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.pooling.Pool;
import heavyindustry.net.HCall;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Player;

import java.util.Iterator;

import static mindustry.Vars.control;
import static mindustry.Vars.headless;
import static mindustry.Vars.mobile;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class InputAggregator implements Iterable<String>, Eachable<String> {
	protected final OrderedMap<String, TapHandle> handles;
	protected final Seq<String> handleKeys;
	protected final Seq<TapResult> results = new Seq<>(TapResult.class);

	protected final Seq<Vec2> taps = new Seq<>(Vec2.class);
	protected final Pool<Vec2> tapPool = new Pool<>() {
		@Override
		protected Vec2 newObject() {
			return new Vec2();
		}
	};

	public InputAggregator() {
		// Store as an ordered map for fast iteration, but don't actually order the keys for fast swap-remove.
		handles = new OrderedMap<>();
		(handleKeys = handles.orderedKeys()).ordered = false;

		if (!headless) {
			if (mobile) Core.input.addProcessor(new GestureDetector(new GestureListener() {
				@Override
				public boolean tap(float x, float y, int count, KeyCode button) {
					if (count == 2 && !(
							state.isMenu() ||
									Core.scene.hasMouse(x, y) ||
									control.input.isPlacing() ||
									control.input.isBreaking() ||
									control.input.selectedUnit() != null
					)) {
						taps.add(tapPool.obtain().set(x, y));
						return true;
					} else {
						return false;
					}
				}
			}));

			Events.run(Trigger.update, () -> {
				//TODO `Keybinds` should have a way to add default bindings.
				if (!mobile && Core.input.keyTap(KeyCode.altLeft) && !(Core.scene.getKeyboardFocus() instanceof TextField)) {
					taps.add(tapPool.obtain().set(Core.input.mouseWorld()));
				}

				for (TapHandle handle : handles.values()) handle.enabled = handle.predicate.get();
				if (taps.any()) {
					for (Vec2 tap : taps) {
						HCall.tap(player, tap.x, tap.y, handleKeys);
					}

					tapPool.freeAll(taps);
					taps.clear();
				}
			});
		}
	}

	public TapHandle onTap(String key, TapListener listener) {
		if (handles.containsKey(key)) return handles.get(key);
		TapHandle handle = new TapHandle();
		handle.index = handleKeys.size;
		handle.listener = listener;

		handles.put(key, handle);
		return handle;
	}

	public Seq<TapResult> tryTap(Player player, float x, float y, Seq<String> targets) {
		results.clear();
		for (int i = 0; i < targets.size; i++) {
			TapHandle handle = handles.get(targets.get(i));
			results.add((handle == null || !handle.enabled) ? TapResult.disabled : handle.listener.canTap(player, x, y) ? TapResult.accepted : TapResult.rejected);
		}

		return results;
	}

	public void tap(Player player, float x, float y, Seq<String> targets, Seq<TapResult> results) {
		for (int i = 0; i < targets.size; i++) {
			TapHandle handle = handles.get(targets.get(i));
			TapResult result = results.get(i);
			if (handle != null && result != TapResult.disabled)
				handle.listener.tapped(player, x, y, result == TapResult.accepted);
		}
	}

	@Override
	public void each(Cons<? super String> listener) {
		for (String key : handleKeys) {
			listener.get(key);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return handleKeys.iterator();
	}

	public enum TapResult {
		accepted, rejected, disabled;

		public static final TapResult[] all = values();
	}

	public interface TapListener {
		default boolean canTap(Player player, float x, float y) {
			return true;
		}

		void tapped(Player player, float x, float y, boolean accepted);
	}

	public class TapHandle {
		protected int index;
		protected TapListener listener;

		protected boolean enabled = true, removed = false;
		protected Boolp predicate = () -> true;

		public void enabled(Boolp predicate) {
			this.predicate = predicate;
		}

		public void remove() {
			if (removed) return;
			removed = true;

			// Swap-remove, and fix the swapped handle's index.
			handles.removeIndex(index);
			if (handleKeys.size > index) handles.get(handleKeys.get(index)).index = index;
		}
	}
}
