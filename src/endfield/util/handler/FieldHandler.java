package endfield.util.handler;

import java.util.WeakHashMap;

import static endfield.Vars2.fieldAccessHelper;

/**
 * A collection of static methods for field operations, including read, write, and other operations. All
 * exceptions thrown by references are caught and encapsulated in {@link RuntimeException}, without the need
 * for manual try or throw.
 *
 * @since 1.0.8
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FieldHandler<T> {
	private static final WeakHashMap<Class, FieldHandler> defaultHandlers = new WeakHashMap<>();

	private final Class<T> clazz;

	public FieldHandler(Class<T> c) {
		clazz = c;
	}

	/**
	 * Construct a processor object using default rules and cache it, and use this default processor to
	 * perform {@code setValue} operations.
	 *
	 * @see FieldHandler#set(Object, String, Object)
	 */
	public static void setDefault(Object obj, String key, Object value) {
		cachedHandler(obj.getClass()).set(obj, key, value);
	}

	/**
	 * Construct a processor object using default rules and cache it, using this default processor to perform
	 * static {@code getValue} operations.
	 *
	 * @see FieldHandler#set(Object, String, Object)
	 */
	public static void setDefault(Class<?> clazz, String key, Object value) {
		cachedHandler(clazz).set(null, key, value);
	}

	/**
	 * Construct a processor object using default rules and cache it, and use this default processor to
	 * perform the {@code getValue} operation.
	 *
	 * @see FieldHandler#get(Object, String)
	 */
	public static <T> T getDefault(Object obj, String key) {
		return (T) cachedHandler(obj.getClass()).get(obj, key);
	}

	/**
	 * Construct a processor object using default rules and cache it, and use this default processor to
	 * perform static {@code getValue} operations.
	 *
	 * @see FieldHandler#get(Object, String)
	 */
	public static <T> T getDefault(Class<?> clazz, String key) {
		return (T) cachedHandler(clazz).get(null, key);
	}

	public static void setByteDefault(Object obj, String key, byte value) {
		cachedHandler(obj.getClass()).setByte(obj, key, value);
	}

	public static void setByteDefault(Class<?> clazz, String key, byte value) {
		cachedHandler(clazz).setByte(null, key, value);
	}

	public static byte getByteDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getByte(obj, key);
	}

	public static byte getByteDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getByte(null, key);
	}

	public static void setShortDefault(Object obj, String key, short value) {
		cachedHandler(obj.getClass()).setShort(obj, key, value);
	}

	public static void setShortDefault(Class<?> clazz, String key, short value) {
		cachedHandler(clazz).setShort(null, key, value);
	}

	public static short getShortDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getShort(obj, key);
	}

	public static short getShortDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getShort(null, key);
	}

	public static void setIntDefault(Object obj, String key, int value) {
		cachedHandler(obj.getClass()).setInt(obj, key, value);
	}

	public static void setIntDefault(Class<?> clazz, String key, int value) {
		cachedHandler(clazz).setInt(null, key, value);
	}

	public static int getIntDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getInt(obj, key);
	}

	public static int getIntDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getInt(null, key);
	}

	public static void setFloatDefault(Object obj, String key, float value) {
		cachedHandler(obj.getClass()).setFloat(obj, key, value);
	}

	public static void setFloatDefault(Class<?> clazz, String key, float value) {
		cachedHandler(clazz).setFloat(null, key, value);
	}

	public static float getFloatDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getFloat(obj, key);
	}

	public static float getFloatDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getFloat(null, key);
	}

	public static void setDoubleDefault(Object obj, String key, double value) {
		cachedHandler(obj.getClass()).setDouble(obj, key, value);
	}

	public static void setDoubleDefault(Class<?> clazz, String key, double value) {
		cachedHandler(clazz).setDouble(null, key, value);
	}

	public static double getDoubleDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getDouble(obj, key);
	}

	public static double getDoubleDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getDouble(null, key);
	}

	public static void setBooleanDefault(Object obj, String key, boolean value) {
		cachedHandler(obj.getClass()).setBoolean(obj, key, value);
	}

	public static void setBooleanDefault(Class<?> clazz, String key, boolean value) {
		cachedHandler(clazz).setBoolean(null, key, value);
	}

	public static boolean getBooleanDefault(Object obj, String key) {
		return cachedHandler(obj.getClass()).getBoolean(obj, key);
	}

	public static boolean getBooleanDefault(Class<?> clazz, String key) {
		return cachedHandler(clazz).getBoolean(null, key);
	}

	private static FieldHandler cachedHandler(Class<?> clazz) {
		return defaultHandlers.computeIfAbsent(clazz, e -> new FieldHandler(clazz));
	}

	public static void decache(Class<?> clazz) {
		defaultHandlers.remove(clazz);
	}

	/** Clear all currently cached processors. */
	public static void clearDefault() {
		defaultHandlers.clear();
	}

	/**
	 * Setting the selected property value of a specified object will ignore the access modifier and final
	 * modifier of that property. If the target object is null, the field set will be static.
	 * <p>Unless the field is static, passing an empty target object is not allowed.
	 *
	 * @param object Object to change attribute value
	 * @param key    The attribute name to be changed
	 * @param value  The value to be written
	 * @throws NullPointerException If the target object passed in is null and the field is not static
	 */
	public void set(T object, String key, Object value) {
		if (object == null) fieldAccessHelper.setStatic(clazz, key, value);
		else fieldAccessHelper.set(object, key, value);
	}

	/**
	 * Retrieve the specified field value and return it. If the field does not exist, an exception will be
	 * thrown. If the target object is null, the set field will be static.
	 * <p>Unless the field is static, passing an empty target object is not allowed.
	 *
	 * @param object If the field of the target object is not static, it is required not to be empty. If the
	 *               field is static, this parameter will be ignored.
	 * @param key    field name
	 * @return The value of the field, if it exists
	 * @throws NullPointerException If the target object passed in is null and the field is not static
	 */
	public <R> R get(T object, String key) {
		return object == null ? fieldAccessHelper.getStatic(clazz, key) : fieldAccessHelper.get(object, key);
	}

	public void setObject(T object, String key, Object value) {
		if (object == null) fieldAccessHelper.setObjectStatic(clazz, key, value);
		else fieldAccessHelper.setObject(object, key, value);
	}

	public <R> R getObject(T object, String key) {
		return object == null ? fieldAccessHelper.getObjectStatic(clazz, key) : fieldAccessHelper.getObject(object, key);
	}

	public void setByte(T object, String key, byte value) {
		if (object == null) fieldAccessHelper.setByteStatic(clazz, key, value);
		else fieldAccessHelper.setByte(object, key, value);
	}

	public byte getByte(T object, String key) {
		if (object == null) return fieldAccessHelper.getByteStatic(clazz, key);
		else return fieldAccessHelper.getByte(object, key);
	}

	public void setShort(T object, String key, short value) {
		if (object == null) fieldAccessHelper.setShortStatic(clazz, key, value);
		else fieldAccessHelper.setShort(object, key, value);
	}

	public short getShort(T object, String key) {
		if (object == null) return fieldAccessHelper.getShortStatic(clazz, key);
		else return fieldAccessHelper.getShort(object, key);
	}

	public void setInt(T object, String key, int value) {
		if (object == null) fieldAccessHelper.setIntStatic(clazz, key, value);
		else fieldAccessHelper.setInt(object, key, value);
	}

	public int getInt(T object, String key) {
		if (object == null) return fieldAccessHelper.getIntStatic(clazz, key);
		else return fieldAccessHelper.getInt(object, key);
	}

	public void setLong(T object, String key, long value) {
		if (object == null) fieldAccessHelper.setLongStatic(clazz, key, value);
		else fieldAccessHelper.setLong(object, key, value);
	}

	public long getLong(T object, String key) {
		if (object == null) return fieldAccessHelper.getLongStatic(clazz, key);
		else return fieldAccessHelper.getLong(object, key);
	}

	public void setFloat(T object, String key, float value) {
		if (object == null) fieldAccessHelper.setFloatStatic(clazz, key, value);
		else fieldAccessHelper.setFloat(object, key, value);
	}

	public float getFloat(T object, String key) {
		if (object == null) return fieldAccessHelper.getFloatStatic(clazz, key);
		else return fieldAccessHelper.getFloat(object, key);
	}

	public void setDouble(T object, String key, double value) {
		if (object == null) fieldAccessHelper.setDoubleStatic(clazz, key, value);
		else fieldAccessHelper.setDouble(object, key, value);
	}

	public double getDouble(T object, String key) {
		if (object == null) return fieldAccessHelper.getDoubleStatic(clazz, key);
		else return fieldAccessHelper.getDouble(object, key);
	}

	public void setChar(T object, String key, char value) {
		if (object == null) fieldAccessHelper.setCharStatic(clazz, key, value);
		else fieldAccessHelper.setChar(object, key, value);
	}

	public char getChar(T object, String key) {
		if (object == null) return fieldAccessHelper.getCharStatic(clazz, key);
		else return fieldAccessHelper.getChar(object, key);
	}

	public void setBoolean(T object, String key, boolean value) {
		if (object == null) fieldAccessHelper.setBooleanStatic(clazz, key, value);
		else fieldAccessHelper.setBoolean(object, key, value);
	}

	public boolean getBoolean(T object, String key) {
		if (object == null) return fieldAccessHelper.getBooleanStatic(clazz, key);
		else return fieldAccessHelper.getBoolean(object, key);
	}
}
