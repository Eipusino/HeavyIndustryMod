package heavyindustry.struct;

/**
 * Provide various utilities related to arrays, Iterable, Map, List, Set, etc. suitable for use in Java.
 *
 * @since 1.0.6
 */
public final class Collectionsf {
	private Collectionsf() {}

	/**
	 * Convert vararg to an array.
	 * Returns an array containing the specified elements.
	 */
	@SafeVarargs
	public static <T> T[] arrayOf(T... elements) {
		return elements;
	}

	public static boolean[] boolOf(boolean... bools) {
		return bools;
	}

	public static byte[] byteOf(byte... bytes) {
		return bytes;
	}

	public static short[] shortOf(short... shorts) {
		return shorts;
	}

	public static int[] intOf(int... ints) {
		return ints;
	}

	public static long[] longOf(long... longs) {
		return longs;
	}

	public static float[] floatOf(float... floats) {
		return floats;
	}

	public static double[] doubleOf(double... doubles) {
		return doubles;
	}
}
