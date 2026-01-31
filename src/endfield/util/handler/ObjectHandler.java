package endfield.util.handler;

import arc.func.Cons;
import endfield.util.CollectionObjectSet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import static endfield.Vars2.classHelper;

/** A set of practical tools for copying field properties of an object to another. */
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
		copyFieldAsBlack(source, target);
	}

	/**
	 * Copy the values of attributes of the source object that are not on the blacklist to the target object,
	 * which must be the type or subclass of the source object.
	 *
	 * @param source    Source object of attribute
	 * @param target    Copy the attribute to the target object
	 * @param blackList Field blacklist
	 */
	public static <S, T extends S> void copyFieldAsBlack(S source, T target, String... blackList) {
		Class<?> curr = source.getClass();
		Set<String> black = CollectionObjectSet.with(blackList);
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
	 * @param whiteList Field whitelist
	 */
	public static <S, T extends S> void copyFieldAsWhite(S source, T target, String... whiteList) {
		for (String s : whiteList) {
			try {
				FieldHandler.setDefault(target, s, FieldHandler.getDefault(source, s));
			} catch (Throwable e) {
				exceptionHandler.get(e);
			}
		}
	}
}
