package endfield.graphics;

import arc.Graphics;
import arc.Graphics.Cursor.SystemCursor;
import endfield.util.Reflects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

/**
 * Use MethodHandle with higher performance than Reflection.
 * <p>Not suitable for Android.
 *
 * @since 1.0.9
 */
public class FastSizedGraphics extends SizedGraphics {
	private static MethodHandle setCursorMethod, setSystemCursorMethod;

	@Override
	protected void setCursor(Cursor cursor) {
		try {
			if (setCursorMethod == null) {
				setCursorMethod = Reflects.lookup.findVirtual(Graphics.class, "setCursor", MethodType.methodType(void.class, Cursor.class));
			}
			setCursorMethod.invokeExact(delegate, cursor);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void setSystemCursor(SystemCursor systemCursor) {
		try {
			if (setSystemCursorMethod == null) {
				setSystemCursorMethod = Reflects.lookup.findVirtual(Graphics.class, "setSystemCursor", MethodType.methodType(void.class, SystemCursor.class));
			}
			setSystemCursorMethod.invokeExact(delegate, systemCursor);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
