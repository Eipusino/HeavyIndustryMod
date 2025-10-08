package heavyindustry.util;

import arc.func.Prov;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import heavyindustry.HVars;
import mindustry.Vars;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import static heavyindustry.util.ArrayUtils.arrayOf;
import static heavyindustry.util.ObjectUtils.requireNonNull;

/**
 * Reflection utilities, mainly for wrapping reflective operations to eradicate checked exceptions.
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class Reflects {
	public static final Map<String, Field> targetFieldMap = new CollectionObjectMap<>(String.class, Field.class);

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

	public static boolean getBool(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getBoolean(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBool(Object object, String name) {
		return getBool(object.getClass(), object, name);
	}

	public static boolean getBool(Class<?> type, String name) {
		return getBool(type, null, name);
	}

	public static byte getByte(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getByte(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte getByte(Object object, String name) {
		return getByte(object.getClass(), object, name);
	}

	public static byte getByte(Class<?> type, String name) {
		return getByte(type, null, name);
	}

	public static short getShort(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getShort(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static short getShort(Object object, String name) {
		return getShort(object.getClass(), object, name);
	}

	public static short getShort(Class<?> type, String name) {
		return getShort(type, null, name);
	}

	public static int getInt(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getInt(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getInt(Object object, String name) {
		return getInt(object.getClass(), object, name);
	}

	public static int getInt(Class<?> type, String name) {
		return getInt(type, null, name);
	}

	public static long getLong(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getLong(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getLong(Object object, String name) {
		return getLong(object.getClass(), object, name);
	}

	public static long getLong(Class<?> type, String name) {
		return getLong(type, null, name);
	}

	public static float getFloat(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getFloat(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static float getFloat(Object object, String name) {
		return getFloat(object.getClass(), object, name);
	}

	public static float getFloat(Class<?> type, String name) {
		return getFloat(type, null, name);
	}

	public static double getDouble(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getDouble(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static double getDouble(Object object, String name) {
		return getDouble(object.getClass(), object, name);
	}

	public static double getDouble(Class<?> type, String name) {
		return getDouble(type, null, name);
	}

	public static char getChar(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getChar(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static char getChar(Object object, String name) {
		return getChar(object.getClass(), object, name);
	}

	public static char getChar(Class<?> type, String name) {
		return getChar(type, null, name);
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

	public static void setBool(Class<?> type, Object object, String name, boolean value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setBoolean(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setBool(Object object, String name, boolean value) {
		setBool(object.getClass(), object, name, value);
	}

	public static void setBool(Class<?> type, String name, boolean value) {
		setBool(type, null, name, value);
	}

	public static void setByte(Class<?> type, Object object, String name, byte value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setByte(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setByte(Object object, String name, byte value) {
		setByte(object.getClass(), object, name, value);
	}

	public static void setByte(Class<?> type, String name, byte value) {
		setByte(type, null, name, value);
	}

	public static void setShort(Class<?> type, Object object, String name, short value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setShort(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setShort(Object object, String name, short value) {
		setShort(object.getClass(), object, name, value);
	}

	public static void setShort(Class<?> type, String name, short value) {
		setShort(type, null, name, value);
	}

	public static void setInt(Class<?> type, Object object, String name, int value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setInt(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInt(Object object, String name, int value) {
		setInt(object.getClass(), object, name, value);
	}

	public static void setInt(Class<?> type, String name, int value) {
		setInt(type, null, name, value);
	}

	public static void setLong(Class<?> type, Object object, String name, long value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setLong(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setLong(Object object, String name, long value) {
		setLong(object.getClass(), object, name, value);
	}

	public static void setLong(Class<?> type, String name, long value) {
		setLong(type, null, name, value);
	}

	public static void setFloat(Class<?> type, Object object, String name, float value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setFloat(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFloat(Object object, String name, float value) {
		setFloat(object.getClass(), object, name, value);
	}

	public static void setFloat(Class<?> type, String name, float value) {
		setFloat(type, null, name, value);
	}

	public static void setDouble(Class<?> type, Object object, String name, double value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setDouble(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setDouble(Object object, String name, double value) {
		setDouble(object.getClass(), object, name, value);
	}

	public static void setDouble(Class<?> type, String name, double value) {
		setDouble(type, null, name, value);
	}

	public static void setChar(Class<?> type, Object object, String name, char value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setChar(object, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setChar(Object object, String name, char value) {
		setDouble(object.getClass(), object, name, value);
	}

	public static void setChar(Class<?> type, String name, char value) {
		setDouble(type, null, name, value);
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
		return getField(type, name, true);
	}

	public static Field getField(Class<?> type, String name, boolean access) {
		try {
			Field field = type.getDeclaredField(name);
			if (access) field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field getField(Object object, String name) {
		return getField(object, name, true);
	}

	/** Gets a field of a model without throwing exceptions. */
	public static Field getField(Object object, String name, boolean access) {
		try {
			Field field = object.getClass().getDeclaredField(name);
			if (access) field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a value from a field of an model without throwing exceptions.
	 * <p>This method does not call the {@link Field#setAccessible(boolean)} method, and it is necessary to
	 * ensure that the field is accessible or has been set to be accessible. This is due to performance
	 * considerations.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Object object, Field field) {
		try {
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBoolField(Object object, Field field) {
		try {
			return field.getBoolean(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte getByteField(Object object, Field field) {
		try {
			return field.getByte(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static short getShortField(Object object, Field field) {
		try {
			return field.getShort(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getIntField(Object object, Field field) {
		try {
			return field.getInt(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getLongField(Object object, Field field) {
		try {
			return field.getLong(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static float getFloatField(Object object, Field field) {
		try {
			return field.getFloat(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static double getDoubleField(Object object, Field field) {
		try {
			return field.getDouble(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static char getCharField(Object object, Field field) {
		try {
			return field.getChar(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/** A utility function to find a field without throwing exceptions. */
	public static Field findField(Class<?> type, String name, boolean access) {
		Field field = requireNonNull(findClassField(type, name));
		if (access) field.setAccessible(true);

		return field;
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
		Method method = requireNonNull(findClassMethod(type, name, args));
		if (access) method.setAccessible(true);

		return method;
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

	/**
	 * Call {@link Method#invoke(Object, Object[])} without throwing an exception.
	 * <p>This method will not call {@link Method#setAccessible(boolean)}, please ensure that the method is
	 * accessible or set to accessible. This is due to performance considerations.
	 */
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

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... args) {
		return getConstructor(type, true, args);
	}

	/** A utility function to find a constructor without throwing exceptions. */
	public static <T> Constructor<T> getConstructor(Class<T> type, boolean access, Class<?>... args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(args);
			if (access) cons.setAccessible(true);
			return cons;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/** A utility function to find a constructor without throwing exceptions. */
	public static <T> Constructor<T> findConstructor(Class<T> type, boolean access, Class<?>... args) {
		Constructor<T> constructor = requireNonNull(findClassConstructor(type, args));
		if (access) constructor.setAccessible(true);

		return constructor;
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

	/**
	 * Finds class with the specified name using Mindustry's mod class loader.
	 *
	 * @param name The class' binary name, as per {@link Class#getName()}.
	 * @return The class, or {@code null} if not found.
	 */
	@Nullable
	public static Class<?> findClass(String name) {
		try {
			return Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Nullable
	public static Field findClassField(Class<?> type, final String name) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(name)) return field;
			}
		}

		return null;
	}

	@Nullable
	public static Method findClassMethod(Class<?> type, final String name, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Method[] methods = type.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), args)) return method;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> Constructor<T> findClassConstructor(Class<?> type, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Constructor<?>[] constructors = type.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (Arrays.equals(constructor.getParameterTypes(), args)) return (Constructor<T>) constructor;
			}
		}

		return null;
	}

	/** Finds a class from the parent classes that has a specific field. */
	@Nullable
	public static Class<?> findContainsFieldClass(Class<?> type, final String name) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(name)) return type;
			}
		}

		return null;
	}

	/** Finds a class from the parent classes that has a specific method. */
	@Nullable
	public static Class<?> findContainsMethodClass(Class<?> type, final String name, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Method[] methods = type.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), args)) return type;
			}
		}

		return null;
	}

	/** Finds a class from the parent classes that has a specific constructor. */
	@Nullable
	public static Class<?> findContainsConstructorClass(Class<?> type, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Constructor<?>[] constructors = type.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (Arrays.equals(constructor.getParameterTypes(), args)) return type;
			}
		}

		return null;
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
		} else {
			return false;
		}
	}

	public static Seq<Class<?>> getClassSubclassHierarchy(Class<?> type) {
		Class<?> c = type.getSuperclass();
		Seq<Class<?>> hierarchy = new Seq<>(Class.class);
		while (c != null) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(Arrays.asList(interfaces));

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	public static <T> T copyProperties(T source, T target, String... filler) {
		return copyProperties(source, target, false, filler);
	}

	/**
	 * Copy the properties of an object field to another object.
	 *
	 * @param source Source Object
	 * @param target Target Object
	 * @param print  If true, any exceptions that occur during the process of copying field properties will
	 *               be output to the Logger
	 * @param filler Excluded field names
	 */
	public static <T> T copyProperties(T source, T target, boolean print, String... filler) {
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

		for (String name : filler) {
			targetFieldMap.remove(name);
		}

		Class<?> sourceClass = source.getClass();
		while (sourceClass != null) {
			for (Field sourceField : sourceClass.getDeclaredFields()) {
				if (Modifier.isFinal(sourceField.getModifiers())) continue;

				Field targetField = targetFieldMap.get(sourceField.getName());

				if (targetField == null || !isAssignable(sourceField, targetField)) continue;

				try {
					if (HVars.hasUnsafe) {
						Object value = Unsafer.get(sourceField, source);
						Unsafer.set(targetField, target, value);
					} else {
						sourceField.setAccessible(true);
						targetField.setAccessible(true);

						Object value = sourceField.get(source);
						targetField.set(target, value);
					}
				} catch (Exception e) {
					if (print) Log.err(e);
				}
			}

			sourceClass = sourceClass.getSuperclass();
		}

		return target;
	}

	public static boolean isAssignable(Field sourceType, Field targetType) {
		return sourceType != null && targetType != null && targetType.getType().isAssignableFrom(sourceType.getType());
	}
}
