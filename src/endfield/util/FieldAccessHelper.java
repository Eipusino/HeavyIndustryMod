package endfield.util;

public interface FieldAccessHelper {
	void setByte(Object object, String name, byte value);

	void setByteStatic(Class<?> clazz, String name, byte value);

	byte getByte(Object object, String name);

	byte getByteStatic(Class<?> clazz, String name);

	void setShort(Object object, String name, short value);

	void setShortStatic(Class<?> clazz, String name, short value);

	short getShort(Object object, String name);

	short getShortStatic(Class<?> clazz, String name);

	void setInt(Object object, String name, int value);

	void setIntStatic(Class<?> clazz, String name, int value);

	int getInt(Object object, String name);

	int getIntStatic(Class<?> clazz, String name);

	void setLong(Object object, String name, long value);

	void setLongStatic(Class<?> clazz, String name, long value);

	long getLong(Object object, String name);

	long getLongStatic(Class<?> clazz, String name);

	void setFloat(Object object, String name, float value);

	void setFloatStatic(Class<?> clazz, String name, float value);

	float getFloat(Object object, String name);

	float getFloatStatic(Class<?> clazz, String name);

	void setDouble(Object object, String name, double value);

	void setDoubleStatic(Class<?> clazz, String name, double value);

	double getDouble(Object object, String name);

	double getDoubleStatic(Class<?> clazz, String name);

	void setChar(Object object, String name, char value);

	void setCharStatic(Class<?> clazz, String name, char value);

	char getChar(Object object, String name);

	char getCharStatic(Class<?> clazz, String name);

	void setBoolean(Object object, String name, boolean value);

	void setBooleanStatic(Class<?> clazz, String name, boolean value);

	boolean getBoolean(Object object, String name);

	boolean getBooleanStatic(Class<?> clazz, String name);

	void setObject(Object object, String name, Object value);

	void setObjectStatic(Class<?> clazz, String name, Object value);

	<T> T getObject(Object object, String name);

	<T> T getObjectStatic(Class<?> clazz, String name);

	void set(Object object, String name, Object value);

	void setStatic(Class<?> clazz, String name, Object value);

	<T> T get(Object object, String name);

	<T> T getStatic(Class<?> clazz, String name);
}
