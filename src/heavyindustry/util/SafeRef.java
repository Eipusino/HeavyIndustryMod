package heavyindustry.util;

import arc.util.*;
import mindustry.*;

import java.lang.reflect.*;

/**
 * The reflection method provided by this class does not immediately throw an exception when a reflection
 * exception occurs, causing the program to crash, but you still need to handle the exception that occurs.
 * And there will be a null pointer warning for IDEA.
 * <p>All methods for handling exceptions will be output to the log for developers to handle exceptions in a
 * timely manner.
 *
 * @author Eipusino
 * @since 1.0.6
 */
@SuppressWarnings({"unused", "unchecked"})
public final class SafeRef {
	private SafeRef() {}

	public static <T> T get(Field field) {
		try {
			return get(null, field);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T get(Object object, Field field) {
		try {
			return (T) field.get(object);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T get(Class<?> type, Object object, String name) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T get(Object object, String name) {
		try {
			return get(object.getClass(), object, name);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T get(Class<?> type, String name) {
		try {
			return get(type, null, name);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static void set(Class<?> type, Object object, String name, Object value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void set(Object object, Field field, Object value) {
		try {
			field.set(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void set(Object object, String name, Object value) {
		try {
			set(object.getClass(), object, name, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void set(Class<?> type, String name, Object value) {
		try {
			set(type, null, name, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static <T> T invoke(Class<?> type, Object object, String name, Object[] args, Class<?>... parameterTypes) {
		try {
			Method method = type.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return (T) method.invoke(object, args);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T invoke(Class<?> type, String name, Object[] args, Class<?>... parameterTypes) {
		try {
			return invoke(type, null, name, args, parameterTypes);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T invoke(Class<?> type, String name) {
		try {
			return invoke(type, name, null);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T invoke(Object object, String name, Object[] args, Class<?>... parameterTypes) {
		try {
			return invoke(object.getClass(), object, name, args, parameterTypes);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T invoke(Object object, String name) {
		try {
			return invoke(object, name, null);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static <T> T make(String type) {
		try {
			Class<T> c = (Class<T>) Class.forName(type);
			return c.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
            Log.err(e);
			return null;
		}
	}

	public static Class<?> clazz(String name) {
		try {
			return Vars.mods.mainLoader().loadClass(name);
		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	/** Basic type reflection, these basic types are all non null. */
	public static final class NumRef {
		private NumRef() {}

		public static boolean getBool(Class<?> type, Object object, String name) {
			try {
				Field field = type.getDeclaredField(name);
				field.setAccessible(true);
				return (boolean) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return false;
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
				return (byte) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0;
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
				return (short) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0;
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
				return (int) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0;
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
				return (long) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0l;
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
				return (float) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0f;
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
				return (double) field.get(object);
			} catch (Exception e) {
				Log.err(e);
				return 0d;
			}
		}

		public static double getDouble(Object object, String name) {
			return getDouble(object.getClass(), object, name);
		}

		public static double getDouble(Class<?> type, String name) {
			return getDouble(type, null, name);
		}
	}
}
