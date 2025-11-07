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

import arc.func.Boolf;
import arc.func.Prov;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.OS;
import heavyindustry.HVars;
import mindustry.Vars;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Reflection utilities, mainly for wrapping reflective operations to eradicate checked exceptions.
 * <p>You should never frequently perform repetitive operations on the same Field/Method/Constructor for
 * performance reasons.
 * <blockquote><pre>
 *     static Field theField;
 *
 *     public static Object getValue(Object obj) {
 *         try {
 *             if (theField == null) {
 *                 theField = MyClass.class.getDeclaredField("myField");
 *                 theField.setAccessible(true);
 *             }
 *             return theField.get(obj);
 *         } catch (NoSuchFieldException | IllegalAccessException e) {
 *             throw new RuntimeException(e);
 *         }
 *     }
 * </pre></blockquote>
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class ReflectUtils {
	static final CollectionObjectMap<String, Field> targetFieldMap = new CollectionObjectMap<>(String.class, Field.class);

	/// Don't let anyone instantiate this class.
	private ReflectUtils() {}

	public static Class<?> box(Class<?> type) {
		if (type == void.class) return Void.class;
		else if (type == boolean.class) return Boolean.class;
		else if (type == byte.class) return Byte.class;
		else if (type == char.class) return Character.class;
		else if (type == short.class) return Short.class;
		else if (type == int.class) return Integer.class;
		else if (type == float.class) return Float.class;
		else if (type == long.class) return Long.class;
		else if (type == double.class) return Double.class;
		else return type;
	}

	public static Class<?> unbox(Class<?> type) {
		if (type == Void.class) return void.class;
		else if (type == Boolean.class) return boolean.class;
		else if (type == Byte.class) return byte.class;
		else if (type == Character.class) return char.class;
		else if (type == Short.class) return short.class;
		else if (type == Integer.class) return int.class;
		else if (type == Float.class) return float.class;
		else if (type == Long.class) return long.class;
		else if (type == Double.class) return double.class;
		else return type;
	}

	public static String def(Class<?> type) {
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

	public static Object as(Class<?> type) {
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

	public static <T> T make(Class<T> type) {
		return make(type, ArrayUtils.arrayOf(), ArrayUtils.arrayOf());
	}

	/** Reflectively instantiates a type without throwing exceptions. */
	public static <T> T make(Constructor<T> cons, Object[] args) {
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
	public static <T> T make(Class<T> type, Class<?>[] parameterTypes, Object[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			cons.setAccessible(true);
			return cons.newInstance(args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Prov<T> supply(Class<T> type) {
		return supply(type, ArrayUtils.arrayOf(), ArrayUtils.arrayOf());
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	public static <T> Prov<T> supply(Class<T> type, Object[] args, Class<?>[] parameterTypes) {
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
	public static <T> Class<T> findClass(String name) {
		try {
			return (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Nullable
	public static Field findField(Class<?> type, String name) {
		while (type != null) {
			Field[] fields = type.getDeclaredFields();
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
	 */
	@Nullable
	public static Field findField(Class<?> type, Boolf<Field> name) {
		while (type != null) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (name.get(field)) return field;
			}

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a method without throwing exceptions.
	 *
	 * @return The method, or {@code null} if not found.
	 */
	@Nullable
	public static Method findMethod(Class<?> type, String name, Class<?>... args) {
		while (type != null) {
			Method[] methods = type.getDeclaredMethods();
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
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> Constructor<T> findConstructor(Class<?> type, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Constructor<?>[] constructors = type.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (Arrays.equals(constructor.getParameterTypes(), args)) return (Constructor<T>) constructor;
			}
		}

		return null;
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

	public static CollectionObjectSet<Class<?>> getClassSubclassHierarchy(Class<?> type) {
		Class<?> c = type.getSuperclass();
		CollectionObjectSet<Class<?>> hierarchy = new CollectionObjectSet<>(Class.class);
		while (c != null) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(interfaces);

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	public static <T> T copyProperties(Object source, T target) {
		return copyProperties(source, target, ArrayUtils.arrayOf("id"));
	}

	/**
	 * Copy the properties of an object field to another object.
	 *
	 * @param source Source Object
	 * @param target Target Object
	 * @param filler Excluded field names
	 */
	public static <T> T copyProperties(Object source, T target, @Nullable String[] filler) {
		if (source == null || target == null) return target;

		targetFieldMap.clear();

		Class<?> targetClass = target.getClass();
		while (targetClass != null) {
			for (Field field : targetClass.getDeclaredFields()) {
				if (Modifier.isFinal(field.getModifiers())) continue;

				targetFieldMap.put(field.getName(), field);
			}

			targetClass = targetClass.getSuperclass();
		}

		if (filler != null) {
			for (String name : filler) {
				targetFieldMap.remove(name);
			}
		}

		// These fields cannot be copied
		if (OS.isAndroid) {
			targetFieldMap.remove("shadow$_klass_");
			targetFieldMap.remove("shadow$_monitor_");
		}

		Class<?> sourceClass = source.getClass();
		while (sourceClass != null) {
			for (Field sourceField : sourceClass.getDeclaredFields()) {
				if (Modifier.isFinal(sourceField.getModifiers())) continue;

				Field targetField = targetFieldMap.get(sourceField.getName());

				if (!isAssignable(sourceField, targetField)) continue;

				try {
					if (!OS.isAndroid && HVars.hasUnsafe) {
						Object value = UnsafeUtils.get(sourceField, source);
						UnsafeUtils.set(targetField, target, value);
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

	public static boolean isAssignable(Field sourceType, Field targetType) {
		return sourceType != null && targetType != null && sourceType.getType().isAssignableFrom(targetType.getType());
	}
}
