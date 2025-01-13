package heavyindustry.util.unsafe;

import arc.util.*;
import sun.misc.*;

import java.lang.reflect.*;

import static arc.Core.*;

/**
 * Provide some {@link Unsafe} methods.
 * <p><strong>It may not be available on certain Android platforms, please use this class with caution.</strong>
 * <p>Due to the fact that the {@code Unsafe} class may be removed or modified, and its use involves a lot of
 * underlying cognition, it is usually not recommended for use in ordinary scenarios. If it is indeed
 * necessary to use {@code Unsafe}, careful consideration should be given to whether there are other safer and
 * higher-level alternatives, and it should be ensured that the risks and consequences of {@code Unsafe}
 * operations are fully understood.
 *
 * @since 1.0.6
 */
public final class UnsafeAccess {
    private static final Unsafe unsafe;

    static {
        if (app.isAndroid()) {
            unsafe = Reflect.get(Unsafe.class, null, "THE_ONE");
        } else {
            unsafe = Reflect.cons(Unsafe.class).get();
        }
    }

    /** Don't let anyone instantiate this class. */
    private UnsafeAccess() {}

    public static Unsafe unsafe() {
        return unsafe;
    }

    public static long getOff(Field field) {
        return Modifier.isStatic(field.getModifiers()) ? unsafe.staticFieldOffset(field) : unsafe.objectFieldOffset(field);
    }

    public static void set(Field field, Object obj, byte value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putByteVolatile(obj, getOff(field), value);
        } else {
            unsafe.putByte(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, byte value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putByteVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putByte(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static byte getByte(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getByteVolatile(obj, getOff(field)) : unsafe.getByte(obj, getOff(field));
    }

    public static byte getByteStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getByteVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getByte(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, short value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putShortVolatile(obj, getOff(field), value);
        } else {
            unsafe.putShort(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, short value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putShortVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putShort(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static short getShort(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getShortVolatile(obj, getOff(field)) : unsafe.getShort(obj, getOff(field));
    }

    public static short getShortStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getShortVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getShort(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, int value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putIntVolatile(obj, getOff(field), value);
        } else {
            unsafe.putInt(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, int value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putIntVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putInt(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static int getInt(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getIntVolatile(obj, getOff(field)) : unsafe.getInt(obj, getOff(field));
    }

    public static int getIntStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getIntVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getInt(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, long value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putLongVolatile(obj, getOff(field), value);
        } else {
            unsafe.putLong(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, long value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putLongVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putLong(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static long getLong(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getLongVolatile(obj, getOff(field)) : unsafe.getLong(obj, getOff(field));
    }

    public static long getLongStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getLongVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getLong(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, float value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putFloatVolatile(obj, getOff(field), value);
        } else {
            unsafe.putFloat(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, float value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putFloatVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putFloat(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static float getFloat(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getFloatVolatile(obj, getOff(field)) : unsafe.getFloat(obj, getOff(field));
    }

    public static float getFloatStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getFloatVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getFloat(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, double value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putDoubleVolatile(obj, getOff(field), value);
        } else {
            unsafe.putDouble(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, double value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putDoubleVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putDouble(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static double getDouble(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getDoubleVolatile(obj, getOff(field)) : unsafe.getDouble(obj, getOff(field));
    }

    public static double getDoubleStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getDoubleVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getDouble(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object obj, boolean value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putBooleanVolatile(obj, getOff(field), value);
        } else {
            unsafe.putBoolean(obj, getOff(field), value);
        }
    }

    public static void setStatic(Field field, boolean value) {
        if (Modifier.isVolatile(field.getModifiers())) {
            unsafe.putBooleanVolatile(field.getDeclaringClass(), getOff(field), value);
        } else {
            unsafe.putBoolean(field.getDeclaringClass(), getOff(field), value);
        }
    }

    public static boolean getBoolean(Field field, Object obj) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getBooleanVolatile(obj, getOff(field)) : unsafe.getBoolean(obj, getOff(field));
    }

    public static boolean getBooleanStatic(Field field) {
        return Modifier.isVolatile(field.getModifiers()) ? unsafe.getBooleanVolatile(field.getDeclaringClass(), getOff(field)) : unsafe.getBoolean(field.getDeclaringClass(), getOff(field));
    }

    public static void set(Field field, Object object, Object value) {
        long fieldOff = unsafe.objectFieldOffset(field);
        Class<?> clazz = field.getType();
        if (Modifier.isVolatile(field.getModifiers())) {
            if (clazz.isPrimitive()) {
                if (clazz == int.class) unsafe.putIntVolatile(object, fieldOff, (int) value);
                else if (clazz == float.class) unsafe.putFloatVolatile(object, fieldOff, (float) value);
                else if (clazz == boolean.class) unsafe.putBooleanVolatile(object, fieldOff, (boolean) value);
                else if (clazz == byte.class) unsafe.putByteVolatile(object, fieldOff, (byte) value);
                else if (clazz == long.class) unsafe.putLongVolatile(object, fieldOff, (long) value);
                else if (clazz == double.class) unsafe.putDoubleVolatile(object, fieldOff, (double) value);
                else if (clazz == char.class) unsafe.putCharVolatile(object, fieldOff, (char) value);
                else if (clazz == short.class) unsafe.putShortVolatile(object, fieldOff, (short) value);
                else throw new IllegalArgumentException("unknown type of field " + field);
            } else unsafe.putObjectVolatile(object, fieldOff, value);
        } else {
            doPut(value, object, fieldOff, clazz);
        }
    }

    public static void setStatic(Field field, Object value) {
        Object base = unsafe.staticFieldBase(field);
        long fieldOff = unsafe.staticFieldOffset(field);
        Class<?> clazz = field.getType();

        doPut(value, base, fieldOff, clazz);
    }

    public static void doPut(Object value, Object base, long fieldOff, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class) unsafe.putInt(base, fieldOff, (int) value);
            else if (clazz == float.class) unsafe.putFloat(base, fieldOff, (float) value);
            else if (clazz == boolean.class) unsafe.putBoolean(base, fieldOff, (boolean) value);
            else if (clazz == byte.class) unsafe.putByte(base, fieldOff, (byte) value);
            else if (clazz == double.class) unsafe.putDouble(base, fieldOff, (double) value);
            else if (clazz == long.class) unsafe.putLong(base, fieldOff, (long) value);
            else if (clazz == char.class) unsafe.putChar(base, fieldOff, (char) value);
            else if (clazz == short.class) unsafe.putShort(base, fieldOff, (short) value);
            else throw new IllegalArgumentException("unknown type of field " + clazz);
        } else unsafe.putObjectVolatile(base, fieldOff, value);
    }

    public static Object get(Field field, Object object) {
        long fieldOff = unsafe.objectFieldOffset(field);
        Class<?> clazz = field.getType();

        if (Modifier.isVolatile(field.getModifiers())) {
            if (clazz.isPrimitive()) {
                if (clazz == int.class) return unsafe.getIntVolatile(object, fieldOff);
                else if (clazz == float.class) return unsafe.getFloatVolatile(object, fieldOff);
                else if (clazz == boolean.class) return unsafe.getBooleanVolatile(object, fieldOff);
                else if (clazz == byte.class) return unsafe.getByteVolatile(object, fieldOff);
                else if (clazz == long.class) return unsafe.getLongVolatile(object, fieldOff);
                else if (clazz == double.class) return unsafe.getDoubleVolatile(object, fieldOff);
                else if (clazz == char.class) return unsafe.getCharVolatile(object, fieldOff);
                else if (clazz == short.class) return unsafe.getShortVolatile(object, fieldOff);
                else throw new IllegalArgumentException("unknown type of field " + field);
            } else return unsafe.getObjectVolatile(object, fieldOff);
        } else {
            return doGet(object, fieldOff, clazz);
        }
    }

    public static Object doGet(Object object, long fieldOff, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class) return unsafe.getInt(object, fieldOff);
            else if (clazz == float.class) return unsafe.getFloat(object, fieldOff);
            else if (clazz == boolean.class) return unsafe.getBoolean(object, fieldOff);
            else if (clazz == byte.class) return unsafe.getByte(object, fieldOff);
            else if (clazz == long.class) return unsafe.getDouble(object, fieldOff);
            else if (clazz == double.class) return unsafe.getLong(object, fieldOff);
            else if (clazz == char.class) return unsafe.getChar(object, fieldOff);
            else if (clazz == short.class) return unsafe.getShort(object, fieldOff);
            else throw new IllegalArgumentException("unknown type of field " + clazz);
        } else return unsafe.getObject(object, fieldOff);
    }

    public static Object getStatic(Field field) {
        Object base = unsafe.staticFieldBase(field);
        long fieldOff = unsafe.staticFieldOffset(field);
        Class<?> clazz = field.getType();

        return doGet(base, fieldOff, clazz);
    }
}
