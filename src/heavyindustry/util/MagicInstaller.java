package heavyindustry.util;

import arc.util.Log;
import arc.util.OS;

public final class MagicInstaller {
	//class jdk.internal.reflect.ReflectionAccessorImpl
	private static final byte[] data = {-54, -2, -70, -66, 0, 0, 0, 61, 0, 10, 1, 0, 43, 106, 100, 107, 47, 105, 110, 116, 101, 114, 110, 97, 108, 47, 114, 101, 102, 108, 101, 99, 116, 47, 82, 101, 102, 108, 101, 99, 116, 105, 111, 110, 65, 99, 99, 101, 115, 115, 111, 114, 73, 109, 112, 108, 7, 0, 1, 1, 0, 38, 106, 100, 107, 47, 105, 110, 116, 101, 114, 110, 97, 108, 47, 114, 101, 102, 108, 101, 99, 116, 47, 77, 97, 103, 105, 99, 65, 99, 99, 101, 115, 115, 111, 114, 73, 109, 112, 108, 7, 0, 3, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 12, 0, 5, 0, 6, 10, 0, 4, 0, 7, 1, 0, 4, 67, 111, 100, 101, 0, 33, 0, 2, 0, 4, 0, 0, 0, 0, 0, 1, 0, 1, 0, 5, 0, 6, 0, 1, 0, 9, 0, 0, 0, 17, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 8, -79, 0, 0, 0, 0, 0, 0};

	private static Class<?> magicClass;
	private static boolean installedMagic;

	private MagicInstaller() {}

	public static Class<?> installMagic() {
		if (!installedMagic && !OS.isAndroid) {
			try {
				//In some VMs, this class does not exist, such as graalvm
				Log.info("Use " + Class.forName("jdk.internal.reflect.MagicAccessorImpl"));

				magicClass = Unsafer2.defineClass(null, data, null);
				installedMagic = true;
			} catch (Exception e) {
				Log.err(e);
			}
		}

		return magicClass;
	}
}
