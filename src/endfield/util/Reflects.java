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
import arc.func.Boolp;
import arc.func.Intp;
import arc.func.Prov;
import arc.util.Log;
import arc.util.OS;
import mindustry.Vars;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static endfield.Vars2.classHelper;
import static endfield.Vars2.fieldAccessHelper;

/**
 * Reflection utilities, mainly for wrapping reflective operations to eradicate checked exceptions.
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class Reflects {
	static final CollectionObjectMap<String, Field> targetFieldMap = new CollectionObjectMap<>(String.class, Field.class);

	/** Don't let anyone instantiate this class. */
	private Reflects() {}

	@Contract(pure = true)
	public static Class<?> box(Class<?> type) {
		if (type == void.class) return Void.class;
		else if (type == boolean.class) return Boolean.class;
		else if (type == int.class) return Integer.class;
		else if (type == float.class) return Float.class;
		else if (type == long.class) return Long.class;
		else if (type == double.class) return Double.class;
		else if (type == short.class) return Short.class;
		else if (type == byte.class) return Byte.class;
		else if (type == char.class) return Character.class;
		else return type;
	}

	@Contract(pure = true)
	public static Class<?> unbox(Class<?> type) {
		if (type == Void.class) return void.class;
		else if (type == Boolean.class) return boolean.class;
		else if (type == Integer.class) return int.class;
		else if (type == Float.class) return float.class;
		else if (type == Long.class) return long.class;
		else if (type == Double.class) return double.class;
		else if (type == Short.class) return short.class;
		else if (type == Byte.class) return byte.class;
		else if (type == Character.class) return char.class;
		else return type;
	}

	@Contract(pure = true)
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

	@Contract(pure = true)
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

	// Suitable for obtaining one field at a time.
	// If frequent reflection operations are required on the same field,
	// cache the field instead of blindly using this method.
	/** @since 1.0.8 */
	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public static <T> T get(Class<?> type, String name, Object object, Prov<T> def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	/** @since 1.0.8 */
	@Contract(pure = true)
	public static boolean getBoolean(Class<?> type, String name, Object object, Boolp def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getBoolean(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	/** @since 1.0.8 */
	@Contract(pure = true)
	public static int getInt(Class<?> type, String name, Object object, Intp def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getInt(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	/** @since 1.0.8 */
	public static void set(Class<?> type, String name, Object object, Object value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	/** @since 1.0.8 */
	public static void setInt(Class<?> type, String name, Object object, int value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setInt(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static Class<?>[] typeOf(Class<?>... types) {
		return types;
	}

	/** Default constructor without parameters. */
	public static <T> T make(Class<T> type) {
		return make(type, Constant.EMPTY_CLASS, Constant.EMPTY_OBJECT);
	}

	/** Reflectively instantiates a type without throwing exceptions. */
	@Contract(pure = true)
	public static <T> T make(Constructor<T> cons, Object... args) {
		try {
			return cons.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	@Contract(pure = true)
	public static <T> T make(Class<T> type, Class<?>[] parameterTypes, Object... args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			cons.setAccessible(true);
			return cons.newInstance(args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	@Contract(pure = true)
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
	public static <T> @Nullable Class<T> findClass(@Nullable String name) {
		try {
			return name == null ? null : (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/** @since 1.0.8 */
	@Contract(pure = true)
	public static @Nullable Field findField(Class<?> type, String name) {
		while (type != null) {
			Field[] fields = classHelper.getFields(type);
			for (Field field : fields) {
				if (field.getName().equals(name)) return field;
			}

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
	@Contract(pure = true)
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
	@Contract(pure = true)
	public static @Nullable Method findMethod(Class<?> type, String name, Class<?>... args) {
		while (type != null) {
			Method[] methods = classHelper.getMethods(type);
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), args)) return method;
			}

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
	@Contract(pure = true)
	public static <T> @Nullable Constructor<T> findConstructor(Class<T> type, Class<?>... args) {
		Constructor<T>[] constructors = classHelper.getConstructors(type);
		for (Constructor<T> constructor : constructors) {
			if (Arrays.equals(constructor.getParameterTypes(), args)) return constructor;
		}

		return null;
	}

	@Contract(value = "null, _ -> false", pure = true)
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

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Set<Class<?>> getClassSubclassHierarchy(Class<?> type) {
		Class<?> c = type.getSuperclass();
		CollectionObjectSet<Class<?>> hierarchy = new CollectionObjectSet<>(Class.class);
		while (c != Object.class) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(interfaces);

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	/** @since 1.0.8 */
	@Contract(value = "_, _ -> param2")
	public static <T> T copyProperties(Object source, T target) {
		return copyProperties(source, target, Constant.EMPTY_STRING);
	}

	/** @since 1.0.8 */
	@Contract(value = "_, _, _ -> param2")
	public static <T> T copyProperties(Object source, T target, List<String> filler) {
		return copyProperties(source, target, filler.toArray(Constant.EMPTY_STRING));
	}

	/**
	 * Copy the properties of an object field to another object.
	 *
	 * @param source Source Object
	 * @param target Target Object
	 * @param filler Excluded field names
	 * @since 1.0.8
	 */
	@Contract(value = "_, _, _ -> param2")
	public static <T> T copyProperties(Object source, T target, String[] filler) {
		if (source == null || target == null) return target;

		targetFieldMap.clear();

		Class<?> targetClass = target.getClass();
		while (targetClass != Object.class) {
			for (Field field : classHelper.getFields(targetClass)) {
				if (Modifier.isFinal(field.getModifiers())) continue;

				targetFieldMap.put(field.getName(), field);
			}

			targetClass = targetClass.getSuperclass();
		}

		for (String name : filler) {
			targetFieldMap.remove(name);
		}

		Class<?> sourceClass = source.getClass();
		while (sourceClass != Object.class) {
			for (Field sourceField : classHelper.getFields(sourceClass)) {
				if (Modifier.isFinal(sourceField.getModifiers())) continue;

				Field targetField = targetFieldMap.get(sourceField.getName());

				if (!isAssignable(sourceField, targetField)) continue;

				try {
					if (!OS.isAndroid) {
						Object value = fieldAccessHelper.get(source, sourceField.getName());
						fieldAccessHelper.set(target, targetField.getName(), value);
					} else {
						sourceField.setAccessible(true);
						targetField.setAccessible(true);

						Object value = sourceField.get(source);
						targetField.set(target, value);
					}
				} catch (Exception e) {
					Log.err(e);
				}
			}

			sourceClass = sourceClass.getSuperclass();
		}

		return target;
	}

	@Contract(pure = true)
	public static boolean isAssignable(@Nullable Field sourceType, @Nullable Field targetType) {
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
	@Contract(pure = true)
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
	@Contract(pure = true)
	public static boolean isAssignableWithBoxing(Class<?>[] sourceTypes, Class<?>[] targetTypes) {
		if (sourceTypes.length != targetTypes.length) return false;

		for (int i = 0; i < sourceTypes.length; i++) {
			if (!isAssignableWithBoxing(sourceTypes[i], targetTypes[i])) {
				return false;
			}
		}
		return true;
	}

	@Contract(pure = true)
	public static boolean isAssignableWithBoxing(Class<?> sourceType, Class<?> targetType) {
		return targetType.isAssignableFrom(sourceType) ||
				targetType.isPrimitive() && box(targetType).isAssignableFrom(sourceType) ||
				sourceType.isPrimitive() && targetType.isAssignableFrom(box(sourceType));
	}
}
