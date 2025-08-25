package heavyindustry.util;

import arc.func.Prov;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static heavyindustry.util.Utils.arrayOf;

/**
 * More expansion of Java reflection functionality.
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class Reflects {
	public static ObjectMap<String, Field> targetFieldMap = new ObjectMap<>();

	/** Don't let anyone instantiate this class. */
	private Reflects() {}

	public static Class<?> box(Class<?> type) {
		if (type == boolean.class) return Boolean.class;
		if (type == byte.class) return Byte.class;
		if (type == char.class) return Character.class;
		if (type == short.class) return Short.class;
		if (type == int.class) return Integer.class;
		if (type == float.class) return Float.class;
		if (type == long.class) return Long.class;
		if (type == double.class) return Double.class;
		return type;
	}

	public static Class<?> unbox(Class<?> type) {
		if (type == Boolean.class) return boolean.class;
		if (type == Byte.class) return byte.class;
		if (type == Character.class) return char.class;
		if (type == Short.class) return short.class;
		if (type == Integer.class) return int.class;
		if (type == Float.class) return float.class;
		if (type == Long.class) return long.class;
		if (type == Double.class) return double.class;
		return type;
	}

	public static String def(Class<?> type) {
		String t = unbox(type).getSimpleName();
		return switch (t) {
			case "boolean" -> "false";
			case "byte", "char", "short", "int", "long" -> "0";
			case "float", "double" -> "0.0";
			default -> "null";
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> T getForName(String type, Object object, String name) {
		try {
			Field field = Class.forName(type).getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T getForName(String type, String name) {
		return getForName(type, null, name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getOrDefault(Class<?> type, Object object, String name, T def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return def;
		}
	}

	public static <T> T getOrDefault(Object object, String name, T def) {
		return getOrDefault(object.getClass(), object, name, def);
	}

	public static <T> T getOrDefault(Class<?> type, String name, T def) {
		return getOrDefault(type, null, name, def);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Class<?> type, Object object, String name, Object[] args, Class<?>[] parameterTypes, T def) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return (T) method.invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return def;
		}
	}

	public static <T> T invoke(Class<?> type, String name, Object[] args, Class<?>[] parameterTypes, T def) {
		return invoke(type, null, name, args, parameterTypes, def);
	}

	public static <T> T invoke(Class<?> type, String name, T def) {
		return invoke(type, name, arrayOf(), arrayOf(), def);
	}

	public static <T> T invoke(Object object, String name, Object[] args, Class<?>[] parameterTypes, T def) {
		return invoke(object.getClass(), object, name, args, parameterTypes, def);
	}

	public static <T> T invoke(Object object, String name, T def) {
		return invoke(object, name, arrayOf(), arrayOf(), def);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Class<?> type, Object object, String name, Object[] args, Class<?>[] parameterTypes) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return (T) method.invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static void invokeVoid(Object object, String name) {
		invokeVoid(object.getClass(), object, name);
	}

	public static void invokeVoid(Class<?> type, Object object, String name) {
		invokeVoid(type, object, name, arrayOf(), arrayOf());
	}

	public static void invokeVoid(Class<?> type, String name) {
		invokeVoid(type, name, arrayOf(), arrayOf());
	}

	public static void invokeVoid(Class<?> type, String name, Object[] args, Class<?>[] parameterTypes) {
		invokeVoid(type, null, name, args, parameterTypes);
	}

	public static void invokeVoid(Class<?> type, Object object, String name, Object[] args, Class<?>[] parameterTypes) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			method.invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T invoke(Class<?> type, String name, Object[] args, Class<?>[] parameterTypes) {
		return invoke(type, null, name, args, parameterTypes);
	}

	public static <T> T invoke(Class<?> type, String name) {
		return invoke(type, name, arrayOf(), arrayOf());
	}

	public static <T> T invoke(Object object, String name, Object[] args, Class<?>[] parameterTypes) {
		return invoke(object.getClass(), object, name, args, parameterTypes);
	}

	public static <T> T invoke(Object object, String name) {
		return invoke(object, name, arrayOf(), arrayOf());
	}

	public static Field getField(Class<?> type, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/** Gets a field of a model without throwing exceptions. */
	public static Field getField(Object object, String name) {
		return getField(object.getClass(), name);
	}

	/** Gets a value from a field of an model without throwing exceptions. */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Object object, Field field) {
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** A utility function to find a field without throwing exceptions. */
	public static Field findField(Class<?> type, String name, boolean access) {
		try {
			Field field = findClassField(type, name).getDeclaredField(name);
			if (access) field.setAccessible(true);

			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/** Sets a field of an model without throwing exceptions. */
	public static void setField(Object object, Field field, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Method getMethod(Class<?> type, String name) {
		return getMethod(type, name, arrayOf());
	}

	public static Method getMethod(Class<?> type, String name, Class<?>[] parameterTypes) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/** A utility function to find a method without throwing exceptions. */
	public static Method findMethod(Class<?> type, String name, boolean access, Class<?>... args) {
		try {
			Method method = findClassMethod(type, name, args).getDeclaredMethod(name, args);
			if (access) method.setAccessible(true);

			return method;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T invokeMethod(Method method) {
		return invokeMethod(method, null, arrayOf());
	}

	public static <T> T invokeMethod(Method method, Object[] object) {
		return invokeMethod(method, null, object);
	}

	public static <T> T invokeMethod(Method method, Object object) {
		return invokeMethod(method, object, arrayOf());
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object object, Object[] args) {
		try {
			return (T) method.invoke(object, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static void invokeVoidMethod(Method method) {
		invokeVoidMethod(method, null, arrayOf());
	}

	public static void invokeVoidMethod(Method method, Object[] args) {
		invokeVoidMethod(method, null, args);
	}

	public static void invokeVoidMethod(Method method, Object object) {
		invokeVoidMethod(method, object, arrayOf());
	}

	public static void invokeVoidMethod(Method method, Object object, Object[] args) {
		try {
			method.invoke(object, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBool(Object object, String name, boolean def) {
		return getBool(object.getClass(), object, name, def);
	}

	public static boolean getBool(Class<?> type, String name, boolean def) {
		return getBool(type, null, name, def);
	}

	public static boolean getBool(Class<?> type, Object object, String name, boolean def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getBoolean(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return def;
		}
	}

	public static int getInt(Object object, String name, int def) {
		return getInt(object.getClass(), object, name, def);
	}

	public static int getInt(Class<?> type, String name, int def) {
		return getInt(type, null, name, def);
	}

	public static int getInt(Class<?> type, Object object, String name, int def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getInt(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return def;
		}
	}

	public static float getFloat(Object object, String name, float def) {
		return getFloat(object.getClass(), object, name, def);
	}

	public static float getFloat(Class<?> type, String name, float def) {
		return getFloat(type, null, name, def);
	}

	public static float getFloat(Class<?> type, Object object, String name, float def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getFloat(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return def;
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>[] args) {
		return getConstructor(type, true, args);
	}

	/** A utility function to find a constructor without throwing exceptions. */
	public static <T> Constructor<T> getConstructor(Class<T> type, boolean access, Class<?>[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(args);
			if (access) cons.setAccessible(true);
			return cons;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/** A utility function to find a constructor without throwing exceptions. */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> findConstructor(Class<T> type, boolean access, Class<?>... args) {
		try {
			Constructor<T> c = ((Class<T>) findClassConstructor(type, args)).getDeclaredConstructor(args);
			if (access) c.setAccessible(true);

			return c;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T make(Class<T> type) {
		return make(type, arrayOf(), arrayOf());
	}

	/** Reflectively instantiates a type without throwing exceptions. */
	public static <T> T make(Constructor<T> cons, Object[] args) {
		try {
			return cons.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

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
		return supply(type, arrayOf(), arrayOf());
	}

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

	/** Finds a class from the parent classes that has a specific field. */
	public static Class<?> findClassField(Class<?> type, final String name) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(name)) return type;
			}
		}

		return type;
	}

	/** Finds a class from the parent classes that has a specific method. */
	public static Class<?> findClassMethod(Class<?> type, final String name, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Method[] methods = type.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), args)) return type;
			}
		}

		return type;
	}

	/** Finds a class from the parent classes that has a specific constructor. */
	public static Class<?> findClassConstructor(Class<?> type, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Constructor<?>[] constructors = type.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (Arrays.equals(constructor.getParameterTypes(), args)) return type;
			}
		}

		return type;
	}

	/**
	 * Finds and casts a class with the specified name using Mindustry's mod class loader.
	 *
	 * @param name The class' binary name, as per {@link Class#getName()}.
	 * @param <T>  The arbitrary type parameter to cast the class into.
	 * @return The casted class, or {@code null} if not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> @Nullable Class<T> findClass(String name) {
		try {
			return (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/** Search for class based on class names without throwing exceptions. */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> forClass(String name) {
		try {
			return (Class<T>) Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getCallerClass() {
		try {
			Thread thread = Thread.currentThread();
			StackTraceElement[] trace = thread.getStackTrace();
			return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean isInstanceButNotSubclass(Object obj, Class<?> clazz) {
		if (clazz.isInstance(obj)) {
			try {
				if (getClassSubclassHierarchy(obj.getClass()).contains(clazz)) {
					return false;
				}
			} catch (ClassCastException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public static Seq<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
		Class<?> c = clazz.getSuperclass();
		Seq<Class<?>> hierarchy = new Seq<>(Class.class);
		while (c != null) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(Arrays.asList(interfaces));

			if (c == Object.class) break;

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	public static void copyProperties(Object source, Object target) {
		try {
			targetFieldMap.clear();

			Class<?> targetClass = target.getClass();
			while (targetClass != null) {
				for (Field field : targetClass.getDeclaredFields()) {
					if (!Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						targetFieldMap.put(field.getName(), field);
					}
				}
				if (targetClass == Object.class) break;

				targetClass = targetClass.getSuperclass();
			}

			targetFieldMap.remove("id");

			Class<?> sourceClass = source.getClass();
			while (sourceClass != null) {
				for (Field sourceField : sourceClass.getDeclaredFields()) {
					if (Modifier.isFinal(sourceField.getModifiers())) {
						continue;
					}
					sourceField.setAccessible(true);

					Field targetField = targetFieldMap.get(sourceField.getName());
					if (targetField != null && isAssignable(sourceField.getType(), targetField.getType())) {
						Object value = sourceField.get(source);
						targetField.set(target, value);
					}
				}
				if (sourceClass == Object.class) break;

				sourceClass = sourceClass.getSuperclass();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
		return targetType.isAssignableFrom(sourceType);
	}
}
