package heavyindustry.util;

import kotlin.collections.*;

import java.util.*;

/**
 * Provide various utilities related to arrays, Iterable, Map, List, Set, etc. suitable for use in Java.
 *
 * @since 1.0.6
 */
public final class Collect {
	private Collect() {}

	/** Convert vararg to an array. */
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

	@SafeVarargs
	public static <T> Set<T> setOf(T... elements) {
		return SetsKt.setOf(elements);
	}

	@SafeVarargs
	public static <T> List<T> listOf(T... elements) {
		return Arrays.asList(elements);
	}

	@SafeVarargs
	public static <T> List<T> mutableListOf(T... elements) {
		return CollectionsKt.mutableListOf(elements);
	}

	@SafeVarargs
	public static <T> List<T> arrayListOf(T... elements) {
		return CollectionsKt.arrayListOf(elements);
	}
}
