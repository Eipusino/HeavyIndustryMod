package heavyindustry.util;

import arc.util.*;
import mindustry.*;

import java.lang.reflect.*;

/**
 * More expansion of Java reflection functionality.
 *
 * @author Eipusino
 * @since 1.0.6
 */
@SuppressWarnings({"unchecked", "unused"})
public final class ExtraRef {
    /** Don't let anyone instantiate this class. */
    private ExtraRef() {}

    public static <T> T get(Object object, String type, String name) {
        try {
            Field field = object.getClass().getClassLoader().loadClass(type).getDeclaredField(name);
            field.setAccessible(true);

            return (T) field.get(object);
        } catch (Exception e) {
            Log.err(e);
            return null;
        }
    }

    public static void set(Object object, String type, String name, Object value) {
        try {
            Field field = object.getClass().getClassLoader().loadClass(type).getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            Log.err(e);
        }
    }

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

    public static <T> Class<T> findClass(String name) {
        return forClass(name, true, Vars.mods.mainLoader());
    }

    /** Search for class based on class names without throwing exceptions. */
    public static <T> Class<T> forClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            Log.err(e);
            return null;
        }
    }

    public static <T> Class<T> forClass(String name, boolean initialize, ClassLoader loader) {
        try {
            return (Class<T>) Class.forName(name, initialize, loader);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            Log.err(e);
            return null;
        }
    }

    public static Class<?> classCaller() {
        try {
            Thread thread = Thread.currentThread();
            StackTraceElement[] trace = thread.getStackTrace();
            return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
        } catch (Exception e) {
            Log.err(e);
            return null;
        }
    }
}
