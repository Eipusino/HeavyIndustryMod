package heavyindustry.core;

import heavyindustry.util.PlatformImpl;

public class DefaultImpl implements PlatformImpl {
	@Override
	public Class<?> callerClass() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] trace = thread.getStackTrace();

		try {
			return Class.forName(trace[3].getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
