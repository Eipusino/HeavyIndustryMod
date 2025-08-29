package heavyindustry.util.handler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The enumeration processor provides some operation methods for enum, which can create enumeration
 * instances and put them into the values of enumeration.
 * <p>This processor is an instance factory, and you need to construct an enumeration processor instance on
 * the target enumeration class in order to perform operations.
 * <p>Since the property is a private final, reflection settings are required. Here, {@link FieldHandler} is referenced to
 * complete the reflection operation. After you obtain an instance of the enumeration processor, you can
 * directly reference the methods provided by the processor.
 * <p><strong>Note that due to the mindustry source code's declaration of enumeration, the operated
 * enumeration may contain an array that holds all instances of the enumeration, but this is only when
 * values() is called during initialization.
 * <p>But this array is a copy, if you have added items to the enumeration, it is best to reassign that array
 * as the changed values().</strong>
 *
 * @since 1.0.8
 */
@SuppressWarnings("unchecked")
public class EnumHandler<T extends Enum<?>> {
	private final FieldHandler<T> fieldHandler;
	private final MethodHandler<T> methodHandler;
	private final Class<T> clazz;

	/**
	 * Construct an enumeration processor without a constructor implementation using the target
	 * enumeration type.
	 * <p>If the enumeration has a constructor implementation, you must provide an external implementation
	 * of the constructor, otherwise unexpected errors may occur.
	 *
	 * @param c The target enumeration types processed by the processor
	 */
	public EnumHandler(Class<T> c) {
		clazz = c;
		fieldHandler = new FieldHandler<>(c);
		methodHandler = new MethodHandler<>(c);
	}

	/**
	 * Instantiate an enumeration object with a specified name, enumeration ordinal, and constructor
	 * parameters.
	 * <p>If you don't put it in the sub item list of the enumeration, its ordinal can be arbitrarily specified,
	 * although this may cause unnecessary errors.
	 *
	 * @param name    Name of enumeration
	 * @param ordinal The ordinal number of enumeration
	 * @param param   Parameters passed to constructor
	 * @return An enumeration instance with a specified name and ordinal number
	 */
	public T newEnumInstance(String name, int ordinal, Object... param) {
		try {
			Object[] params = new Object[param.length + 2];

			params[0] = name;
			params[1] = ordinal;

			System.arraycopy(param, 0, params, 2, param.length);

			return methodHandler.newInstance(params);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create an enumeration instance and insert it at the end of the enumeration
	 *
	 * @param addition The name of the instance created
	 * @param param    Additional constructor parameter list
	 */
	public T addEnumItemTail(String addition, Object... param) {
		try {
			Method method = clazz.getMethod("values");
			method.setAccessible(true);
			return addEnumItem(addition, ((Object[]) method.invoke(null)).length, param);
		} catch (SecurityException | NoSuchMethodException | InvocationTargetException | IllegalArgumentException |
		         IllegalAccessException e) {
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
	public T addEnumItem(String addition, int ordinal, Object... param) {
		T newEnum = newEnumInstance(addition, ordinal, param);
		rearrange(newEnum, ordinal);
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
	 * current total number of elements in the enumeration, or if the ordinal of the newly added
	 * item is greater than the total number of elements
	 */
	public void rearrange(T instance, int ordinal) {
		Field valuesField = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().contains("$VALUES")) {
				valuesField = field;
				break;
			}
		}

		try {
			assert valuesField != null;
			Method method = clazz.getMethod("values");
			T[] arr = (T[]) method.invoke(null);
			ArrayList<T> values = new ArrayList<>(Arrays.asList(arr));
			if (values.contains(instance) && ordinal >= values.size())
				throw new IndexOutOfBoundsException("rearrange a exist item, ordinal should be less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");
			else if (ordinal > values.size())
				throw new IndexOutOfBoundsException("add a new item, ordinal should be equal or less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");

			values.remove(instance);

			values.add(ordinal, instance);

			fieldHandler.setValue(null, valuesField.getName(), values.toArray((T[]) Array.newInstance(clazz, 0)));
		} catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException |
		         InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
