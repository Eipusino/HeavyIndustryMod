package heavyindustry.util;

import arc.func.Cons;
import arc.func.ConsT;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.OS;
import heavyindustry.HVars;
import heavyindustry.func.ProvT;
import heavyindustry.func.RunT;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A utility assembly for objects.
 *
 * @author Eipusino
 * @since 1.0.8
 */
public final class ObjectUtils {
	private ObjectUtils() {}

	public static boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	public static int hashCode(Object obj) {
		return obj == null ? 0 : obj.hashCode();
	}

	public static int hash(Object... values) {
		if (values == null)
			return 0;

		int result = 1;

		for (Object element : values)
			result = 31 * result + (element == null ? 0 : element.hashCode());

		return result;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T requireInstance(Class<?> type, T obj) {
		if (obj != null && !type.isInstance(obj))
			throw new ClassCastException();
		return obj;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T requireNonNullInstance(Class<?> type, T obj) {
		if (!type.isInstance(obj))
			throw new ClassCastException();
		return obj;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	@Nullable
	public static <T> T getNull() {
		return null;
	}

	public static <T> T apply(T obj, Cons<T> cons) {
		cons.get(obj);
		return obj;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static void run(RunT<Throwable> cons) {
		try {
			cons.run();
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> void get(ConsT<T, Throwable> cons, T obj) {
		try {
			cons.get(obj);
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<T, Throwable> prov, T def) {
		try {
			return prov.get();
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<T, Throwable> prov, ConsT<T, Throwable> cons, T def) {
		try {
			T t = prov.get();
			cons.get(t);
			return t;
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	/** Used to optimize code conciseness in specific situations. */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj, Class<T> type, T def) {
		if (obj != null && !type.isInstance(obj))
			return def;
		return (T) obj;
	}

	/**
	 * Deceiving the compiler does not require throwing checked exceptions when throws or try cache are
	 * included.
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> T thrower(Throwable err) throws E {
		throw (E) err;
	}

	/**
	 * Returns a string reporting the value of each declared field, via reflection.
	 * <p>Static fields are automatically skipped. Produces output like:
	 * <p>{@code "SimpleClassName[integer = 1234, string = "hello", character = 'c', intArray = [1, 2, 3], none = null]"}.
	 * <p>If there is an exception in obtaining the value of a certain field, it will result in:
	 * <p>{@code "SimpleClassName[unknown = ???]"}.
	 *
	 * @param last Should the fields of the super class be retrieved.
	 */
	public static String toString(Object o, boolean last) {
		Class<?> c = o.getClass();
		StringBuilder sb = new StringBuilder();
		sb.append(c.getSimpleName()).append('[');
		int i = 0;
		while (c != null) {
			for (Field f : c.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}

				if (i++ > 0) {
					sb.append(", ");
				}

				sb.append(f.getName());
				sb.append(" = ");

				try {
					Object value;

					// On the Android platform, the reflection performance is not low, so there is no need to use Unsafe.
					if (!OS.isAndroid && HVars.hasUnsafe) {
						value = Unsafer.get(f, o);
					} else {
						f.setAccessible(true);
						value = f.get(o);
					}

					if (value == null) {
						sb.append("null");

						continue;
					}

					Class<?> type = value.getClass();

					if (type.isArray()) {
						// I think using instanceof would be better.
						if (value instanceof float[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof int[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof boolean[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof byte[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof char[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof double[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof long[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof short[] a) {
							ArrayUtils.append(sb, a);
						} else if (value instanceof Object[] a) {
							ArrayUtils.append(sb, a);
						} else {
							// It shouldn't have happened...
							sb.append("???");
						}
					} else if (value instanceof Character h) {
						sb.append('\'').append(h.charValue()).append('\'');
					} else if (value instanceof String s) {
						sb.append('"').append(s).append('"');
					} else {
						sb.append(value);
					}
				} catch (Exception e) {
					sb.append("???");
				}
			}

			c = last ? c.getSuperclass() : null;
		}
		sb.append("]");
		return sb.toString();
	}
}
