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

import arc.func.Boolf;
import arc.func.Prov;
import dynamilize.FunctionType;
import mindustry.Vars;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import static endfield.Vars2.classHelper;

/**
 * Reflection utilities, mainly for wrapping reflective operations to eradicate checked exceptions.
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class Reflects {
	/** Don't let anyone instantiate this class. */
	private Reflects() {}

	public static String defs(Class<?> type) {
		// boolean
		if (type == boolean.class || type == Boolean.class) return "false";
		// integer
		if (type == byte.class || type == Byte.class ||
				type == short.class || type == Short.class ||
				type == int.class || type == Integer.class ||
				type == long.class || type == Long.class ||
				type == char.class || type == Character.class) return "0";
		// float
		if (type == float.class || type == Float.class ||
				type == double.class || type == Double.class) return "0.0";
		// reference or void
		return "null";
	}

	public static Object def(Class<?> type) {
		if (type == boolean.class || type == Boolean.class) return false;
		else if (type == byte.class || type == Byte.class) return (byte) 0;
		else if (type == short.class || type == Short.class) return (short) 0;
		else if (type == int.class || type == Integer.class) return 0;
		else if (type == long.class || type == Long.class) return 0l;
		else if (type == char.class || type == Character.class) return '\u0000';
		else if (type == float.class || type == Float.class) return 0f;
		else if (type == double.class || type == Double.class) return 0d;
		else return null;
	}

	public static Class<?>[] typeOf(Class<?>... types) {
		return types;
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	public static <T> Prov<T> supply(Class<T> type, Class<?>[] parameterTypes, Object... args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			return () -> {
				try {
					return cons.newInstance(args);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds class with the specified name using Mindustry's mod class loader.
	 *
	 * @param name The class' binary name, as per {@link Class#getName()}.
	 * @return The class, or {@code null} if not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> @UnknownNullability Class<T> findClass(String name) {
		try {
			return name == null ? null : (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/** @since 1.0.8 */
	public static @Nullable Field findField(Class<?> type, String name) {
		while (type != null) {
			Field field = classHelper.getField(type, name);

			if (field != null) return field;

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a field without throwing exceptions.
	 *
	 * @return The field, or {@code null} if not found.
	 * @since 1.0.8
	 */
	public static @Nullable Field findField(Class<?> type, Boolf<Field> filler) {
		while (type != null) {
			Field[] fields = classHelper.getFields(type);
			for (Field field : fields) {
				if (filler.get(field)) return field;
			}

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a method without throwing exceptions.
	 *
	 * @return The method, or {@code null} if not found.
	 * @since 1.0.8
	 */
	public static @Nullable Method findMethod(Class<?> type, String name, Class<?>... parameterTypes) {
		while (type != null) {
			Method method = classHelper.getMethod(type, name, parameterTypes);

			if (method != null) return method;

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a constructor without throwing exceptions.
	 *
	 * @return The constructor, or {@code null} if not found.
	 * @since 1.0.8
	 */
	public static <T> @Nullable Constructor<T> findConstructor(Class<T> type, Class<?>... args) {
		Constructor<T>[] constructors = classHelper.getConstructors(type);
		for (Constructor<T> constructor : constructors) {
			if (Arrays.equals(constructor.getParameterTypes(), args)) return constructor;
		}

		return null;
	}

	public static String methodToString(Class<?> type, String name, Class<?>... argTypes) {
		StringBuilder builder = new StringBuilder();
		builder.append(type.getName()).append('.').append(name);

		if (argTypes == null || argTypes.length == 0) return builder.append("()").toString();

		int max = argTypes.length - 1;

		builder.append('(');
		int i = 0;
		while (true) {
			i++;
			builder.append(argTypes[i].getName());
			if (i == max) return builder.append(')').toString();
			builder.append(',');
		}
		// The approach of Java. But I don't like using Stream here.
		/*return type.getName() + '.' + name +
				((argTypes == null || argTypes.length == 0) ?
						"()" :
						Arrays.stream(argTypes)
						.map(c -> c == null ? "null" : c.getName())
						.collect(Collectors.joining(",", "(", ")")));*/
	}

	public static boolean isInstanceButNotSubclass(Object obj, Class<?> type) {
		if (type.isInstance(obj)) {
			try {
				if (getClassSubclassHierarchy(obj.getClass()).contains(type)) {
					return false;
				}
			} catch (ClassCastException e) {
				return false;
			}
			return true;
		}

		return false;
	}

	public static Set<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
		Class<?> c = clazz.getSuperclass();
		CollectionObjectSet<Class<?>> hierarchy = new CollectionObjectSet<>(Class.class);
		while (c != Object.class) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(interfaces);

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	public static boolean isAssignable(Field sourceType, Field targetType) {
		return sourceType != null && targetType != null && targetType.getType().isAssignableFrom(sourceType.getType());
	}

	/**
	 * Compare two parameter types and check if they can be assigned to another parameter, without
	 * considering the primitive type and its corresponding wrapper class.
	 *
	 * @param sourceTypes Source parameter type
	 * @param targetTypes Parameter type to be assigned
	 * @throws NullPointerException If the parameter array is {@code null} and the elements in the array are
	 *                              {@code null}, This is normal and should not happen
	 * @since 1.0.9
	 */
	public static boolean isAssignable(Class<?>[] sourceTypes, Class<?>[] targetTypes) {
		if (sourceTypes.length != targetTypes.length) return false;

		for (int i = 0; i < sourceTypes.length; i++) {
			if (sourceTypes[i] != targetTypes[i] && !targetTypes[i].isAssignableFrom(sourceTypes[i])) return false;
		}

		return true;
	}

	/**
	 * This method is compatible with primitive types and their corresponding wrapper classes, but the
	 * performance overhead may be slightly higher.
	 *
	 * @param sourceTypes Source parameter type
	 * @param targetTypes Parameter type to be assigned
	 * @since 1.0.9
	 */
	public static boolean isAssignableWithBoxing(Class<?>[] sourceTypes, Class<?>[] targetTypes) {
		if (sourceTypes.length != targetTypes.length) return false;

		for (int i = 0; i < sourceTypes.length; i++) {
			if (!isAssignableWithBoxing(sourceTypes[i], targetTypes[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAssignableWithBoxing(Class<?> sourceType, Class<?> targetType) {
		return targetType.isAssignableFrom(sourceType) ||
				targetType.isPrimitive() && FunctionType.wrapper(targetType).isAssignableFrom(sourceType) ||
				sourceType.isPrimitive() && targetType.isAssignableFrom(FunctionType.wrapper(sourceType));
	}

	/**
	 * Call {@code MethodHandle.invoke(Object...)} using a parameter array.
	 * <p>The constructor also uses this method.
	 *
	 * @since 1.0.9
	 */
	public static Object invokeStatic(MethodHandle handle, Object... args) throws Throwable {
		return switch (args.length) {
			case 0 -> handle.invoke();
			case 1 -> handle.invoke(args[0]);
			case 2 -> handle.invoke(args[0], args[1]);
			case 3 -> handle.invoke(args[0], args[1], args[2]);
			case 4 -> handle.invoke(args[0], args[1], args[2], args[3]);
			case 5 -> handle.invoke(args[0], args[1], args[2], args[3], args[4]);
			case 6 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
			case 7 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
			case 8 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
			case 9 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
			case 10 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9]);
			case 11 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10]);
			case 12 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11]);
			case 13 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12]);
			case 14 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13]);
			case 15 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14]);
			case 16 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
			case 17 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
			case 18 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
			case 19 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
			case 20 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19]);
			case 21 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20]);
			case 22 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21]);
			case 23 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22]);
			case 24 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23]);
			case 25 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24]);
			case 26 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
			case 27 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
			case 28 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
			case 29 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
			case 30 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29]);
			case 31 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29], args[30]);
			case 32 ->
					handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29], args[30], args[31]);
			default -> handle.invokeWithArguments(args);
		};
	}

	/**
	 * Call {@code MethodHandle.invoke(Object...)} using a parameter array.
	 *
	 * @since 1.0.9
	 */
	public static Object invokeVirtual(Object object, MethodHandle handle, Object... args) throws Throwable {
		return switch (args.length) {
			case 0 -> handle.invoke(object);
			case 1 -> handle.invoke(object, args[0]);
			case 2 -> handle.invoke(object, args[0], args[1]);
			case 3 -> handle.invoke(object, args[0], args[1], args[2]);
			case 4 -> handle.invoke(object, args[0], args[1], args[2], args[3]);
			case 5 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4]);
			case 6 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5]);
			case 7 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
			case 8 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
			case 9 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
			case 10 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9]);
			case 11 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10]);
			case 12 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11]);
			case 13 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12]);
			case 14 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13]);
			case 15 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14]);
			case 16 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
			case 17 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
			case 18 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
			case 19 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
			case 20 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19]);
			case 21 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20]);
			case 22 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21]);
			case 23 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22]);
			case 24 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23]);
			case 25 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24]);
			case 26 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
			case 27 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
			case 28 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
			case 29 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
			case 30 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29]);
			case 31 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29], args[30]);
			case 32 ->
					handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
							args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
							args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
							args[29], args[30], args[31]);
			default -> {
				Object[] methodArgs = new Object[args.length + 1];
				methodArgs[0] = object;

				System.arraycopy(args, 0, methodArgs, 1, args.length);

				yield handle.invokeWithArguments(methodArgs);
			}
		};
	}
}
