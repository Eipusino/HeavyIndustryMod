package heavyindustry.util;

import arc.files.Fi;
import arc.util.io.Streams;
import heavyindustry.func.ProvT;
import heavyindustry.func.RunT;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often enough to
 * require me to write a class for it.
 */
public final class InputOutput {
	/// Don't let anyone instantiate this class.
	private InputOutput() {}

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
			Objects2.thrower(e);
		}
	}

	public static <T> T ioUnchecked(ProvT<T, IOException> prov) {
		try {
			return prov.get();
		} catch (IOException e) {
			return Objects2.thrower(e);
		}
	}

	public static String getMD5(Fi file) {
		//MessageDigest md;
		InputStream input = null;
		byte[] buffer = new byte[8192];
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			input = new FileInputStream(file.file());
			int data;
			while ((data = input.read(buffer)) != -1) {
				md.update(buffer, 0, data);
			}
			return new BigInteger(1, md.digest()).toString(16);
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} finally {
			Streams.close(input);
		}
	}
}
