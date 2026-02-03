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
package endfield.util;

import arc.func.Cons;
import arc.func.ConsT;
import arc.func.Prov;
import arc.util.Log;
import endfield.Vars2;
import endfield.func.ProvT;
import endfield.func.RunT;
import endfield.util.handler.FieldHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * A utility assembly for objects.
 *
 * @author Eipusino
 * @since 1.0.8
 */
public final class Objects2 {
	private Objects2() {}

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
	@Contract(value = "!null, _ -> param1; null, _ -> !null", pure = true)
	public static <T> @NotNull T requireNonNullElseGet(@Nullable T obj, Prov<? extends T> supplier) {
		return (obj != null) ? obj
				: Objects.requireNonNull(Objects.requireNonNull(supplier, "supplier").get(), "supplier.get()");
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is.
	 *
	 * <p>Unlike the method {@link Objects#requireNonNull(Object, String)},
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
	@Contract(value = "null, _ -> fail; _, _ -> param1")
	public static <T> @NotNull T requireNonNull(@Nullable T obj, Prov<String> messageSupplier) {
		if (obj == null)
			throw new NullPointerException(messageSupplier == null ?
					null : messageSupplier.get());
		return obj;
	}

	@Contract(value = "_, _ -> param1")
	public static <T> T apply(T obj, Cons<? super T> cons) {
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
	public static <T> void get(ConsT<? super T, Throwable> cons, T obj) {
		try {
			cons.get(obj);
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<? extends T, Throwable> prov, T def) {
		try {
			return prov.get();
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<? extends T, Throwable> prov, ConsT<? super T, Throwable> cons, T def) {
		try {
			T t = prov.get();
			cons.get(t);
			return t;
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	@Contract(value = "_ -> param1", pure = true)
	public static <T> T initializer(T obj) {
		return obj;
	}

	@Contract(pure = true)
	public static <T> T initializerNullable() {
		return null;
	}

	/**
	 * Used to optimize code conciseness in specific situations.
	 * <p>You must ensure that {@code obj} can be safely cast to {@link T}.
	 */
	@SuppressWarnings("unchecked")
	@Contract(value = "_ -> param1")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	/**
	 * Used to optimize code conciseness in specific situations.
	 * <p>If {@code obj} cannot be cast to {@link T}, return {@code def}.
	 */
	@SuppressWarnings("unchecked")
	@Contract(value = "null, _, _ -> null", pure = true)
	public static <T> T cast(Object obj, Class<T> type, T def) {
		if (obj != null && !type.isInstance(obj))
			return def;
		return (T) obj;
	}

	public static String toString(@Nullable Object object) {
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
	@Contract(pure = true)
	public static @NotNull String toString(@Nullable Object object, boolean last) {
		if (object == null) return "null";

		Class<?> type = object.getClass();

		StringBuilder builder = new StringBuilder();
		builder.append(type.getSimpleName()).append('[');
		int i = 0;
		while (type != null) {
			for (Field field : Vars2.classHelper.getFields(type)) {
				if ((field.getModifiers() & Modifier.STATIC) != 0) continue;

				if (i++ > 0) builder.append(", ");

				builder.append(field.getName()).append('=');

				try {
					Object value = FieldHandler.getDefault(object, field.getName());

					if (value == null) {
						builder.append("null");
						continue;
					}

					if (value.getClass().isArray()) {
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
	 *     <li>boolean -> {@code Z}
	 *     <li>int -> {@code I}
	 *     <li>long -> {@code J}
	 *     <li>float[] -> {@code [F}
	 *     <li>java.lang.Object -> {@code Ljava/lang/Object;}
	 *     <li>java.lang.invoke.MethodHandles.Lookup -> {@code Ljava/lang/invoke/MethodHandles$Lookup;}
	 *     <li>mindustry.world.Tile[][] -> {@code [[Lmindustry/world/Tile;}
	 * </ul>
	 *
	 * @param type The Class object to be converted
	 * @return JVM type descriptor string
	 * @throws NullPointerException If {@code type} is null.
	 */
	@Contract(pure = true)
	public static @NotNull String descriptor(Class<?> type) {
		if (type == null) throw new NullPointerException("type is null");

		int depth = 0;
		Class<?> current = type;
		while (current.isArray()) {
			depth++;
			current = current.getComponentType();
		}

		String baseDesc;
		if (current.isPrimitive()) {
			baseDesc = getPrimitiveDescriptor(current);
		} else {
			baseDesc = 'L' + current.getName().replace('.', '/') + ';';
		}

		if (depth == 0) {
			return baseDesc;
		}

		return buildArrayDescriptor(depth, baseDesc);
	}

	static String getPrimitiveDescriptor(Class<?> type) {
		if (type == void.class) return "V";
		else if (type == boolean.class) return "Z";
		else if (type == byte.class) return "B";
		else if (type == short.class) return "S";
		else if (type == int.class) return "I";
		else if (type == long.class) return "J";
		else if (type == float.class) return "F";
		else if (type == double.class) return "D";
		else if (type == char.class) return "C";
		else throw new IllegalArgumentException("unknown type of " + type);
	}

	static String buildArrayDescriptor(int depth, String baseDesc) {
		int totalLength = depth + baseDesc.length();
		char[] result = new char[totalLength];

		for (int i = 0; i < depth; i++) {
			result[i] = '[';
		}

		baseDesc.getChars(0, baseDesc.length(), result, depth);

		return String.valueOf(result);
	}
}
