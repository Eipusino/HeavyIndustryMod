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

	default void setByte(Object object, Field field, byte value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setByte(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setByteStatic(Field field, byte value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setByte(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default byte getByte(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getByte(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default byte getByteStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getByte(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setShort(Object object, Field field, short value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setShort(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setShortStatic(Field field, short value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setShort(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default short getShort(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getShort(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default short getShortStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getShort(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setInt(Object object, Field field, int value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setInt(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setIntStatic(Field field, int value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setInt(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default int getInt(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getInt(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default int getIntStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getInt(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setLong(Object object, Field field, long value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setLong(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setLongStatic(Field field, long value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setLong(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default long getLong(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getLong(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default long getLongStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getLong(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setFloat(Object object, Field field, float value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setFloat(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setFloatStatic(Field field, float value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setFloat(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default float getFloat(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getFloat(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default float getFloatStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getFloat(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setDouble(Object object, Field field, double value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setDouble(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setDoubleStatic(Field field, double value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setDouble(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default double getDouble(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getDouble(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default double getDoubleStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getDouble(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setChar(Object object, Field field, char value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setChar(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setCharStatic(Field field, char value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setChar(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default char getChar(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getChar(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default char getCharStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getChar(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setBoolean(Object object, Field field, boolean value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setBoolean(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setBooleanStatic(Field field, boolean value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.setBoolean(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default boolean getBoolean(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getBoolean(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default boolean getBooleanStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return field.getBoolean(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setObject(Object object, Field field, Object value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setObjectStatic(Field field, Object value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.set(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getObject(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getObjectStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void set(Object object, Field field, Object value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default void setStatic(Field field, Object value, boolean access) {
		try {
			if (access) field.setAccessible(true);
			field.set(null, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T get(Object object, Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return (T) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getStatic(Field field, boolean access) {
		try {
			if (access) field.setAccessible(true);
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
