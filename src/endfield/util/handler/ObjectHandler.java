package endfield.util.handler;

import arc.func.Boolf;
import endfield.util.CollectionList;
import endfield.util.CollectionObjectSet;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import static endfield.Vars2.classHelper;

/**
 * A set of practical tools for copying field properties of an object to another.
 *
 * @since 1.0.9
 */
public final class ObjectHandler {
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
		List<Field> fields = getFields(source, null);

		for (Field field : fields) {
			FieldHandler.set(target, field, FieldHandler.get(source, field, true), true);
		}
	}

	/**
	 * Copy the values of attributes of the source object that are not on the blacklist to the target object,
	 * which must be the type or subclass of the source object.
	 *
	 * @param source Source object of attribute
	 * @param target Copy the attribute to the target object
	 * @param blacks Field blacklist
	 */
	public static <S, T extends S> void copyFieldAsBlack(S source, T target, String... blacks) {
		Set<String> black = CollectionObjectSet.with(blacks);
		List<Field> fields = getFields(source, field -> !black.contains(field.getName()));

		for (Field field : fields) {
			FieldHandler.set(target, field, FieldHandler.get(source, field, true), true);
		}
	}

	/**
	 * Copy the specified attribute values of the source object to the target object, which must be the type
	 * or subclass of the source object.
	 *
	 * @param source Source object of attribute
	 * @param target Copy the attribute to the target object
	 * @param whites Field whitelist
	 */
	public static <S, T extends S> void copyFieldAsWhite(S source, T target, String... whites) {
		Set<String> black = CollectionObjectSet.with(whites);
		List<Field> fields = getFields(source, field -> black.contains(field.getName()));

		for (Field field : fields) {
			FieldHandler.set(target, field, FieldHandler.get(source, field, true), true);
		}
	}

	public static List<Field> getFields(Object object, @Nullable Boolf<Field> filler) {
		Class<?> curr = object.getClass();
		List<Field> fields = new CollectionList<>(Field.class);

		while (curr != Object.class) {
			for (Field field : classHelper.getFields(curr)) {
				if ((field.getModifiers() & Modifier.STATIC) != 0 && filler != null && !filler.get(field)) continue;

				fields.add(field);
			}

			curr = curr.getSuperclass();
		}
		return fields;
	}
}
