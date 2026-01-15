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
package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Intp;
import arc.func.Prov;
import arc.util.Log;
import arc.util.OS;
import heavyindustry.HVars;
import mindustry.Vars;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Reflection utilities, mainly for wrapping reflective operations to eradicate checked exceptions.
 * <p>You should never frequently perform repetitive operations on the same Field/Method/Constructor for
 * performance reasons.
 * <pre>{@code
 *     private static Field theField;
 *
 *     public static MyType getVal(Object obj) {
 *         try {
 *             if (theField == null) {
 *                 theField = MyClass.class.getDeclaredField("myVar");
 *                 theField.setAccessible(true);
 *             }
 *             return (MyType) theField.get(obj);
 *         } catch (NoSuchFieldException | IllegalAccessException e) {
 *             throw new RuntimeException(e);
 *         }
 *     }
 * }</pre>
 *
 * @author Eipusino
 * @since 1.0.6
 */
public final class Reflects {
	public static Lookup lookup;

	static Field modifiersField;
	static Method cloneMethod;

	static final CollectionObjectMap<String, Field> targetFieldMap = new CollectionObjectMap<>(String.class, Field.class);

	/** Don't let anyone instantiate this class. */
	private Reflects() {}

	@Contract(pure = true)
	public static Class<?> box(Class<?> type) {
		if (type == void.class) return Void.class;
		else if (type == boolean.class) return Boolean.class;
		else if (type == byte.class) return Byte.class;
		else if (type == char.class) return Character.class;
		else if (type == short.class) return Short.class;
		else if (type == int.class) return Integer.class;
		else if (type == float.class) return Float.class;
		else if (type == long.class) return Long.class;
		else if (type == double.class) return Double.class;
		else return type;
	}

	@Contract(pure = true)
	public static Class<?> unbox(Class<?> type) {
		if (type == Void.class) return void.class;
		else if (type == Boolean.class) return boolean.class;
		else if (type == Byte.class) return byte.class;
		else if (type == Character.class) return char.class;
		else if (type == Short.class) return short.class;
		else if (type == Integer.class) return int.class;
		else if (type == Float.class) return float.class;
		else if (type == Long.class) return long.class;
		else if (type == Double.class) return double.class;
		else return type;
	}

	@Contract(pure = true)
	public static String defs(Class<?> type) {
		// boolean
		if (type == boolean.class || type == Boolean.class) return "false";
		// integer
		if (type == byte.class || type == Byte.class ||
				type == short.class || type == Short.class ||
				type == int.class || type == Integer.class ||
				type == long.class || type == Long.class ||
				type == char.class || type == Character.class) return "0";
		// float
		if (type == float.class || type == Float.class ||
				type == double.class || type == Double.class) return "0.0";
		// reference or void
		return "null";
	}

	@Contract(pure = true)
	public static Object def(Class<?> type) {
		if (type == boolean.class || type == Boolean.class) return false;
		else if (type == byte.class || type == Byte.class) return (byte) 0;
		else if (type == short.class || type == Short.class) return (short) 0;
		else if (type == int.class || type == Integer.class) return 0;
		else if (type == long.class || type == Long.class) return 0l;
		else if (type == char.class || type == Character.class) return '\u0000';
		else if (type == float.class || type == Float.class) return 0f;
		else if (type == double.class || type == Double.class) return 0d;
		else return null;
	}

	// Suitable for obtaining one field at a time.
	// If frequent reflection operations are required on the same field,
	// cache the field instead of blindly using this method.
	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public static <T> T get(Class<?> type, String name, Object object, Prov<T> def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	@Contract(pure = true)
	public static boolean getBoolean(Class<?> type, String name, Object object, Boolp def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getBoolean(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	@Contract(pure = true)
	public static int getInt(Class<?> type, String name, Object object, Intp def) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			return field.getInt(object);
		} catch (Exception e) {
			Log.err(e);
			return def.get();
		}
	}

	public static void set(Class<?> type, String name, Object object, Object value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void setInt(Class<?> type, String name, Object object, int value) {
		try {
			Field field = type.getDeclaredField(name);
			field.setAccessible(true);
			field.setInt(object, value);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static <T> T make(Class<T> type) {
		return make(type, Arrays2.arrayOf(), Arrays2.arrayOf());
	}

	/** Reflectively instantiates a type without throwing exceptions. */
	@Contract(pure = true)
	public static <T> T make(Constructor<T> cons, Object[] args) {
		try {
			return cons.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	@Contract(pure = true)
	public static <T> T make(Class<T> type, Class<?>[] parameterTypes, Object[] args) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			cons.setAccessible(true);
			return cons.newInstance(args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Prov<T> supply(Class<T> type) {
		return supply(type, Arrays2.arrayOf(), Arrays2.arrayOf());
	}

	/**
	 * Reflectively instantiates a type without throwing exceptions.
	 *
	 * @throws RuntimeException Any exception that occurs in reflection.
	 */
	@Contract(pure = true)
	public static <T> Prov<T> supply(Class<T> type, Object[] args, Class<?>[] parameterTypes) {
		try {
			Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
			return () -> {
				try {
					return cons.newInstance(args);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds class with the specified name using Mindustry's mod class loader.
	 *
	 * @param name The class' binary name, as per {@link Class#getName()}.
	 * @return The class, or {@code null} if not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> findClass(@Nullable String name) {
		try {
			return name == null ? null : (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Contract(pure = true)
	public static @Nullable Field findField(Class<?> type, String name) {
		while (type != null) {
			Field[] fields = HVars.platformImpl.getFields(type);
			for (Field field : fields) {
				if (field.getName().equals(name)) return field;
			}

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a field without throwing exceptions.
	 *
	 * @return The field, or {@code null} if not found.
	 */
	@Contract(pure = true)
	public static @Nullable Field findField(Class<?> type, Boolf<Field> filler) {
		while (type != null) {
			Field[] fields = HVars.platformImpl.getFields(type);
			for (Field field : fields) {
				if (filler.get(field)) return field;
			}

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a method without throwing exceptions.
	 *
	 * @return The method, or {@code null} if not found.
	 */
	@Contract(pure = true)
	public static @Nullable Method findMethod(Class<?> type, String name, Class<?>... args) {
		while (type != null) {
			Method[] methods = HVars.platformImpl.getMethods(type);
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), args)) return method;
			}

			type = type.getSuperclass();
		}

		return null;
	}

	/**
	 * A utility function to find a constructor without throwing exceptions.
	 *
	 * @return The constructor, or {@code null} if not found.
	 */
	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public static <T> @Nullable Constructor<T> findConstructor(Class<?> type, Class<?>... args) {
		for (type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()) {
			Constructor<?>[] constructors = HVars.platformImpl.getConstructors(type);
			for (Constructor<?> constructor : constructors) {
				if (Arrays.equals(constructor.getParameterTypes(), args)) return (Constructor<T>) constructor;
			}
		}

		return null;
	}

	@Contract(value = "null, _ -> false", pure = true)
	public static boolean isInstanceButNotSubclass(Object obj, Class<?> type) {
		if (type.isInstance(obj)) {
			try {
				if (getClassSubclassHierarchy(obj.getClass()).contains(type)) {
					return false;
				}
			} catch (ClassCastException e) {
				return false;
			}
			return true;
		}

		return false;
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Set<Class<?>> getClassSubclassHierarchy(Class<?> type) {
		Class<?> c = type.getSuperclass();
		CollectionObjectSet<Class<?>> hierarchy = new CollectionObjectSet<>(Class.class);
		while (c != Object.class) {
			hierarchy.add(c);
			Class<?>[] interfaces = c.getInterfaces();
			hierarchy.addAll(interfaces);

			c = c.getSuperclass();
		}
		return hierarchy;
	}

	@Contract(value = "_, _, _ -> param2")
	public static <T> T copyProperties(Object source, T target, List<String> filler) {
		return copyProperties(source, target, filler.toArray(Constant.EMPTY_STRING));
	}

	/**
	 * Copy the properties of an object field to another object.
	 *
	 * @param source Source Object
	 * @param target Target Object
	 * @param filler Excluded field names
	 */
	@Contract(value = "_, _, _ -> param2")
	public static <T> T copyProperties(Object source, T target, String... filler) {
		if (source == null || target == null) return target;

		targetFieldMap.clear();

		Class<?> targetClass = target.getClass();
		while (targetClass != Object.class) {
			for (Field field : HVars.platformImpl.getFields(targetClass)) {
				if (Modifier.isFinal(field.getModifiers())) continue;

				targetFieldMap.put(field.getName(), field);
			}

			targetClass = targetClass.getSuperclass();
		}

		for (String name : filler) {
			targetFieldMap.remove(name);
		}

		Class<?> sourceClass = source.getClass();
		while (sourceClass != null) {
			for (Field sourceField : HVars.platformImpl.getFields(sourceClass)) {
				if (Modifier.isFinal(sourceField.getModifiers())) continue;

				Field targetField = targetFieldMap.get(sourceField.getName());

				if (!isAssignable(sourceField, targetField)) continue;

				try {
					if (OS.isAndroid) {
						Object value = Unsafer.get(sourceField, source);
						Unsafer.set(targetField, target, value);
					} else {
						sourceField.setAccessible(true);
						targetField.setAccessible(true);

						Object value = sourceField.get(source);
						targetField.set(target, value);
					}
				} catch (Exception e) {
					Log.err(e);
				}
			}

			sourceClass = sourceClass.getSuperclass();
		}

		return target;
	}

	@Contract(pure = true)
	public static boolean isAssignable(@Nullable Field sourceType, @Nullable Field targetType) {
		return sourceType != null && targetType != null && targetType.getType().isAssignableFrom(sourceType.getType());
	}

	/**
	 * Attempt to directly call the clone method of Object.
	 * <p>If the class does not implement the {@code Cloneable} interface, it will throw
	 * {@code CloneNotSupportedException}. But on the Android platform, this restriction can be bypassed.
	 *
	 * @since 1.0.9
	 */
	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public static <T> T clone(T object) {
		try {
			if (cloneMethod == null) {
				// On Android, it should be possible to bypass the Cloneable interface restrictions and directly clone.
				cloneMethod = Object.class.getDeclaredMethod(OS.isAndroid ? "internalClone" : "clone");
				cloneMethod.setAccessible(true);
			}
			return (T) cloneMethod.invoke(object);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Remove the {@code final} attribute of the field.
	 * <p><strong>If Anuke's code wasn't that bad, we would never have used such extreme reflection.</strong>
	 *
	 * @since 1.0.9
	 */
	public static void setModifiers(Field field) {
		if (Modifier.isFinal(field.getModifiers())) {
			try {
				if (modifiersField == null) {
					modifiersField = Field.class.getDeclaredField(OS.isAndroid ? "accessFlags" : "modifiers");
					modifiersField.setAccessible(true);
				}
				modifiersField.setInt(field, modifiersField.getInt(field) & (~Modifier.FINAL));
			} catch (NoSuchFieldException | IllegalAccessException e) {
				Log.err(e);
			}
		}
	}

	/**
	 * Call {@code MethodHandle.invoke(Object...)} using a parameter array.
	 *
	 * @since 1.0.9
	 */
	public static Object invokeStatic(MethodHandle handle, Object... args) {
		try {
			return switch (args.length) {
				case 0 -> handle.invoke();
				case 1 -> handle.invoke(args[0]);
				case 2 -> handle.invoke(args[0], args[1]);
				case 3 -> handle.invoke(args[0], args[1], args[2]);
				case 4 -> handle.invoke(args[0], args[1], args[2], args[3]);
				case 5 -> handle.invoke(args[0], args[1], args[2], args[3], args[4]);
				case 6 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
				case 7 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
				case 8 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
				case 9 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
				case 10 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9]);
				case 11 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10]);
				case 12 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11]);
				case 13 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12]);
				case 14 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13]);
				case 15 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14]);
				case 16 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
				case 17 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
				case 18 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
				case 19 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
				case 20 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19]);
				case 21 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20]);
				case 22 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21]);
				case 23 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22]);
				case 24 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23]);
				case 25 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24]);
				case 26 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
				case 27 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
				case 28 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
				case 29 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
				case 30 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29]);
				case 31 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29], args[30]);
				case 32 ->
						handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29], args[30], args[31]);
				default -> handle.invokeWithArguments(args);
			};
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Call {@code MethodHandle.invoke(Object...)} using a parameter array.
	 *
	 * @since 1.0.9
	 */
	public static Object invokeVirtual(@NotNull Object object, MethodHandle handle, Object... args) {
		try {
			return switch (args.length) {
				case 0 -> handle.invoke(object);
				case 1 -> handle.invoke(object, args[0]);
				case 2 -> handle.invoke(object, args[0], args[1]);
				case 3 -> handle.invoke(object, args[0], args[1], args[2]);
				case 4 -> handle.invoke(object, args[0], args[1], args[2], args[3]);
				case 5 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4]);
				case 6 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5]);
				case 7 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
				case 8 -> handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
				case 9 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
				case 10 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9]);
				case 11 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10]);
				case 12 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11]);
				case 13 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12]);
				case 14 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13]);
				case 15 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14]);
				case 16 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
				case 17 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
				case 18 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
				case 19 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
				case 20 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19]);
				case 21 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20]);
				case 22 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21]);
				case 23 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22]);
				case 24 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23]);
				case 25 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24]);
				case 26 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
				case 27 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
				case 28 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
				case 29 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
				case 30 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29]);
				case 31 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29], args[30]);
				case 32 ->
						handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
								args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
								args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
								args[29], args[30], args[31]);
				default -> handle.invokeWithArguments(args);
			};
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
