/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package endfield.util;

import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static endfield.Vars2.platformImpl;
import static endfield.util.Objects2.requireInstance;
import static endfield.util.Objects2.requireNonNullInstance;

/**
 * {@code Unsafe} reflection tool. Mainly provides functions for modifying or setting field values.
 * <p>This class behavior will violate the access protection of Java security encapsulation and is inherently
 * insecure. If it is not necessary, please try to avoid using this class.
 * <p>The {@link #unsafe} field of this class is public, but it is generally recommended to use the static methods
 * provided by the class, which perform some security check packaging on the methods inside the {@link Unsafe}
 * to avoid causing strange bugs or even <strong>JVM crashes</strong>. Unless you are very clear about what you are doing
 * at this moment, it is not recommended to use the {@link #unsafe} field directly.
 *
 * @author Eipusino
 * @since 1.0.7
 */
public final class Unsafer {
	/** Initialize in libs/Impl.jar in the mod resource package. */
	public static Unsafe unsafe;

	/** Do not call. */
	private Unsafer() {}

	public static <T> T getObject(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getObject(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the value of a field through {@code Unsafe}. If the field is {@code static}, object can be {@code null}.
	 * Otherwise, {@code object} must not be {@code null} and be an instance of {@code field.getDeclaringClass()}.
	 *
	 * @throws IllegalArgumentException If any of the following  is true:
	 *                                  <ul><li>If the field type is a primitive type.
	 *                                  <li>If the field is not {@code static} and the {@code object} is not an
	 *                                                                   instance of {@code field.getDeclaringClass()} or {@code null}.</ul>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return (T) (Modifier.isVolatile(modifiers) ?
				unsafe.getObjectVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getObject(isStatic ? type : requireNonNullInstance(type, object), offset));
	}

	public static boolean getBool(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getBool(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the value of a field through {@code Unsafe}. If the field is {@code static}, object can be {@code null}.
	 * Otherwise, {@code object} must not be {@code null} and be an instance of {@code field.getDeclaringClass()}.
	 *
	 * @throws IllegalArgumentException If any of the following  is true:
	 *                                  <ul><li>If the field type is not boolean.
	 *                                  <li>If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.</ul>
	 */
	public static boolean getBool(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getBoolean(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static byte getByte(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getByte(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte getByte(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != byte.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getByte(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static short getShort(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getShort(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static short getShort(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != short.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getShort(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static int getInt(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getInt(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getInt(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != int.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getInt(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static long getLong(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getLong(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getLong(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != long.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getLong(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static float getFloat(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getFloat(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static float getFloat(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != float.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getFloat(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static double getDouble(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getDouble(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static double getDouble(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != double.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getDouble(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static char getChar(Class<?> type, String name, Object object) throws IllegalArgumentException {
		try {
			return getChar(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static char getChar(@NotNull Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != char.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getChar(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static void setObject(Class<?> type, String name, Object object, Object value) throws IllegalArgumentException {
		try {
			setObject(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the value of a field through {@code Unsafe}. If the field is {@code static}, object can be {@code null}.
	 * Otherwise, {@code object} must not be {@code null} and be an instance of {@code field.getDeclaringClass()}.
	 *
	 * @throws IllegalArgumentException If the field type is a primitive type.
	 * @throws ClassCastException If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.
	 */
	public static void setObject(@NotNull Field field, Object object, Object value) throws IllegalArgumentException {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putObjectVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		} else {
			unsafe.putObject(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		}
	}

	public static void setBool(Class<?> type, String name, Object object, boolean value) throws IllegalArgumentException {
		try {
			setBool(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setBool(@NotNull Field field, Object object, boolean value) throws IllegalArgumentException {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putBoolean(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setByte(Class<?> type, String name, Object object, byte value) throws IllegalArgumentException {
		try {
			setByte(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setByte(@NotNull Field field, Object object, byte value) throws IllegalArgumentException {
		if (field.getType() != byte.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putByte(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setShort(Class<?> type, String name, Object object, short value) throws IllegalArgumentException {
		try {
			setShort(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setShort(@NotNull Field field, Object object, short value) throws IllegalArgumentException {
		if (field.getType() != short.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putShort(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setInt(Class<?> type, String name, Object object, int value) throws IllegalArgumentException {
		try {
			setInt(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInt(@NotNull Field field, Object object, int value) throws IllegalArgumentException {
		if (field.getType() != int.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putInt(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setLong(Class<?> type, String name, Object object, long value) throws IllegalArgumentException {
		try {
			setLong(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setLong(@NotNull Field field, Object object, long value) throws IllegalArgumentException {
		if (field.getType() != long.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putLong(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setFloat(Class<?> type, String name, Object object, float value) throws IllegalArgumentException {
		try {
			setFloat(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFloat(@NotNull Field field, Object object, float value) throws IllegalArgumentException {
		if (field.getType() != float.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putFloat(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setDouble(Class<?> type, String name, Object object, double value) throws IllegalArgumentException {
		try {
			setDouble(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setDouble(@NotNull Field field, Object object, double value) throws IllegalArgumentException {
		if (field.getType() != double.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putDouble(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setChar(Class<?> type, String name, Object object, char value) throws IllegalArgumentException {
		try {
			setChar(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setChar(@NotNull Field field, Object object, char value) throws IllegalArgumentException {
		if (field.getType() != char.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putChar(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static Object get(Class<?> type, String name, Object object) {
		try {
			return get(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object get(@NotNull Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = isStatic ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(modifiers)) {
			if (type.isPrimitive()) {
				if (type == int.class) return unsafe.getIntVolatile(o, offset);
				else if (type == float.class) return unsafe.getFloatVolatile(o, offset);
				else if (type == boolean.class) return unsafe.getBooleanVolatile(o, offset);
				else if (type == byte.class) return unsafe.getByteVolatile(o, offset);
				else if (type == long.class) return unsafe.getLongVolatile(o, offset);
				else if (type == double.class) return unsafe.getDoubleVolatile(o, offset);
				else if (type == char.class) return unsafe.getCharVolatile(o, offset);
				else if (type == short.class) return unsafe.getShortVolatile(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return unsafe.getObjectVolatile(o, offset);
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) return unsafe.getInt(o, offset);
				else if (type == float.class) return unsafe.getFloat(o, offset);
				else if (type == boolean.class) return unsafe.getBoolean(o, offset);
				else if (type == byte.class) return unsafe.getByte(o, offset);
				else if (type == long.class) return unsafe.getDouble(o, offset);
				else if (type == double.class) return unsafe.getLong(o, offset);
				else if (type == char.class) return unsafe.getChar(o, offset);
				else if (type == short.class) return unsafe.getShort(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + type);
			} else {
				return unsafe.getObject(o, offset);
			}
		}
	}

	public static void set(Class<?> type, String name, Object object, Object value) {
		try {
			set(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void set(@NotNull Field field, Object object, Object value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = isStatic ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(modifiers)) {
			if (type.isPrimitive()) {
				if (type == int.class) unsafe.putIntVolatile(o, offset, (int) value);
				else if (type == float.class) unsafe.putFloatVolatile(o, offset, (float) value);
				else if (type == boolean.class) unsafe.putBooleanVolatile(o, offset, (boolean) value);
				else if (type == byte.class) unsafe.putByteVolatile(o, offset, (byte) value);
				else if (type == long.class) unsafe.putLongVolatile(o, offset, (long) value);
				else if (type == double.class) unsafe.putDoubleVolatile(o, offset, (double) value);
				else if (type == char.class) unsafe.putCharVolatile(o, offset, (char) value);
				else if (type == short.class) unsafe.putShortVolatile(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				unsafe.putObjectVolatile(o, offset, requireInstance(field.getType(), value));
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) unsafe.putInt(o, offset, (int) value);
				else if (type == float.class) unsafe.putFloat(o, offset, (float) value);
				else if (type == boolean.class) unsafe.putBoolean(o, offset, (boolean) value);
				else if (type == byte.class) unsafe.putByte(o, offset, (byte) value);
				else if (type == double.class) unsafe.putDouble(o, offset, (double) value);
				else if (type == long.class) unsafe.putLong(o, offset, (long) value);
				else if (type == char.class) unsafe.putChar(o, offset, (char) value);
				else if (type == short.class) unsafe.putShort(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				unsafe.putObject(o, offset, requireInstance(field.getType(), value));
			}
		}
	}

	public static void init() throws NoSuchFieldException, IllegalAccessException {
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		unsafe = (Unsafe) field.get(null);
	}
}
