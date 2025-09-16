package heavyindustry.input;

import arc.Core;
import arc.input.KeyCode;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import heavyindustry.util.CollectionList;
import heavyindustry.util.CollectionObjectMap;

public class CombineKeyListener<R> extends InputListener {
	public final Class<R> recType;

	public CombineKeyTree<R> keysTree;
	public boolean fuzzed;

	CollectionList<KeyCode> keysDown = new CollectionList<>(KeyCode.class);

	public CombineKeyListener(Class<R> type, CombineKeyTree<R> tree, boolean fuzz) {
		recType = type;
		keysTree = tree;
		fuzzed = fuzz;
	}

	public CombineKeyListener(Class<R> type, CombineKeyTree<R> tree) {
		this(type, tree, false);
	}

	@Override
	public boolean keyDown(InputEvent event, KeyCode keycode) {
		if (!keysTree.containsKeyCode(keycode)) return false;
		keysDown.addUnique(keycode);

		if (CombinedKeys.isControllerKey(keycode)) return true;

		if (!fuzzed) {
			CollectionObjectMap<CombinedKeys, R> map = keysTree.getTargetBindings(Core.input);
			if (map.isEmpty()) return false;

			CombinedKeys keys = new CombinedKeys(keysDown.toArray());
			R rec = map.get(keys);
			if (rec != null) {
				keysDown(event, keycode, keys, rec);
			}
		} else {
			keysTree.eachTargetBindings(Core.input, true, (k, r) -> {
				if (k.key == keycode) keysDown(event, keycode, k, r);
			});
		}

		return true;
	}

	@Override
	public boolean keyUp(InputEvent event, KeyCode keycode) {
		if (!keysTree.containsKeyCode(keycode)) return false;

		if (CombinedKeys.isControllerKey(keycode)) {
			return keysDown.remove(keycode);
		}

		if (!fuzzed) {
			CollectionObjectMap<CombinedKeys, R> map = keysTree.getTargetBindings(Core.input);
			if (map.isEmpty()) return false;

			keysDown.add(keycode); // When the button is lifted, it will inevitably listen to the last button that is lifted.
			CombinedKeys keys = new CombinedKeys(keysDown.toArray());
			R rec = map.get(keys);
			if (rec != null) {
				keysUp(event, keycode, keys, rec);
			}
			keysDown.remove(keysDown.size - 1);
		} else {
			keysTree.eachTargetBindings(Core.input, true, (k, r) -> {
				if (k.key == keycode) keysUp(event, keycode, k, r);
			});
		}
		return keysDown.remove(keycode);
	}

	public void keysDown(InputEvent event, KeyCode keycode, CombinedKeys combinedKeys, R rec) {}

	public void keysUp(InputEvent event, KeyCode keycode, CombinedKeys combinedKeys, R rec) {}
}
