package endfield.util.handler;

import arc.func.Cons;
import endfield.util.CollectionObjectSet;
import endfield.util.Constant;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import static endfield.Vars2.classHelper;

/**
 * A set of practical tools for copying field properties of an object to another.
 *
 * @since 1.0.9
 */
public final class ObjectHandler {
	public static Cons<Throwable> exceptionHandler = e -> {};

	private ObjectHandler() {}

	/**
	 * Copy all attribute values of the source object completely to the target object, which must be the
	 * type or subclass of the source object.
	 *
	 * @param source Source object of attribute
	 * @param target Copy the attribute to the target object
	 * @throws IllegalArgumentException If the target object is not assigned from the source class
	 */
	public static <S, T extends S> void copyField(S source, T target) {
		copyFieldAsBlack(source, target, Constant.EMPTY_STRING);
	}

	/**
	 * Copy the values of attributes of the source object that are not on the blacklist to the target object,
	 * which must be the type or subclass of the source object.
	 *
	 * @param source    Source object of attribute
	 * @param target    Copy the attribute to the target object
	 * @param blacks    Field blacklist
	 */
	public static <S, T extends S> void copyFieldAsBlack(S source, T target, String... blacks) {
		Class<?> curr = source.getClass();
		Set<String> black = CollectionObjectSet.with(blacks);
		Set<String> fields = new CollectionObjectSet<>(String.class);

		while (curr != Object.class) {
			for (Field field : classHelper.getFields(curr)) {
				if (Modifier.isStatic(field.getModifiers())) continue;

				fields.add(field.getName());
			}

			curr = curr.getSuperclass();
		}

		copyFieldAsWhite(source, target, fields.stream().filter(e -> !black.contains(e)).toArray(String[]::new));
	}

	/**
	 * Copy the specified attribute values of the source object to the target object, which must be the type
	 * or subclass of the source object.
	 *
	 * @param source    Source object of attribute
	 * @param target    Copy the attribute to the target object
	 * @param whites    Field whitelist
	 */
	public static <S, T extends S> void copyFieldAsWhite(S source, T target, String... whites) {
		for (String white : whites) {
			try {
				FieldHandler.setDefault(target, white, FieldHandler.getDefault(source, white));
			} catch (Throwable e) {
				exceptionHandler.get(e);
			}
		}
	}
}
