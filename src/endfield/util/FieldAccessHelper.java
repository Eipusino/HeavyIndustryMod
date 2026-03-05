package endfield.util;

import java.lang.reflect.Field;

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

	default void setByte(Object object, Field field, byte value) {
		try {
			field.setByte(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setByteStatic(Field field, byte value) {
		try {
			field.setByte(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default byte getByte(Object object, Field field) {
		try {
			return field.getByte(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default byte getByteStatic(Field field) {
		try {
			return field.getByte(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setShort(Object object, Field field, short value) {
		try {
			field.setShort(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setShortStatic(Field field, short value) {
		try {
			field.setShort(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default short getShort(Object object, Field field) {
		try {
			return field.getShort(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default short getShortStatic(Field field) {
		try {
			return field.getShort(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setInt(Object object, Field field, int value) {
		try {
			field.setInt(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setIntStatic(Field field, int value) {
		try {
			field.setInt(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default int getInt(Object object, Field field) {
		try {
			return field.getInt(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default int getIntStatic(Field field) {
		try {
			return field.getInt(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setLong(Object object, Field field, long value) {
		try {
			field.setLong(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setLongStatic(Field field, long value) {
		try {
			field.setLong(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default long getLong(Object object, Field field) {
		try {
			return field.getLong(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default long getLongStatic(Field field) {
		try {
			return field.getLong(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setFloat(Object object, Field field, float value) {
		try {
			field.setFloat(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setFloatStatic(Field field, float value) {
		try {
			field.setFloat(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default float getFloat(Object object, Field field) {
		try {
			return field.getFloat(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default float getFloatStatic(Field field) {
		try {
			return field.getFloat(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setDouble(Object object, Field field, double value) {
		try {
			field.setDouble(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setDoubleStatic(Field field, double value) {
		try {
			field.setDouble(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default double getDouble(Object object, Field field) {
		try {
			return field.getDouble(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default double getDoubleStatic(Field field) {
		try {
			return field.getDouble(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setChar(Object object, Field field, char value) {
		try {
			field.setChar(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setCharStatic(Field field, char value) {
		try {
			field.setChar(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default char getChar(Object object, Field field) {
		try {
			return field.getChar(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default char getCharStatic(Field field) {
		try {
			return field.getChar(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setBoolean(Object object, Field field, boolean value) {
		try {
			field.setBoolean(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setBooleanStatic(Field field, boolean value) {
		try {
			field.setBoolean(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default boolean getBoolean(Object object, Field field) {
		try {
			return field.getBoolean(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default boolean getBooleanStatic(Field field) {
		try {
			return field.getBoolean(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setObject(Object object, Field field, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setObjectStatic(Field field, Object value) {
		try {
			field.set(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getObject(Object object, Field field) {
		try {
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getObjectStatic(Field field) {
		try {
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void set(Object object, Field field, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setStatic(Field field, Object value) {
		try {
			field.set(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T get(Object object, Field field) {
		try {
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getStatic(Field field) {
		try {
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
