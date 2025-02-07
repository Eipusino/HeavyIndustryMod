package heavyindustry.util;

import arc.func.*;
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

	/**
	 * Performs the given action for each element of the {@code Iterable} been processed or the action throws
	 * an exception. Actions are performed in the order of iteration if that order is specified. Exceptions
	 * thrown by the action are relayed to the caller.
	 * <p>The behavior of this method is unspecified if the action performs side effects that modify the
	 * underlying source of elements, unless an overriding class has specified a concurrent modification
	 * policy.
	 *
	 * @param iterable Implemented the {@link Iterable} interface object
	 * @param cons     Constructor
	 * @implSpec <p>The default implementation behaves as if:
	 * <pre>{@code
	 *     for (T t : iterable)
	 *         cons.get(t);
	 * }</pre>
	 * @apiNote This static method replaces the {@code Iterable.forEach} method that is not supported on
	 * some Android platforms. So why not just use {@code for} directly?
	 * @since 1.0.6
	 */
	public static <T> void forEach(Iterable<T> iterable, Cons<T> cons) {
		if (iterable != null) {
			for (T t : iterable) {
				cons.get(t);
			}
		}
	}

	public static <T> void forEach(T[] array, Cons<T> cons) {
		if (array != null) {
			for (T t : array) {
				cons.get(t);
			}
		}
	}
}
