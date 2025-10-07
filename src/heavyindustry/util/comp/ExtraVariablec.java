package heavyindustry.util.comp;

import arc.func.Boolp;
import arc.func.FloatFloatf;
import arc.func.Floatp;
import arc.func.Func;
import arc.func.Intp;
import arc.func.Prov;
import heavyindustry.func.BoolBoolf;
import heavyindustry.func.DoubleDoublef;
import heavyindustry.func.Doublep;
import heavyindustry.func.Intt;
import heavyindustry.func.Longp;
import heavyindustry.func.Longt;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static heavyindustry.util.ArrayUtils.doubleOf;
import static heavyindustry.util.ArrayUtils.floatOf;

/**
 * The appended variable interface is used to provide dynamic append variables for types,
 * providing operations such as {@code get}, {@code set}, {@code handle} on variables, but non-removable variables.
 * <br>(However, in fact, you can forcibly delete the variable mapping table by calling the {@link #extra()} method,
 * but this goes against the original design intention of this type.)
 *
 * @apiNote I really hate the encapsulation of raw data types, it is definitely the most disgusting design in Java.
 */
public interface ExtraVariablec {
	/**
	 * Variable mapping table entry, automatically or manually bound to a mapping object,
	 * which will serve as a container for storing dynamic variables as objects.
	 */
	Map<String, Object> extra();

	/**
	 * Get the value of a dynamic variable, return null if the variable does not exist.
	 *
	 * @param <T>   Get the type of variable
	 * @param field Variable name
	 */
	@SuppressWarnings("unchecked")
	default <T> T getVar(String field) {
		return (T) extra().get(field);
	}

	/**
	 * Retrieve the value of a dynamic variable, and if the variable does not exist, return the default value given.
	 * <br><strong>Note: </strong>If the variable does not exist, the default value is returned directly and will not be added to the variable table.
	 *
	 * @param <T>   Get the type of variable
	 * @param field Variable name
	 * @param def   Default value
	 */
	@SuppressWarnings("unchecked")
	default <T> T getVar(String field, T def) {
		return (T) extra().getOrDefault(field, def);
	}

	/**
	 * Get the value of a dynamic variable. If the variable does not exist,
	 * return the return value of the given initialization function and assign this value to the given variable.
	 * This is usually used for convenient variable value initialization.
	 *
	 * @param <T>	 Get the type of variable
	 * @param field   Variable name
	 * @param initial Initial value function
	 */
	@SuppressWarnings("unchecked")
	default <T> T getVar(String field, Prov<T> initial) {
		T t = initial.get();
		Object v;
		if ((v = extra().get(field)) == null) {
			T newValue;
			if ((newValue = t) != null) {
				extra().put(field, newValue);
				return newValue;
			}
		}

		return (T) v;
	}

	/**
	 * Get the value of a dynamic variable, throw an exception if the variable does not exist
	 *
	 * @param <T>   Get the type of variable
	 * @param field Variable name
	 * @throws NoSuchFieldException If the obtained variable does not exist
	 */
	@SuppressWarnings("unchecked")
	default <T> T getVarThr(String field) throws NoSuchFieldException {
		if (!extra().containsKey(field))
			throw new NoSuchFieldException("No such field with name: " + field);

		return (T) extra().get(field);
	}

	/**
	 * Set the value of the specified variable
	 *
	 * @param <T>   Set the type of variable
	 * @param field Variable name
	 * @param value Variable values set
	 * @return The original value of the variable before it was set
	 */
	@SuppressWarnings("unchecked")
	default <T> T setVar(String field, T value) {
		return (T) extra().put(field, value);
	}

	/**
	 * Use a function to process the value of a variable and update the value of the variable with the return value
	 *
	 * @param <T>   Set the type of variable
	 * @param field Variable name
	 * @param cons  Variable handling function
	 * @param def   Default value of variable
	 * @return The updated variable value, which is the return value of the function
	 */
	default <T> T handleVar(String field, Func<T, T> cons, T def) {
		T res;
		setVar(field, res = cons.get(getVar(field, def)));

		return res;
	}

	//-----------------------
	//Optimization and overloading of primitive data type operations
	//
	//Java's primitive data type boxing must be one of the top ten foolish behaviors in programming language history.
	//-----------------------

	/**
	 * Set boolean type variable value
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or atomized reference
	 * @see ExtraVariablec#setVar(String, Object)
	 */
	default boolean setVar(String field, boolean value) {
		Object res = getVar(field);

		if (res instanceof AtomicBoolean b) {
			boolean r = b.get();
			b.set(value);
			return r;
		} else if (res instanceof Boolean n) {
			extra().put(field, new AtomicBoolean(value));
			return n;
		} else if (res == null) {
			extra().put(field, new AtomicBoolean(value));
			return false;
		}

		throw new ClassCastException(res + " is not a boolean value or atomic boolean");
	}

	/**
	 * Get boolean variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Object)
	 */
	default boolean getVar(String field, boolean def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof AtomicBoolean i) return i.get();
		else if (res instanceof Boolean n) return n;

		throw new ClassCastException(res + " is not a boolean value or atomic boolean");
	}

	/**
	 * Retrieve the boolean variable value and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Prov)
	 */
	default boolean getVar(String field, Boolp initial) {
		Object res = getVar(field);
		if (res == null) {
			boolean b = initial.get();
			extra().put(field, new AtomicBoolean(b));
			return b;
		}

		if (res instanceof AtomicBoolean b) return b.get();
		else if (res instanceof Boolean n) return n;

		throw new ClassCastException(res + " is not a boolean value or atomic boolean");
	}

	/**
	 * Use processing functions to handle boolean variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or atomized reference
	 * @see ExtraVariablec#handleVar(String, Func, Object)
	 */
	default boolean handleVar(String field, BoolBoolf handle, boolean def) {
		boolean b;
		setVar(field, b = handle.get(getVar(field, def)));

		return b;
	}

	/**
	 * Set the value of an int type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or atomized reference
	 * @see ExtraVariablec#setVar(String, Object)
	 */
	default int setVar(String field, int value) {
		Object res = getVar(field);

		if (res instanceof AtomicInteger i) {
			int r = i.get();
			i.set(value);
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new AtomicInteger(value));
			return n.intValue();
		} else if (res == null) {
			extra().put(field, new AtomicInteger(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or atomic integer");
	}

	/**
	 * Get the value of the int variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Object)
	 */
	default int getVar(String field, int def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof AtomicInteger i) return i.get();
		else if (res instanceof Number n) return n.intValue();

		throw new ClassCastException(res + " is not a number or atomic integer");
	}

	/**
	 * Retrieve the value of an int variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Prov)
	 */
	default int getVar(String field, Intp initial) {
		Object res = getVar(field);
		if (res == null) {
			int b = initial.get();
			extra().put(field, new AtomicInteger(b));
			return b;
		}

		if (res instanceof AtomicInteger i) return i.get();
		else if (res instanceof Number n) return n.intValue();

		throw new ClassCastException(res + " is not a number or atomic integer");
	}

	/**
	 * Use processing functions to handle int variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or atomized reference
	 * @see ExtraVariablec#handleVar(String, Func, Object)
	 */
	default int handleVar(String field, Intt handle, int def) {
		int i;
		setVar(field, i = handle.get(getVar(field, def)));

		return i;
	}

	/**
	 * Set the value of a long type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or atomized reference
	 * @see ExtraVariablec#setVar(String, Object)
	 */
	default long setVar(String field, long value) {
		Object res = getVar(field);

		if (res instanceof AtomicLong l) {
			long r = l.get();
			l.set(value);
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new AtomicLong(value));
			return n.longValue();
		} else if (res == null) {
			extra().put(field, new AtomicLong(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or atomic long");
	}

	/**
	 * Get the value of a long variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Object)
	 */
	default long getVar(String field, long def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof AtomicLong l) return l.get();
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or atomic long");
	}

	/**
	 * Retrieve the value of a long variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or atomized reference
	 * @see ExtraVariablec#getVar(String, Prov)
	 */
	default long getVar(String field, Longp initial) {
		Object res = getVar(field);
		if (res == null) {
			long l = initial.get();
			extra().put(field, new AtomicLong(l));
			return l;
		}

		if (res instanceof AtomicLong l) return l.get();
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or atomic long");
	}

	/**
	 * Use processing functions to handle long variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or atomized reference
	 * @see ExtraVariablec#handleVar(String, Func, Object)
	 */
	default long handleVar(String field, Longt handle, long def) {
		long l;
		setVar(field, l = handle.get(getVar(field, def)));

		return l;
	}

	/**
	 * Set float type variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element float array
	 * @see ExtraVariablec#setVar(String, Object)
	 */
	default float setVar(String field, float value) {
		Object res = getVar(field);

		if (res instanceof float[] a && a.length > 0) {
			float r = a[0];
			a[0] = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, floatOf(value));
			return n.floatValue();
		} else if (res == null) {
			extra().put(field, floatOf(value));
			return 0f;
		}

		throw new ClassCastException(res + " is not a number or single float reference array");
	}

	/**
	 * Get float variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element float array
	 * @see ExtraVariablec#getVar(String, Object)
	 */
	default float getVar(String field, float def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof float[] f && f.length > 0) return f[0];
		else if (res instanceof Number n) return n.floatValue();

		throw new ClassCastException(res + " is not a number or single float reference array");
	}

	/**
	 * Retrieve the float variable value and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element float array
	 * @see ExtraVariablec#getVar(String, Prov)
	 */
	default float getVar(String field, Floatp initial) {
		Object res = getVar(field);
		if (res == null) {
			float f = initial.get();
			extra().put(field, floatOf(f));
			return f;
		}

		if (res instanceof float[] l && l.length > 0) return l[0];
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or single float reference array");
	}

	/**
	 * Use processing functions to handle float variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element float array
	 * @see ExtraVariablec#handleVar(String, Func, Object)
	 */
	default float handleVar(String field, FloatFloatf handle, float def) {
		float trans;
		setVar(field, trans = handle.get(getVar(field, def)));

		return trans;
	}

	/**
	 * Set the value of a double type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element double array
	 * @see ExtraVariablec#setVar(String, Object)
	 */
	default double setVar(String field, double value) {
		Object res = getVar(field);

		if (res instanceof double[] a && a.length > 0) {
			double r = a[0];
			a[0] = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, doubleOf(value));
			return n.doubleValue();
		} else if (res == null) {
			extra().put(field, doubleOf(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or single double reference array");
	}

	/**
	 * Get double variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or a single element double array
	 * @see ExtraVariablec#getVar(String, Object)
	 */
	default double getVar(String field, double def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof double[] f && f.length > 0) return f[0];
		else if (res instanceof Number n) return n.doubleValue();

		throw new ClassCastException(res + " is not a number or single double reference array");
	}

	/**
	 * Retrieve the value of a double variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or a single element double array
	 * @see ExtraVariablec#getVar(String, Prov)
	 */
	default double getVar(String field, Doublep initial) {
		Object res = getVar(field);
		if (res == null) {
			double d = initial.get();
			extra().put(field, doubleOf(d));
			return d;
		}

		if (res instanceof double[] d && d.length > 0) return d[0];
		else if (res instanceof Number n) return n.doubleValue();

		throw new ClassCastException(res + " is not a number or single double reference array");
	}

	/**
	 * Use processing functions to handle double variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or a single element double array
	 * @see ExtraVariablec#handleVar(String, Func, Object)
	 */
	default double handleVar(String field, DoubleDoublef handle, double def) {
		double d;
		setVar(field, d = handle.get(getVar(field, def)));

		return d;
	}
}
