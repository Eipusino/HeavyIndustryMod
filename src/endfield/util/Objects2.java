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
	static final CollectionObjectSet<Object[]> arraySet = new CollectionObjectSet<>(Object[].class);

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
	public static <T> T requireNonNullElseGet(T obj, Prov<? extends T> supplier) {
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
	public static <T> T requireNonNull(T obj, Prov<String> messageSupplier) {
		if (obj == null)
			throw new NullPointerException(messageSupplier == null ?
					null : messageSupplier.get());
		return obj;
	}

	public static <T> T apply(T obj, Cons<? super T> cons) {
		cons.get(obj);
		return obj;
	}

	/** Used to optimize code conciseness in specific situations. */
	public static void run(RunT<? extends Throwable> cons) {
		try {
			cons.run();
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> void get(ConsT<? super T, ? extends Throwable> cons, T obj) {
		try {
			cons.get(obj);
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<? extends T, ? extends Throwable> prov, T def) {
		try {
			return prov.get();
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	/** Used to optimize code conciseness in specific situations. */
	public static <T> T get(ProvT<? extends T, ? extends Throwable> prov, ConsT<? super T, ? extends Throwable> cons, T def) {
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
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	/**
	 * Used to optimize code conciseness in specific situations.
	 * <p>If {@code obj} cannot be cast to {@link T}, return {@code def}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj, Class<T> type, T def) {
		if (obj == null || type.isInstance(obj))
			return (T) obj;
		return def;
	}

	public static String toString(Object object) {
		return toString(object, true, true);
	}

	/**
	 * Returns a string reporting the value of each declared field, via reflection.
	 * <p>Static fields are automatically skipped. Produces output like:
	 * <p>{@code "SimpleClassName{integer=1234, string=hello, character=c, intArray=[1, 2, 3], object=java.lang.Object@1234abcd, none=null}"}.
	 * <p>If there is an exception in obtaining the value of a certain field, it will result in:
	 * <p>{@code "SimpleClassName{unknown=???}"}.
	 *
	 * @param parent Should the fields of the super class be retrieved.
	 * @param deep   If true, convert the array contents to a String. Otherwise, directly convert the array
	 *               object to a String.
	 */
	public static String toString(Object object, final boolean parent, final boolean deep) {
		if (object == null) return "null";

		Class<?> type = object.getClass();

		if (type.isArray()) return toString(object, type);

		StringBuilder buf = new StringBuilder();
		buf.append(type.getSimpleName()).append('{');
		int i = 0;
		while (type != Object.class) {
			for (Field field : Vars2.classHelper.getFields(type)) {
				if ((field.getModifiers() & Modifier.STATIC) != 0) continue;

				if (i++ > 0) buf.append(", ");

				buf.append(field.getName()).append('=');

				try {
					Object value = FieldHandler.getDefault(object, field.getName());

					if (value == null) {
						buf.append("null");
					} else if (deep && value.getClass().isArray()) {
						// I think using instanceof would be better.
						if (value instanceof float[] floats) {
							Arrays2.floatToString(buf, floats);
						} else if (value instanceof int[] ints) {
							Arrays2.intToString(buf, ints);
						} else if (value instanceof boolean[] booleans) {
							Arrays2.booleanToString(buf, booleans);
						} else if (value instanceof byte[] bytes) {
							Arrays2.byteToString(buf, bytes);
						} else if (value instanceof char[] chars) {
							Arrays2.charToString(buf, chars);
						} else if (value instanceof double[] doubles) {
							Arrays2.doubleToString(buf, doubles);
						} else if (value instanceof long[] longs) {
							Arrays2.longToString(buf, longs);
						} else if (value instanceof short[] shorts) {
							Arrays2.shortToString(buf, shorts);
						} else if (value instanceof Object[] objects) {
							Arrays2.deepToString(objects, buf, arraySet);
						} else {
							// It shouldn't have happened...
							buf.append("???");
						}
					} else {
						buf.append(value);
					}
				} catch (Exception e) {
					buf.append("???");
				}
			}

			if (!parent) continue;

			type = type.getSuperclass();
		}

		return buf.append('}').toString();
	}

	static String toString(Object object, Class<?> componentType/*, final boolean deep*/) {
		String name = componentType.getSimpleName();
		/*StringBuilder buf = new StringBuilder();
		buf.append(name).append("{length=");*/

		if (object instanceof float[] floats) {
			return name + "{length=" + floats.length + '}';
		} else if (object instanceof int[] ints) {
			return name + "{length=" + ints.length + '}';
		} else if (object instanceof boolean[] booleans) {
			return name + "{length=" + booleans.length + '}';
		} else if (object instanceof byte[] bytes) {
			return name + "{length=" + bytes.length + '}';
		} else if (object instanceof char[] chars) {
			return name + "{length=" + chars.length + '}';
		} else if (object instanceof double[] doubles) {
			return name + "{length=" + doubles.length + '}';
		} else if (object instanceof long[] longs) {
			return name + "{length=" + longs.length + '}';
		} else if (object instanceof short[] shorts) {
			return name + "{length=" + shorts.length + '}';
		} else if (object instanceof Object[] objects) {
			return name + "{length=" + objects.length + '}';
			/*buf.append(objects.length).append("elements=");
			if (deep) Arrays2.deepToString(objects, buf, arraySet);
			else Arrays2.toString(buf, objects);*/
		} else {
			return name + "{???}";
		}
		//return buf.append('}').toString();
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
	public static String descriptor(Class<?> type) {
		if (type == null) throw new NullPointerException("type is null");

		int depth = 0;
		Class<?> current = type;
		while (current.isArray()) {
			depth++;
			current = current.getComponentType();
		}

		String baseDesc;
		if (current.isPrimitive()) {
			if (current == void.class) baseDesc = "V";
			else if (current == boolean.class) baseDesc = "Z";
			else if (current == byte.class) baseDesc = "B";
			else if (current == short.class) baseDesc = "S";
			else if (current == int.class) baseDesc = "I";
			else if (current == long.class) baseDesc = "J";
			else if (current == float.class) baseDesc = "F";
			else if (current == double.class) baseDesc = "D";
			else if (current == char.class) baseDesc = "C";
			else throw new IllegalArgumentException("unknown type of " + type);
		} else {
			baseDesc = 'L' + current.getName().replace('.', '/') + ';';
		}

		if (depth == 0) {
			return baseDesc;
		}

		return buildArrayDescriptor(depth, baseDesc);
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
