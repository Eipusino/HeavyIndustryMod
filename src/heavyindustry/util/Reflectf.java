package heavyindustry.util;

import arc.func.*;
import arc.util.*;
import mindustry.*;

import java.lang.reflect.*;

import static heavyindustry.util.Collect.*;

/**
 * More expansion of Java reflection functionality.
 *
 * @author Eipusino
 * @since 1.0.6
 */
@SuppressWarnings({"unchecked", "unused"})
public final class Reflectf {
	/** Don't let anyone instantiate this class. */
	private Reflectf() {}

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

	public static <T> T gef(String type, Object object, String name) {
		try {
			Field field = Class.forName(type).getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T gef(String type, String name) {
		return gef(type, null, name);
	}

	/**
	 * @param object object from which the represented field's value is to be extracted
	 * @param type The standard name of the class where the field is located.
	 * @param name the name of the field
	 */
	public static <T> T gel(Object object, String type, String name) {
		try {
			Field field = object.getClass().getClassLoader().loadClass(type).getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void sel(Object object, String type, String name, Object value) {
		try {
			Field field = object.getClass().getClassLoader().loadClass(type).getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static <T> T ged(Class<?> type, String name, T def) {
		return ged(type, null, name, def);
	}

	public static <T> T ged(Object object, String name, T def) {
		return ged(object.getClass(), object, name, def);
	}

	/**
	 * Reflect to retrieve fields without throwing exceptions and return default value.
	 *
	 * @param type the class from which to obtain the field
	 * @param object object from which the represented field's value is to be extracted
	 * @param name the name of the field
	 * @param def default value. If there is an abnormality in the reflection, return this parameter.
	 */
	public static <T> T ged(Class<?> type, Object object, String name, T def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			return def;
		}
	}

	public static <T> T invoke(Class<?> type, Object object, String name, Object[] args, Class<?>[] parameterTypes) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return (T) method.invoke(object, args);
		} catch (Exception e) {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Gets a field of a model without throwing exceptions. */
	public static Field getField(Object object, String name) {
		try {
			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
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
		} catch (Exception e) {
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

	public static <T> T invokeMethod(Method method, Object object, Object[] args) {
		try {
			return (T) method.invoke(object, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBool(Object object, String name) {
		return getBool(object.getClass(), object, name);
	}

	public static boolean getBool(Class<?> type, String name) {
		return getBool(type, null, name);
	}

	public static boolean getBool(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getBoolean(object);
		} catch (Exception e) {
			return false;
		}
	}

	public static int getInt(Object object, String name) {
		return getInt(object.getClass(), object, name);
	}

	public static int getInt(Class<?> type, String name) {
		return getInt(type, null, name);
	}

	public static int getInt(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getInt(object);
		} catch (Exception e) {
			return 0;
		}
	}

	public static float getFloat(Object object, String name) {
		return getFloat(object.getClass(), object, name);
	}

	public static float getFloat(Class<?> type, String name) {
		return getFloat(type, null, name);
	}

	public static float getFloat(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getFloat(object);
		} catch (Exception e) {
			return 0f;
		}
	}

	public static <T> Constructor<T> cons(Class<T> type, Class<?>[] args) {
		return cons(type, true, args);
	}

	/** A utility function to find a constructor without throwing exceptions. */
	public static <T> Constructor<T> cons(Class<T> type, boolean access, Class<?>[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(args);
			if (access) cons.setAccessible(true);
			return cons;
		} catch (Exception e) {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T make(Class<T> type, Class<?>[] parameterTypes, Object[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			cons.setAccessible(true);
			return cons.newInstance(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Prov<T> supply(Class<T> type) {
		return supply(type, arrayOf(), arrayOf());
	}

	public static <T> Prov<T> supply(Class<T> type, Class<?>[] parameterTypes, Object[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			return () -> {
				try {
					return cons.newInstance(args);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Class<T> mainClass(String name) {
		return forClass(name, true, Vars.mods.mainLoader());
	}

	/** Search for class based on class names without throwing exceptions. */
	public static <T> Class<T> forClass(String name) {
		try {
			return (Class<T>) Class.forName(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Class<T> forClass(String name, boolean initialize, ClassLoader loader) {
		try {
			return (Class<T>) Class.forName(name, initialize, loader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getCallerClass() {
		try {
			Thread thread = Thread.currentThread();
			StackTraceElement[] trace = thread.getStackTrace();
			return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
		} catch (Exception e) {
			return null;
		}
	}
}
