package heavyindustry.util;

import arc.util.OS;
import heavyindustry.HVars;
import org.jetbrains.annotations.Contract;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The enumeration processor provides some operation methods for enum, which can create enumeration
 * instances and put them into the {@code values()} of enumeration.
 * <p>Due to the property being {@code private} and {@code final}, reflection settings are required
 * <p><strong>Note that due to the mindustry source code's declaration of enumeration, the operated
 * enumeration may contain an array that holds all instances of the enumeration, but this is only when
 * {@code values()} is called during initialization.
 * <p>But this array is a copy, if you have added items to the enumeration, it is best to reassign that array
 * as the changed {@code values()}.</strong>
 * <p><strong>If Anuke's code wasn't that bad, we would never have used such extreme reflection.</strong>
 *
 * @author EBwilson
 * @author Eipusino
 * @since 1.0.9
 */
public final class Enums {
	static final CollectionObjectMap<Class<? extends Enum<?>>, Object> valuesFields = new CollectionObjectMap<>(Class.class, Object.class);
	static final CollectionObjectMap<Class<? extends Enum<?>>, Method> valuesMethods = new CollectionObjectMap<>(Class.class, Method.class);
	static final CollectionObjectMap<Class<? extends Enum<?>>, Object[]> enumConstructors = new CollectionObjectMap<>(Class.class, Object[].class);

	private Enums() {}

	/**
	 * Instantiate an enumeration object with a specified name, enumeration ordinal, and constructor parameters.
	 * <p>If you don't put it in the sub item list of the enumeration, its ordinal can be arbitrarily specified,
	 * although this may cause unnecessary errors.
	 *
	 * @param name    Name of enumeration
	 * @param ordinal The ordinal number of enumeration
	 * @param param   Parameters passed to constructor
	 * @return An enumeration instance with a specified name and ordinal number
	 */
	@SuppressWarnings("unchecked")
	@Contract(value = "_, _, _, _, _ -> new", pure = true)
	public static <T extends Enum<T>> T newEnumInstance(Class<T> type, String name, int ordinal, Class<?>[] paramType, Object... param) {
		if (paramType == null || param.length != paramType.length)
			throw new IllegalArgumentException("paramType: " + Arrays.toString(paramType) + " param: " + Arrays.toString(param));

		Class<?>[] asParamTypes = new Class[paramType.length + 2];

		asParamTypes[0] = String.class;
		asParamTypes[1] = int.class;

		System.arraycopy(paramType, 0, asParamTypes, 2, paramType.length);

		Object[] params = new Object[param.length + 2];

		params[0] = name;
		params[1] = ordinal;

		System.arraycopy(param, 0, params, 2, param.length);

		try {
			if (!OS.isAndroid) {
				return (T) Reflects.invokeStatic(Arrays2.findOrThrow((MethodHandle[]) enumConstructors.get(type, () -> {
					try {
						Constructor<T>[] constructors = HVars.platformImpl.getConstructors(type);
						MethodHandle[] handles = new MethodHandle[constructors.length];
						for (int i = 0; i < constructors.length; i++) {
							Constructor<T> constructor = constructors[i];
							handles[i] = Reflects.lookup.unreflectConstructor(constructor);
						}
						return handles;
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}), h -> Reflects.isAssignable(asParamTypes, h.type().parameterArray())), params);
			} else {
				return (T) Arrays2.findOrThrow((Constructor<?>[]) enumConstructors.get(type, () -> {
					Constructor<?>[] constructors = type.getDeclaredConstructors();
					for (Constructor<?> constructor : constructors) {
						constructor.setAccessible(true);
					}
					return constructors;
				}), c -> Reflects.isAssignable(asParamTypes, c.getParameterTypes())).newInstance(params);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create an enumeration instance and insert it at the end of the enumeration.
	 *
	 * @param addition The name of the instance created
	 * @param param    Additional constructor parameter list
	 */
	public static <T extends Enum<T>> T addEnumItemTail(Class<T> type, String addition, Class<?>[] paramType, Object... param) {
		Method method = valuesMethods.get(type, () -> {
			try {
				return type.getMethod("values");
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		});

		try {
			int ordinal = ((Object[]) method.invoke(null)).length;
			return addEnumItem(type, addition, ordinal, paramType, param);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create an enumeration instance and insert it into the specified location in the enumeration item list.
	 * <p>The enumeration instances in the original position and the instances behind them will be moved
	 * backwards, and the enumeration ordinals will be updated synchronously with the updates of the list.
	 *
	 * @param addition The name of the instance created
	 * @param ordinal  The ordinal number corresponding to the insertion position
	 * @param param    Additional constructor parameter list
	 */
	public static <T extends Enum<T>> T addEnumItem(Class<T> type, String addition, int ordinal, Class<?>[] paramType, Object... param) {
		T newEnum = newEnumInstance(type, addition, ordinal, paramType, param);
		rearrange(type, newEnum, ordinal);
		return newEnum;
	}

	/**
	 * Place the specified enumeration item in the specified ordinal position, and reset all enumeration
	 * item ordinals to their correct positions.
	 * <p>The enumeration ordinal must be correctly specified between 0 and the total number of
	 * enumeration items, and the ordinal number of newly added items can be equal to the current total
	 * number.
	 *
	 * @param instance The target enumeration instance to be inserted
	 * @param ordinal  The enumeration ordinal of the target position to be inserted
	 * @throws IndexOutOfBoundsException If the specified ordinal is greater than or equal to the
	 *                                   current total number of elements in the enumeration, or if the ordinal of the newly added
	 *                                   item is greater than the total number of elements
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> void rearrange(Class<T> type, T instance, int ordinal) {
		try {
			Method method = valuesMethods.get(type, () -> {
				try {
					return type.getMethod("values");
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});
			T[] array = (T[]) method.invoke(null);
			CollectionList<T> values = CollectionList.with(array);
			if (values.contains(instance) && ordinal >= values.size())
				throw new IndexOutOfBoundsException("rearrange a exist item, ordinal should be less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");
			else if (ordinal > values.size())
				throw new IndexOutOfBoundsException("add a new item, ordinal should be equal or less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");

			values.remove(instance);

			values.add(ordinal, instance);

			T[] value = values.toArray((T[]) Array.newInstance(type, 0));

			if (!OS.isAndroid) {
				((MethodHandle) valuesFields.get(type, () -> {
					try {
						Field valuesField = findValuesField(type);
						return Reflects.lookup.findStaticSetter(type, valuesField.getName(), value.getClass());
					} catch (NoSuchFieldException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})).invoke((Object) value);
			} else {
				Field valuesField = (Field) valuesFields.get(type, () -> findValuesField(type));
				//valuesField.set(null, value);
				Unsafer.set(valuesField, null, value);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	static Field findValuesField(Class<? extends Enum<?>> type) {
		Field[] fields = HVars.platformImpl.getFields(type);
		for (Field field : fields) {
			if (field.getName().contains("$VALUES")) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new RuntimeException("$VALUES field not found");
	}
}
