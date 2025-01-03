package heavyindustry.util.handler;

import java.util.*;

import static heavyindustry.core.HeavyIndustryMod.*;

/**
 * A collection of static methods for field operations, including read, write, and other operations.
 * All exceptions thrown by references are caught and encapsulated in {@link RuntimeException},
 * without the need for manual try or throw.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FieldHandler<T> {
    private static final WeakHashMap<Class, FieldHandler> defaultHandlers = new WeakHashMap<>();

    private final Class<T> clazz;

    public FieldHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Construct a processor object using default rules and cache it,
     * and use this default processor to perform setValue operations.
     *
     * @see FieldHandler#setValue(Object, String, Object)
     */
    public static void setValueDefault(Object obj, String key, Object value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    /**
     * Construct a processor object using default rules and cache it,
     * and use this default processor to perform static getValue operations.
     *
     * @see FieldHandler#setValue(Object, String, Object)
     */
    public static void setValueDefault(Class<?> clazz, String key, Object value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    /**
     * Construct a processor object using default rules and cache it,
     * and use this default processor to perform the getValue operation.
     *
     * @see FieldHandler#getValue(Object, String)
     */
    public static <T> T getValueDefault(Object obj, String key) {
        return (T) cachedHandler(obj.getClass()).getValue(obj, key);
    }

    /**
     * Construct a processor object using default rules and cache it,
     * and use this default processor to perform static getValue operations.
     *
     * @see FieldHandler#getValue(Object, String)
     */
    public static <T> T getValueDefault(Class<?> clazz, String key) {
        return (T) cachedHandler(clazz).getValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, byte value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, byte value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static byte getByteDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getByteValue(obj, key);
    }

    public static byte getByteDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getByteValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, short value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, short value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static short getShortDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getShortValue(obj, key);
    }

    public static short getShortDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getShortValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, int value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, int value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static int getIntDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getIntValue(obj, key);
    }

    public static int getIntDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getIntValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, float value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, float value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static float getFloatDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getFloatValue(obj, key);
    }

    public static float getFloatDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getFloatValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, double value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, double value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static double getDoubleDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getDoubleValue(obj, key);
    }

    public static double getDoubleDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getDoubleValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, boolean value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, boolean value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static boolean getBooleanDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getBooleanValue(obj, key);
    }

    public static boolean getBooleanDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getBooleanValue(null, key);
    }

    private static FieldHandler cachedHandler(Class<?> clazz) {
        return defaultHandlers.computeIfAbsent(clazz, e -> new FieldHandler(clazz));
    }

    public static void decache(Class<?> clazz) {
        defaultHandlers.remove(clazz);
    }

    /** 清空所有当前缓存的处理器 */
    public static void clearDefault() {
        defaultHandlers.clear();
    }

    /**
     * Setting the selected property value of a specified object will ignore the access modifier and final modifier of that property.
     * If the target object is null, the field set will be static.
     * <n>Unless the field is static, passing an empty target object is not allowed
     *
     * @param object Object to change attribute value
     * @param key    The attribute name to be changed
     * @param value  The value to be written
     * @throws NullPointerException If the target object passed in is null and the field is not static
     */
    public void setValue(T object, String key, Object value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    /**
     * Retrieve the specified field value and return it. If the field does not exist, an exception will be thrown. If the target object is null, the set field will be static.
     * Unless the field is static, passing an empty target object is not allowed
     *
     * @param object If the field of the target object is not static, it is required not to be empty. If the field is static, this parameter will be ignored
     * @param key    Field name
     * @return The value of the field, if it exists
     * @throws NullPointerException If the target object passed in is null and the field is not static
     */
    public <R> R getValue(T object, String key) {
        return object == null ? fieldAccessHelper.getStatic(clazz, key) : fieldAccessHelper.get(object, key);
    }

    public void setValue(T object, String key, byte value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public byte getByteValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getByteStatic(clazz, key);
        else return fieldAccessHelper.getByte(object, key);
    }

    public void setValue(T object, String key, short value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public short getShortValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getShortStatic(clazz, key);
        else return fieldAccessHelper.getShort(object, key);
    }

    public void setValue(T object, String key, int value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public int getIntValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getIntStatic(clazz, key);
        else return fieldAccessHelper.getInt(object, key);
    }

    public void setValue(T object, String key, long value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public long getLongValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getLongStatic(clazz, key);
        else return fieldAccessHelper.getLong(object, key);
    }

    public void setValue(T object, String key, float value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public float getFloatValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getFloatStatic(clazz, key);
        else return fieldAccessHelper.getFloat(object, key);
    }

    public void setValue(T object, String key, double value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public double getDoubleValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getDoubleStatic(clazz, key);
        else return fieldAccessHelper.getDouble(object, key);
    }

    public void setValue(T object, String key, boolean value) {
        if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
        else fieldAccessHelper.set(object, key, value);
    }

    public boolean getBooleanValue(T object, String key) {
        if (object == null) return fieldAccessHelper.getBooleanStatic(clazz, key);
        else return fieldAccessHelper.getBoolean(object, key);
    }
}
