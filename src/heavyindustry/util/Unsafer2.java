package heavyindustry.util;

import jdk.internal.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import static heavyindustry.util.ObjectUtils.requireInstance;
import static heavyindustry.util.ObjectUtils.requireNonNullInstance;

/**
 * JDK-Internal-Unsafe class. It may become invalid in future Java or Android versions.
 * inconsistently on different devices, and is not recommended for non-essential use.
 * <p>The {@link #internalUnsafe} field of this class is public, but it is generally recommended to use the static
 * methods provided by the class, which perform some security check packaging on the methods inside the
 * {@code Unsafe} to avoid causing strange bugs or even <b>JVM crashes</b>. Unless you
 * are very clear about what you are doing at this moment, it is not recommended to use the {@link #internalUnsafe} field directly.
 *
 * @author Eipusino
 * @see Unsafer
 * @since 1.0.8
 */
public final class Unsafer2 {
	/** Initialize in libs/Impl.jar in the mod resource package. */
	public static Unsafe internalUnsafe;

	private Unsafer2() {}

	public static <T> T getObject(Class<?> type, String name, Object object) {
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
	public static <T> T getObject(Field field, Object object) {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("Method 'getObject' does not support field of primitive types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return (T) (Modifier.isVolatile(modifiers) ?
				internalUnsafe.getReferenceVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getReference(isStatic ? type : requireNonNullInstance(type, object), offset));
	}

	public static void setObject(Class<?> type, String name, Object object, Object value) {
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
	 * @throws IllegalArgumentException If any of the following  is true:
	 *                                  <ul><li>If the field type is a primitive type.
	 *                                  <li>If the field is not {@code static} and the {@code object} is not an
	 *                                                                   instance of {@code field.getDeclaringClass()} or {@code null}.</ul>
	 */
	public static void setObject(Field field, Object object, Object value) {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("Method 'getObject' does not support field of primitive types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putReferenceVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		} else {
			internalUnsafe.putReference(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		}
	}

	public static Object get(Class<?> type, String name, Object object) {
		try {
			return get(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object get(Field field, Object object) {
		long offset = getOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = Modifier.isStatic(field.getModifiers()) ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(field.getModifiers())) {
			if (type.isPrimitive()) {
				if (type == int.class) return internalUnsafe.getIntVolatile(o, offset);
				else if (type == float.class) return internalUnsafe.getFloatVolatile(o, offset);
				else if (type == boolean.class) return internalUnsafe.getBooleanVolatile(o, offset);
				else if (type == byte.class) return internalUnsafe.getByteVolatile(o, offset);
				else if (type == long.class) return internalUnsafe.getLongVolatile(o, offset);
				else if (type == double.class) return internalUnsafe.getDoubleVolatile(o, offset);
				else if (type == char.class) return internalUnsafe.getCharVolatile(o, offset);
				else if (type == short.class) return internalUnsafe.getShortVolatile(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return internalUnsafe.getReferenceVolatile(o, offset);
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) return internalUnsafe.getInt(o, offset);
				else if (type == float.class) return internalUnsafe.getFloat(o, offset);
				else if (type == boolean.class) return internalUnsafe.getBoolean(o, offset);
				else if (type == byte.class) return internalUnsafe.getByte(o, offset);
				else if (type == long.class) return internalUnsafe.getDouble(o, offset);
				else if (type == double.class) return internalUnsafe.getLong(o, offset);
				else if (type == char.class) return internalUnsafe.getChar(o, offset);
				else if (type == short.class) return internalUnsafe.getShort(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return internalUnsafe.getReference(o, offset);
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

	public static void set(Field field, Object object, Object value) {
		long offset = getOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = Modifier.isStatic(field.getModifiers()) ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(field.getModifiers())) {
			if (type.isPrimitive()) {
				if (type == int.class) internalUnsafe.putIntVolatile(o, offset, (int) value);
				else if (type == float.class) internalUnsafe.putFloatVolatile(o, offset, (float) value);
				else if (type == boolean.class) internalUnsafe.putBooleanVolatile(o, offset, (boolean) value);
				else if (type == byte.class) internalUnsafe.putByteVolatile(o, offset, (byte) value);
				else if (type == long.class) internalUnsafe.putLongVolatile(o, offset, (long) value);
				else if (type == double.class) internalUnsafe.putDoubleVolatile(o, offset, (double) value);
				else if (type == char.class) internalUnsafe.putCharVolatile(o, offset, (char) value);
				else if (type == short.class) internalUnsafe.putShortVolatile(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				internalUnsafe.putReferenceVolatile(o, offset, requireInstance(field.getType(), value));
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) internalUnsafe.putInt(o, offset, (int) value);
				else if (type == float.class) internalUnsafe.putFloat(o, offset, (float) value);
				else if (type == boolean.class) internalUnsafe.putBoolean(o, offset, (boolean) value);
				else if (type == byte.class) internalUnsafe.putByte(o, offset, (byte) value);
				else if (type == double.class) internalUnsafe.putDouble(o, offset, (double) value);
				else if (type == long.class) internalUnsafe.putLong(o, offset, (long) value);
				else if (type == char.class) internalUnsafe.putChar(o, offset, (char) value);
				else if (type == short.class) internalUnsafe.putShort(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				internalUnsafe.putReference(o, offset, requireInstance(field.getType(), value));
			}
		}
	}

	public static Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) {
		return defineClass(name, bytes, loader, null);
	}

	public static Class<?> defineClass(String name, byte[] bytes, ClassLoader loader, ProtectionDomain protectionDomain) {
		return defineClass(name, bytes, 0, loader, protectionDomain);
	}

	public static Class<?> defineClass(String name, byte[] bytes, int offset, ClassLoader loader, ProtectionDomain protectionDomain) {
		return internalUnsafe.defineClass(name, bytes, offset, bytes.length, loader, protectionDomain);
	}

	public static long getOffset(Field field) {
		return Modifier.isStatic(field.getModifiers()) ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);
	}
}
