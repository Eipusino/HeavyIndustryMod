package endfield.util;

import arc.func.Func;
import arc.util.OS;
import arc.util.Strings;
import endfield.Vars2;
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
	static CollectionObjectMap<Class<? extends Enum<?>>, Field> valuesFieldMap;
	static CollectionObjectMap<Class<? extends Enum<?>>, MethodHandle> valuesFieldMap2;

	static CollectionObjectMap<Class<? extends Enum<?>>, Method> valuesMethodMap;

	static CollectionObjectMap<Class<? extends Enum<?>>, Constructor<?>[]> constructMap;
	static CollectionObjectMap<Class<? extends Enum<?>>, MethodHandle[]> constructMap2;

	static final Class<?>[] defaultEnumParamType = {String.class, int.class};
	static final CollectionList<Class<?>> tmpParamType = new CollectionList<>(defaultEnumParamType);

	static final Func<Class<? extends Enum<?>>, Field> findValuesField = type -> {
		Field[] fields = Vars2.platformImpl.getFields(type);
		for (Field field : fields) {
			if (field.getName().contains("$VALUES")) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new RuntimeException(Strings.format("@.$VALUES field not found", type.getCanonicalName()));
	};

	static {
		valuesMethodMap = new CollectionObjectMap<>(Class.class, Method.class);

		if (!OS.isAndroid) {
			constructMap2 = new CollectionObjectMap<>(Class.class, MethodHandle[].class);
			valuesFieldMap2 = new CollectionObjectMap<>(Class.class, MethodHandle.class);
		} else {
			constructMap = new CollectionObjectMap<>(Class.class, Constructor[].class);
			valuesFieldMap = new CollectionObjectMap<>(Class.class, Field.class);
		}
	}

	private Enums() {}

	/**
	 * Instantiate an enumeration object with a specified name, enumeration ordinal, and constructor parameters.
	 * <p>If you don't put it in the sub item list of the enumeration, its ordinal can be arbitrarily specified,
	 * although this may cause unnecessary errors.
	 *
	 * @param name    Name of enumeration
	 * @param ordinal The ordinal number of enumeration
	 * @param param   Parameters passed to constructor. if it is an empty array or {@code null}, default
	 *                parameters will be used
	 * @return An enumeration instance with a specified name and ordinal number
	 */
	@SuppressWarnings("unchecked")
	@Contract(value = "_, _, _, _, _ -> new", pure = true)
	public static <T extends Enum<T>> T newEnumInstance(Class<T> type, String name, int ordinal, Class<?>[] paramType, Object... param) {
		Class<?>[] asType;

		if (paramType != null && paramType.length > 0) {
			tmpParamType.clear(2);
			tmpParamType.add(paramType);

			asType = tmpParamType.toArray(new Class[paramType.length + 2]);
		} else {
			asType = defaultEnumParamType;
		}

		Object[] params = new Object[param.length + 2];

		params[0] = name;
		params[1] = ordinal;

		System.arraycopy(param, 0, params, 2, param.length);

		if (!OS.isAndroid) {
			MethodHandle[] constructs = constructMap2.getDefault2(type, () -> {
				try {
					Constructor<T>[] constructors = Vars2.platformImpl.getConstructors(type);
					MethodHandle[] handles = new MethodHandle[constructors.length];
					for (int i = 0; i < constructors.length; i++) {
						Constructor<T> constructor = constructors[i];
						handles[i] = Reflects.lookup.unreflectConstructor(constructor);
					}
					return handles;
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});
			for (MethodHandle construct : constructs) {
				if (Reflects.isAssignable(asType, construct.type().parameterArray())) return (T) Reflects.invokeStatic(construct, params);
			}
		} else {
			Constructor<?>[] constructs = constructMap.getDefault2(type, () -> {
				Constructor<?>[] constructors = type.getDeclaredConstructors();
				for (Constructor<?> constructor : constructors) {
					constructor.setAccessible(true);
				}
				return constructors;
			});
			for (Constructor<?> construct : constructs) {
				if (Reflects.isAssignable(asType, construct.getParameterTypes())) {
					try {
						return (T) construct.newInstance(params);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		throw new RuntimeException(Strings.format("No method found: @", Arrays.toString(asType)));
	}

	/**
	 * Create an enumeration instance and insert it at the end of the enumeration.
	 *
	 * @param addition The name of the instance created
	 * @param param    Additional constructor parameter list
	 */
	public static <T extends Enum<T>> T addEnumItemTail(Class<T> type, String addition, Class<?>[] paramType, Object... param) {
		Method method = valuesMethodMap.getDefault2(type, () -> {
			try {
				return type.getMethod("values");
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		});

		try {
			return addEnumItem(type, addition, ((Object[]) method.invoke(null)).length, paramType, param);
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
			Method method = valuesMethodMap.getDefault2(type, () -> {
				try {
					return type.getMethod("values");
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});
			T[] array = (T[]) method.invoke(null);
			CollectionList<T> values = CollectionList.with(array);
			if (values.contains(instance) && ordinal >= values.size())
				throw new IndexOutOfBoundsException(Strings.format("rearrange a exist item, ordinal should be less than amount of all items, (ordinal: @, amount: @", ordinal, values.size()));
			else if (ordinal > values.size())
				throw new IndexOutOfBoundsException(Strings.format("add a new item, ordinal should be equal or less than amount of all items, (ordinal: @, amount: @)", ordinal, values.size()));

			values.remove(instance);

			values.add(ordinal, instance);

			T[] value = values.toArray((T[]) Array.newInstance(type, values.size()));

			if (!OS.isAndroid) {
				valuesFieldMap2.getDefault2(type, () -> {
					try {
						Field valuesField = findValuesField.get(type);
						return Reflects.lookup.findStaticSetter(type, valuesField.getName(), value.getClass());
					} catch (NoSuchFieldException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}).invoke((Object) value);
			} else {
				Field valuesField = valuesFieldMap.getDefault3(type, findValuesField);
				Unsafer.set(valuesField, null, value);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
