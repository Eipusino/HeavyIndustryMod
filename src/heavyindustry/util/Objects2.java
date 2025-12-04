/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package heavyindustry.util;

import arc.func.Cons;
import arc.func.ConsT;
import arc.func.Prov;
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
public final class Objects2 {
	private Objects2() {}

	public static boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	public static boolean unequals(Object a, Object b) {
		return a == null ? b != null : !a.equals(b);
	}

	public static int hashCode(Object obj) {
		return obj == null ? 0 : obj.hashCode();
	}

	// To prevent JS from being unable to match methods, it is necessary to distinguish them.

	// 'Boolean.hashCode(boolean)' may not be compatible with Android.
	public static int hashCodeBool(boolean value) {
		return value ? 1231 : 1237;
	}

	public static int hashCodeLong(long value) {
		return (int) (value ^ (value >>> 32));
	}

	public static int hashCodes(Object... values) {
		if (values == null) return 0;

		int result = 1;

		for (Object element : values) {
			result = 31 * result + hashCode(element);
		}

		return result;
	}

	public static int hashCodeBools(boolean... values) {
		if (values == null) return 0;

		int result = 1;

		for (boolean element : values) {
			result = 31 * result + hashCodeBool(element);
		}

		return result;
	}

	public static int hashCodeLongs(long... values) {
		if (values == null) return 0;

		int result = 1;

		for (long element : values) {
			result = 31 * result + hashCodeLong(element);
		}

		return result;
	}

	public static int asInt(Object obj, int def) {
		return obj instanceof Number num ? num.intValue() : def;
	}

	public static float asFloat(Object obj, float def) {
		return obj instanceof Number num ? num.floatValue() : def;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T requireInstance(Class<?> type, T obj) {
		if (obj == null || type.isInstance(obj))
			return obj;
		throw new IllegalArgumentException("obj cannot be casted to " + type.getName());
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T requireNonNullInstance(Class<?> type, T obj) {
		if (type.isInstance(obj))
			return obj;
		throw new IllegalArgumentException("obj is not an instance of " + type.getName());
	}

	/**
	 * Checks that the specified object reference is not {@code null}. This
	 * method is designed primarily for doing parameter validation in methods
	 * and constructors, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar) {
	 *     this.bar = Objects2.requireNonNull(bar);
	 * }
	 * </pre></blockquote>
	 *
	 * @param obj the object reference to check for nullity
	 * @param <T> the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is. This method
	 * is designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar, Baz baz) {
	 *     this.bar = Objects2.requireNonNull(bar, "bar must not be null");
	 *     this.baz = Objects2.requireNonNull(baz, "baz must not be null");
	 * }
	 * </pre></blockquote>
	 *
	 * @param obj     the object reference to check for nullity
	 * @param message detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param <T>     the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}

	/**
	 * Returns {@code true} if the provided reference is {@code null} otherwise
	 * returns {@code false}.
	 *
	 * @param obj a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is {@code null} otherwise
	 * {@code false}
	 * @apiNote This method exists to be used as a
	 * {@link arc.func.Boolf}, {@code filter(Objects2::isNull)}
	 * @see arc.func.Boolf
	 * @since 1.0.8
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}

	/**
	 * Returns {@code true} if the provided reference is non-{@code null}
	 * otherwise returns {@code false}.
	 *
	 * @param obj a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is non-{@code null}
	 * otherwise {@code false}
	 * @apiNote This method exists to be used as a
	 * {@link arc.func.Boolf}, {@code filter(Objects2::nonNull)}
	 * @see arc.func.Boolf
	 * @since 1.0.8
	 */
	public static boolean nonNull(Object obj) {
		return obj != null;
	}

	/**
	 * Returns the first argument if it is non-{@code null} and
	 * otherwise returns the non-{@code null} second argument.
	 *
	 * @param obj        an object
	 * @param defaultObj a non-{@code null} object to return if the first argument
	 *                   is {@code null}
	 * @param <T>        the type of the reference
	 * @return the first argument if it is non-{@code null} and
	 * otherwise the second argument if it is non-{@code null}
	 * @throws NullPointerException if both {@code obj} is null and
	 *                              {@code defaultObj} is {@code null}
	 * @since 1.0.8
	 */
	public static <T> T requireNonNullElse(T obj, T defaultObj) {
		return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
	}

	/**
	 * Returns the first argument if it is non-{@code null} and otherwise
	 * returns the non-{@code null} value of {@code supplier.get()}.
	 *
	 * @param obj      an object
	 * @param supplier of a non-{@code null} object to return if the first argument
	 *                 is {@code null}
	 * @param <T>      the type of the first argument and return type
	 * @return the first argument if it is non-{@code null} and otherwise
	 * the value from {@code supplier.get()} if it is non-{@code null}
	 * @throws NullPointerException if both {@code obj} is null and
	 *                              either the {@code supplier} is {@code null} or
	 *                              the {@code supplier.get()} value is {@code null}
	 * @since 1.0.8
	 */
	public static <T> T requireNonNullElseGet(T obj, Prov<? extends T> supplier) {
		return (obj != null) ? obj
				: requireNonNull(requireNonNull(supplier, "supplier").get(), "supplier.get()");
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is.
	 *
	 * <p>Unlike the method {@link #requireNonNull(Object, String)},
	 * this method allows creation of the message to be deferred until
	 * after the null check is made. While this may confer a
	 * performance advantage in the non-null case, when deciding to
	 * call this method care should be taken that the costs of
	 * creating the message supplier are less than the cost of just
	 * creating the string message directly.
	 *
	 * @param obj             the object reference to check for nullity
	 * @param messageSupplier supplier of the detail message to be
	 *                        used in the event that a {@code NullPointerException} is thrown
	 * @param <T>             the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 * @since 1.0.8
	 */
	public static <T> T requireNonNull(T obj, Prov<String> messageSupplier) {
		if (obj == null)
			throw new NullPointerException(messageSupplier == null ?
					null : messageSupplier.get());
		return obj;
	}

	/// Used for Kotlin.
	public static <T> T get(T t) {
		return t;
	}

	/// Used for Kotlin.
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

	/**
	 * Used to optimize code conciseness in specific situations.
	 * <p>You must ensure that {@code obj} can be safely cast to {@link T}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T as(Object obj) {
		return (T) obj;
	}

	/**
	 * Used to optimize code conciseness in specific situations.
	 * <p>If {@code obj} cannot be cast to {@link T}, return {@code def}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T as(Object obj, Class<T> type, T def) {
		if (obj != null && !type.isInstance(obj))
			return def;
		return (T) obj;
	}

	/**
	 * Deceiving the compiler does not require throwing checked exceptions when throws or try cache are
	 * included.
	 * <p>This deduces the generic type to be `RuntimeException`, which is actually not assignable from `IOException`.
	 * <br>However, type erasure will erase all static casts anyway.
	 * <br>The result is, the code fools the compiler into thinking it's throwing `RuntimeException` and not have its
	 * method signature explicitly throw `IOException`, even though it actually does.
	 * <br>Such is the way of Java...
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> T thrower(Throwable err) throws E {
		throw (E) err;
	}

	public static String toString(Object object) {
		return toString(object, true);
	}

	/**
	 * Returns a string reporting the value of each declared field, via reflection.
	 * <p>Static fields are automatically skipped. Produces output like:
	 * <p>{@code "SimpleClassName[integer=1234, string=hello, character=c, intArray=[1, 2, 3], object=java.lang.Object@1234abcd, none=null]"}.
	 * <p>If there is an exception in obtaining the value of a certain field, it will result in:
	 * <p>{@code "SimpleClassName[unknown=???]"}.
	 *
	 * @param last Should the fields of the super class be retrieved.
	 */
	public static String toString(Object object, boolean last) {
		if (object == null) return "null";

		Class<?> type = object.getClass();

		StringBuilder builder = new StringBuilder();
		builder.append(type.getSimpleName()).append('[');
		int i = 0;
		while (type != null) {
			for (Field field : type.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if (i++ > 0) {
					builder.append(", ");
				}

				builder.append(field.getName()).append('=');

				try {
					Object value;

					// On the Android platform, the reflection performance is not low, so there is no need to use Unsafe.
					if (!OS.isAndroid && HVars.hasUnsafe) {
						value = Unsafer.get(field, object);
					} else {
						field.setAccessible(true);
						value = field.get(object);
					}

					if (value == null) {
						builder.append("null");

						continue;
					}

					Class<?> valueType = value.getClass();

					if (valueType.isArray()) {
						// I think using instanceof would be better.
						if (value instanceof float[] array) {
							Arrays2.appendFloat(builder, array);
						} else if (value instanceof int[] array) {
							Arrays2.appendInt(builder, array);
						} else if (value instanceof boolean[] array) {
							Arrays2.appendBool(builder, array);
						} else if (value instanceof byte[] array) {
							Arrays2.appendByte(builder, array);
						} else if (value instanceof char[] array) {
							Arrays2.appendChar(builder, array);
						} else if (value instanceof double[] array) {
							Arrays2.appendDouble(builder, array);
						} else if (value instanceof long[] array) {
							Arrays2.appendLong(builder, array);
						} else if (value instanceof short[] array) {
							Arrays2.appendShort(builder, array);
						} else if (value instanceof Object[] array) {
							Arrays2.append(builder, array);
						} else {
							// It shouldn't have happened...
							builder.append("???");
						}
					} else {
						builder.append(value);
					}
				} catch (Exception e) {
					builder.append("???");
				}
			}

			type = last ? type.getSuperclass() : null;
		}

		return builder.append(']').toString();
	}

	/**
	 * Convert Class object to JVM type descriptor.
	 * <p>Example:
	 * <ul>
	 *     <li>boolean -> Z
	 *     <li>int -> I
	 *     <li>long -> J
	 *     <li>float[] -> [F
	 *     <li>java.lang.Object -> Ljava/lang/Object;
	 *     <li>java.lang.invoke.MethodHandles.Lookup -> Ljava/lang/invoke/MethodHandles$Lookup;
	 *     <li>mindustry.world.Tile[][] -> [[Lmindustry/world/Tile;
	 * </ul>
	 *
	 * @param type The Class object to be converted
	 * @return JVM type descriptor string
	 * @throws NullPointerException If {@code type} is null.
	 */
	public static String toDescriptor(Class<?> type) {
		if (type == null) throw new NullPointerException("param 'type' is null");

		if (type.isArray()) return '[' + toDescriptor(type.getComponentType());

		if (type.isPrimitive()) {
			if (type == void.class) return "V";
			else if (type == boolean.class) return "Z";
			else if (type == byte.class) return "B";
			else if (type == short.class) return "S";
			else if (type == int.class) return "I";
			else if (type == long.class) return "J";
			else if (type == float.class) return "F";
			else if (type == double.class) return "D";
			else if (type == char.class) return "C";
			else throw new IllegalArgumentException("unknown type of " + type);// should not happen
		}

		return 'L' + type.getName().replace('.', '/') + ';';
	}
}
