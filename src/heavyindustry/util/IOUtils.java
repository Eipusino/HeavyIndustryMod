package heavyindustry.util;

import heavyindustry.func.ProvT;
import heavyindustry.func.RunT;

import java.io.IOException;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often enough to
 * require me to write a class for it.
 */
public final class IOUtils {
	/// Don't let anyone instantiate this class.
	private IOUtils() {}

	public static void ioErr(RunT<Throwable> run, String message) throws IOException {
		try {
			run.run();
		} catch (Throwable t) {
			if (t instanceof IOException e) throw e;
			else throw new IOException(message, t);
		}
	}

	public static void ioUnchecked(RunT<IOException> run) {
		try {
			run.run();
		} catch (IOException e) {
			ObjectUtils.thrower(e);
		}
	}

	public static <T> T ioUnchecked(ProvT<T, IOException> prov) {
		try {
			return prov.get();
		} catch (IOException e) {
			return ObjectUtils.thrower(e);
		}
	}
}
