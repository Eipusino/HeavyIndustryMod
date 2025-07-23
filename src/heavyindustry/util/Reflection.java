package heavyindustry.util;

import arc.struct.ObjectMap;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class Reflection {
	public static ObjectMap<String, Field> targetFieldMap = new ObjectMap<>();

	private Reflection() {}

	public static Unsafe getUnsafe() {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			return (Unsafe) field.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyProperties(Object source, Object target) {
		try {
			targetFieldMap.clear();

			Class<?> targetClass = target.getClass();
			while (targetClass != null) {
				for (Field field : targetClass.getDeclaredFields()) {
					if (!Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						targetFieldMap.put(field.getName(), field);
					}
				}
				targetClass = targetClass.getSuperclass();
			}

			targetFieldMap.remove("id");

			Class<?> sourceClass = source.getClass();
			while (sourceClass != null) {
				for (Field sourceField : sourceClass.getDeclaredFields()) {
					if (Modifier.isFinal(sourceField.getModifiers())) {
						continue;
					}
					sourceField.setAccessible(true);

					Field targetField = targetFieldMap.get(sourceField.getName());
					if (targetField != null && isAssignable(sourceField.getType(), targetField.getType())) {
						Object value = sourceField.get(source);
						targetField.set(target, value);
					}
				}
				sourceClass = sourceClass.getSuperclass();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isAssignable(Class<?> sourceType, Class<?> targetType) {
		return targetType.isAssignableFrom(sourceType);
	}
}
