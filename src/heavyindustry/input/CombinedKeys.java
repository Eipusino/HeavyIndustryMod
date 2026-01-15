package heavyindustry.input;

import arc.Input;
import arc.input.KeyCode;
import arc.util.ArcRuntimeException;
import arc.util.Structs;
import heavyindustry.util.Objects2;

import java.io.Serializable;

public class CombinedKeys implements Serializable {
	private static final long serialVersionUID = -5318302851540855921l;

	boolean isCtrl, isAlt, isShift;
	KeyCode key;

	public CombinedKeys(KeyCode... keys) {
		isCtrl = Structs.contains(keys, CombinedKeys::isCtrl);
		isAlt = Structs.contains(keys, CombinedKeys::isAlt);
		isShift = Structs.contains(keys, CombinedKeys::isShift);

		key = Structs.find(keys, c -> !isCtrl(c) && !isAlt(c) && !isShift(c));

		if (key == null) throw new ArcRuntimeException("No key specified");
	}

	public boolean isDown(Input input) {
		if ((isCtrl && !input.ctrl()) || (!isCtrl && input.alt())) return false;
		if ((isAlt && !input.alt()) || (!isAlt && input.alt())) return false;
		if ((isShift && !input.shift()) || (!isShift && input.shift())) return false;
		return input.keyDown(key);
	}

	public boolean isReleased(Input input) {
		if ((isCtrl && !input.ctrl()) || (!isCtrl && input.alt())) return false;
		if ((isAlt && !input.alt()) || (!isAlt && input.alt())) return false;
		if ((isShift && !input.shift()) || (!isShift && input.shift())) return false;
		return input.keyRelease(key);
	}

	public boolean isTap(Input input) {
		if ((isCtrl && !input.ctrl()) || (!isCtrl && input.alt())) return false;
		if ((isAlt && !input.alt()) || (!isAlt && input.alt())) return false;
		if ((isShift && !input.shift()) || (!isShift && input.shift())) return false;
		return input.keyTap(key);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (isCtrl) builder.append("Ctrl").append(" + ");
		if (isAlt) builder.append("Alt").append(" + ");
		if (isShift) builder.append("Shift").append(" + ");

		builder.append(key.value);

		return builder.toString();
	}

	@Override
	public int hashCode() {
		int res = key.hashCode();
		res = res * 31 + Objects2.boolToHash(isShift);
		res = res * 31 + Objects2.boolToHash(isAlt);
		res = res * 31 + Objects2.boolToHash(isCtrl);
		return res;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof CombinedKeys keys
				&& keys.key == key
				&& keys.isCtrl == isCtrl
				&& keys.isAlt == isAlt
				&& keys.isShift == isShift;
	}

	public static boolean isControllerKey(KeyCode key) {
		return key == KeyCode.controlLeft || key == KeyCode.controlRight
				|| key == KeyCode.altLeft || key == KeyCode.altRight
				|| key == KeyCode.shiftLeft || key == KeyCode.shiftRight;
	}

	public static boolean isCtrl(KeyCode key) {
		return key == KeyCode.controlLeft || key == KeyCode.controlRight;
	}

	public static boolean isAlt(KeyCode key) {
		return key == KeyCode.altLeft || key == KeyCode.altRight;
	}

	public static boolean isShift(KeyCode key) {
		return key == KeyCode.shiftLeft || key == KeyCode.shiftRight;
	}
}
