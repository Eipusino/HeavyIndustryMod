package heavyindustry.input;

import arc.Input;
import arc.func.Cons;
import arc.func.Cons2;
import arc.input.KeyCode;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.concurrent.holder.ObjectHolder;

public class CombineKeyTree<R> {
	public final Class<R> recType;

	CollectionObjectMap<CombinedKeys, R> tempMap;

	CollectionObjectMap<CombinedKeys, R> normalBindings;
	CollectionObjectMap<CombinedKeys, R> ctrlBindings;
	CollectionObjectMap<CombinedKeys, R> altBindings;
	CollectionObjectMap<CombinedKeys, R> shiftBindings;
	CollectionObjectMap<CombinedKeys, R> altCtrlBindings;
	CollectionObjectMap<CombinedKeys, R> ctrlShiftBindings;
	CollectionObjectMap<CombinedKeys, R> altShiftBindings;
	CollectionObjectMap<CombinedKeys, R> altCtrlShiftBindings;

	R rec = null;

	public CombineKeyTree(Class<R> type) {
		recType = type;

		tempMap = new CollectionObjectMap<>(CombinedKeys.class, recType);
		normalBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		ctrlBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		altBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		shiftBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		altCtrlBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		ctrlShiftBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		altShiftBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
		altCtrlShiftBindings = new CollectionObjectMap<>(CombinedKeys.class, recType);
	}

	public void putKeyBinding(CombinedKeys binding, R rec) {
		if (binding.isShift) {
			if (binding.isAlt && binding.isCtrl) altCtrlShiftBindings.put(binding, rec);
			else if (binding.isAlt) altShiftBindings.put(binding, rec);
			else if (binding.isCtrl) ctrlShiftBindings.put(binding, rec);
			else shiftBindings.put(binding, rec);
		} else {
			if (binding.isAlt && binding.isCtrl) altCtrlBindings.put(binding, rec);
			else if (binding.isAlt) altBindings.put(binding, rec);
			else if (binding.isCtrl) ctrlBindings.put(binding, rec);
			else normalBindings.put(binding, rec);
		}
	}

	@SuppressWarnings("unchecked")
	public void putKeyBinds(ObjectHolder<CombinedKeys, R>... bindings) {
		for (ObjectHolder<CombinedKeys, R> binding : bindings) {
			putKeyBinding(binding.key, binding.value);
		}
	}

	public void clear() {
		normalBindings.clear();
		altBindings.clear();
		ctrlBindings.clear();
		shiftBindings.clear();
		altShiftBindings.clear();
		ctrlShiftBindings.clear();
		altCtrlBindings.clear();
		altCtrlShiftBindings.clear();
	}

	public void forEach(Cons2<CombinedKeys, R> block) {
		normalBindings.each(block);
		ctrlBindings.each(block);
		altBindings.each(block);
		altCtrlBindings.each(block);
		ctrlShiftBindings.each(block);
		altShiftBindings.each(block);
		altCtrlShiftBindings.each(block);
	}

	public boolean containsKeyCode(KeyCode key) {
		if (key == null) return false;

		if (CombinedKeys.isCtrl(key))
			return !ctrlBindings.isEmpty() || !ctrlShiftBindings.isEmpty() || !altCtrlBindings.isEmpty() || !altCtrlShiftBindings.isEmpty();
		if (CombinedKeys.isAlt(key))
			return !altBindings.isEmpty() || !altShiftBindings.isEmpty() || !altCtrlBindings.isEmpty() || !altCtrlShiftBindings.isEmpty();
		if (CombinedKeys.isShift(key))
			return !shiftBindings.isEmpty() || !ctrlShiftBindings.isEmpty() || !altShiftBindings.isEmpty() || !altCtrlShiftBindings.isEmpty();

		return any(normalBindings, key)
				|| any(ctrlBindings, key)
				|| any(altBindings, key)
				|| any(shiftBindings, key)
				|| any(altCtrlBindings, key)
				|| any(ctrlShiftBindings, key)
				|| any(altShiftBindings, key)
				|| any(altCtrlShiftBindings, key);
	}

	public boolean any(CollectionObjectMap<CombinedKeys, R> map, KeyCode key) {
		for (var entry : map) {
			if (entry.key.key == key) return true;
		}
		return false;
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(Input input) {
		return getTargetBindings(input, false);
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(Input input, boolean fuzzyMatch) {
		return getTargetBindings(input.ctrl(), input.alt(), input.shift(), fuzzyMatch);
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(CombinedKeys input) {
		return getTargetBindings(input, false);
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(CombinedKeys input, boolean fuzzyMatch) {
		return getTargetBindings(input.isCtrl, input.isAlt, input.isShift, fuzzyMatch);
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(boolean ctrlDown, boolean altDown, boolean shiftDown) {
		return getTargetBindings(ctrlDown, altDown, shiftDown, false);
	}

	public CollectionObjectMap<CombinedKeys, R> getTargetBindings(boolean ctrlDown, boolean altDown, boolean shiftDown, boolean fuzzyMatch) {
		tempMap.clear();
		if (fuzzyMatch) {
			tempMap.putAll(normalBindings);
			if (ctrlDown) {
				tempMap.putAll(ctrlBindings);
				if (altDown) {
					tempMap.putAll(altCtrlBindings);
					if (shiftDown) tempMap.putAll(altCtrlShiftBindings);
				} else if (shiftDown) tempMap.putAll(ctrlShiftBindings);
			} else {
				if (altDown) {
					tempMap.putAll(altBindings);
					if (shiftDown) tempMap.putAll(altShiftBindings);
				} else if (shiftDown) tempMap.putAll(shiftBindings);
			}
			return tempMap;
		} else {
			return shiftDown ? altDown ? ctrlDown ? altCtrlShiftBindings : altShiftBindings : ctrlDown ? ctrlShiftBindings : shiftBindings : altDown ? ctrlDown ? altCtrlBindings : altBindings : ctrlDown ? ctrlBindings : normalBindings;
		}
	}

	public void eachTargetBindings(Input input, Cons2<CombinedKeys, R> cons) {
		eachTargetBindings(input, false, cons);
	}

	public void eachTargetBindings(Input input, boolean fuzzyMatch, Cons2<CombinedKeys, R> cons) {
		for (var entry : getTargetBindings(input, fuzzyMatch)) cons.get(entry.key, entry.value);
	}

	public void eachDown(Input input, Cons<R> cons) {
		eachDown(input, false, cons);
	}

	public void eachDown(Input input, boolean fuzzyMatch, Cons<R> cons) {
		eachTargetBindings(input, fuzzyMatch, (k, r) -> {
			if (input.keyDown(k.key)) cons.get(r);
		});
	}

	public void eachRelease(Input input, Cons<R> cons) {
		eachRelease(input, false, cons);
	}

	public void eachRelease(Input input, boolean fuzzyMatch, Cons<R> cons) {
		eachTargetBindings(input, fuzzyMatch, (k, r) -> {
			if (input.keyRelease(k.key)) cons.get(r);
		});
	}

	public void eachTap(Input input, Cons<R> cons) {
		eachTap(input, false, cons);
	}

	public void eachTap(Input input, boolean fuzzyMatch, Cons<R> cons) {
		eachTargetBindings(input, fuzzyMatch, (k, r) -> {
			if (input.keyTap(k.key)) cons.get(r);
		});
	}

	public R checkDown(Input input) {
		rec = null;

		eachTargetBindings(input, (k, r) -> {
			if (input.keyDown(k.key)) rec = r;
		});
		return rec;
	}

	public R checkRelease(Input input) {
		rec = null;

		eachTargetBindings(input, (k, r) -> {
			if (input.keyRelease(k.key)) rec = r;
		});
		return rec;
	}

	public R checkTap(Input input) {
		rec = null;

		eachTargetBindings(input, (k, r) -> {
			if (input.keyTap(k.key)) rec = r;
		});
		return rec;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		forEach((keys, rec) -> stringBuilder.append(keys).append(" -> ").append(rec).append(",\n"));
		return stringBuilder.toString();
	}
}
