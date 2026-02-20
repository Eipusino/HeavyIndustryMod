package endfield.graphics;

import arc.Graphics;
import arc.Graphics.Cursor.SystemCursor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static endfield.Vars2.platformImpl;

public class FastSizedGraphics extends SizedGraphics {
	private static MethodHandle setCursor, setSystemCursor;

	@Override
	protected void setCursor(Cursor cursor) {
		try {
			if (setCursor == null) {
				setCursor = platformImpl.lookup(Graphics.class).findVirtual(Graphics.class, "setCursor", MethodType.methodType(void.class, Cursor.class));
			}
			setCursor.invokeExact(delegate, cursor);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void setSystemCursor(SystemCursor systemCursor) {
		try {
			if (setSystemCursor == null) {
				setSystemCursor = platformImpl.lookup(Graphics.class).findVirtual(Graphics.class, "setSystemCursor", MethodType.methodType(void.class, SystemCursor.class));
			}
			setSystemCursor.invokeExact(delegate, systemCursor);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
