package endfield.util;

import arc.func.Boolp;
import arc.func.FloatFloatf;
import arc.func.Floatp;
import arc.func.Func;
import arc.func.IntIntf;
import arc.func.Intp;
import arc.func.Prov;
import endfield.func.BoolBoolf;
import endfield.func.CharCharf;
import endfield.func.Charp;
import endfield.func.DoubleDoublef;
import endfield.func.Doublep;
import endfield.func.LongLongf;
import endfield.func.Longp;
import endfield.util.misc.BoolReference;
import endfield.util.misc.CharReference;
import endfield.util.misc.DoubleReference;
import endfield.util.misc.FloatReference;
import endfield.util.misc.IntReference;
import endfield.util.misc.LongReference;

import java.util.Map;

/**
 * The appended variable interface is used to provide dynamic append variables for types,
 * providing operations such as {@code get}, {@code set}, {@code handle} on variables, but non-removable variables.
 * <br>(However, in fact, you can forcibly delete the variable mapping table by calling the {@link #extra()} method,
 * but this goes against the original design intention of this type.)
 *
 * @apiNote I really hate the encapsulation of raw data types, it is definitely the most disgusting design in Java.
 */
public interface ExtraVariable {
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
		Object o = extra().get(field);
		return o != null || extra().containsKey(field) ? (T) o : def;
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
		Object o = extra().get(field);
		if (o == null) {
			T newValue;
			if ((newValue = initial.get()) != null) {
				extra().put(field, newValue);
				return newValue;
			}
		}

		return (T) o;
	}

	/**
	 * Get the value of a dynamic variable, throw an exception if the variable does not exist
	 *
	 * @param <T>   Get the type of variable
	 * @param field Variable name
	 * @throws NoSuchVariableException If the obtained variable does not exist
	 */
	@SuppressWarnings("unchecked")
	default <T> T getVarThr(String field) throws NoSuchVariableException {
		if (!extra().containsKey(field))
			throw new NoSuchVariableException("No such field with name: " + field);

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
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or boolean reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default boolean setVar(String field, boolean value) {
		Object res = getVar(field);

		if (res instanceof BoolReference b) {
			boolean r = b.element;
			b.element = value;
			return r;
		} else if (res instanceof Boolean n) {
			extra().put(field, new BoolReference(value));
			return n;
		} else if (res == null) {
			extra().put(field, new BoolReference(value));
			return false;
		}

		throw new ClassCastException(res + " is not a boolean value or boolean reference");
	}

	/**
	 * Get boolean variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or
	 *                            boolean reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default boolean getVar(String field, boolean def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof BoolReference r) return r.element;
		else if (res instanceof Boolean b) return b;

		throw new ClassCastException(res + " is not a boolean value or boolean reference");
	}

	/**
	 * Retrieve the boolean variable value and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or
	 *                            boolean reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default boolean getVar(String field, Boolp initial) {
		Object res = getVar(field);
		if (res == null) {
			boolean b = initial.get();
			extra().put(field, new BoolReference(b));
			return b;
		}

		if (res instanceof BoolReference r) return r.element;
		else if (res instanceof Boolean b) return b;

		throw new ClassCastException(res + " is not a boolean value or boolean reference");
	}

	/**
	 * Use processing functions to handle boolean variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a boolean wrapper type or
	 *                            boolean reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default boolean handleVar(String field, BoolBoolf handle, boolean def) {
		boolean b;
		setVar(field, b = handle.get(getVar(field, def)));

		return b;
	}

	/**
	 * Set character type variable value
	 *
	 * @throws ClassCastException If the variable already exists and is not a character wrapper type
	 *                            or character reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default char setVar(String field, char value) {
		Object res = getVar(field);

		if (res instanceof CharReference c) {
			char r = c.element;
			c.element = value;
			return r;
		} else if (res instanceof Character c) {
			extra().put(field, new CharReference(value));
			return c;
		} else if (res == null) {
			extra().put(field, new CharReference(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a character value or character reference");
	}

	/**
	 * Get character variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a character wrapper type
	 *                            or char reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default char getVar(String field, char def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof CharReference r) return r.element;
		else if (res instanceof Character c) return c;

		throw new ClassCastException(res + " is not a character value or character reference");
	}

	/**
	 * Retrieve the value of an character variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not an character wrapper type
	 *                            or character reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default char getVar(String field, Charp initial) {
		Object res = getVar(field);
		if (res == null) {
			char c = initial.get();
			extra().put(field, new CharReference(c));
			return c;
		}

		if (res instanceof CharReference c) return c.element;
		else if (res instanceof Character c) return c;

		throw new ClassCastException(res + " is not a character or character reference");
	}

	/**
	 * Use processing functions to handle character variable values and update variable values with return
	 * values.
	 *
	 * @throws ClassCastException If the variable already exists and is not an character wrapper type
	 *                            or character reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default int handleVar(String field, CharCharf handle, char def) {
		int i;
		setVar(field, i = handle.get(getVar(field, def)));

		return i;
	}

	/**
	 * Set the value of an int type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or int
	 *                            reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default int setVar(String field, int value) {
		Object res = getVar(field);

		if (res instanceof IntReference i) {
			int r = i.element;
			i.element = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new IntReference(value));
			return n.intValue();
		} else if (res == null) {
			extra().put(field, new IntReference(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or integer reference");
	}

	/**
	 * Get the value of the int variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or int
	 *                            reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default int getVar(String field, int def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof IntReference i) return i.element;
		else if (res instanceof Number n) return n.intValue();

		throw new ClassCastException(res + " is not a number or integer reference");
	}

	/**
	 * Retrieve the value of an int variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or int
	 *                            reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default int getVar(String field, Intp initial) {
		Object res = getVar(field);
		if (res == null) {
			int i = initial.get();
			extra().put(field, new IntReference(i));
			return i;
		}

		if (res instanceof IntReference i) return i.element;
		else if (res instanceof Number n) return n.intValue();

		throw new ClassCastException(res + " is not a number or integer reference");
	}

	/**
	 * Use processing functions to handle int variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not an int wrapper type or int
	 *                            reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default int handleVar(String field, IntIntf handle, int def) {
		int i;
		setVar(field, i = handle.get(getVar(field, def)));

		return i;
	}

	/**
	 * Set the value of a long type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or long reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default long setVar(String field, long value) {
		Object res = getVar(field);

		if (res instanceof LongReference l) {
			long r = l.element;
			l.element = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new LongReference(value));
			return n.longValue();
		} else if (res == null) {
			extra().put(field, new LongReference(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or long reference");
	}

	/**
	 * Get the value of a long variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or long reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default long getVar(String field, long def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof LongReference l) return l.element;
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or long reference");
	}

	/**
	 * Retrieve the value of a long variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or long reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default long getVar(String field, Longp initial) {
		Object res = getVar(field);
		if (res == null) {
			long l = initial.get();
			extra().put(field, new LongReference(l));
			return l;
		}

		if (res instanceof LongReference l) return l.element;
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or float reference");
	}

	/**
	 * Use processing functions to handle long variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a long wrapper type or long reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default long handleVar(String field, LongLongf handle, long def) {
		long l;
		setVar(field, l = handle.get(getVar(field, def)));

		return l;
	}

	/**
	 * Set float type variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or float reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default float setVar(String field, float value) {
		Object res = getVar(field);

		if (res instanceof FloatReference f) {
			float r = f.element;
			f.element = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new FloatReference(value));
			return n.floatValue();
		} else if (res == null) {
			extra().put(field, new FloatReference(value));
			return 0f;
		}

		throw new ClassCastException(res + " is not a number or float reference");
	}

	/**
	 * Get float variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or float reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default float getVar(String field, float def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof FloatReference f) return f.element;
		else if (res instanceof Number n) return n.floatValue();

		throw new ClassCastException(res + " is not a number or float reference");
	}

	/**
	 * Retrieve the float variable value and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or float reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default float getVar(String field, Floatp initial) {
		Object res = getVar(field);
		if (res == null) {
			float f = initial.get();
			extra().put(field, new FloatReference(f));
			return f;
		}

		if (res instanceof FloatReference f) return f.element;
		else if (res instanceof Number n) return n.longValue();

		throw new ClassCastException(res + " is not a number or float reference");
	}

	/**
	 * Use processing functions to handle float variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a float wrapper type or float reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default float handleVar(String field, FloatFloatf handle, float def) {
		float trans;
		setVar(field, trans = handle.get(getVar(field, def)));

		return trans;
	}

	/**
	 * Set the value of a double type variable.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or double reference
	 * @see ExtraVariable#setVar(String, Object)
	 */
	default double setVar(String field, double value) {
		Object res = getVar(field);

		if (res instanceof DoubleReference d) {
			double r = d.element;
			d.element = value;
			return r;
		} else if (res instanceof Number n) {
			extra().put(field, new DoubleReference(value));
			return n.doubleValue();
		} else if (res == null) {
			extra().put(field, new DoubleReference(value));
			return 0;
		}

		throw new ClassCastException(res + " is not a number or double reference");
	}

	/**
	 * Get double variable value.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or double reference
	 * @see ExtraVariable#getVar(String, Object)
	 */
	default double getVar(String field, double def) {
		Object res = getVar(field);
		if (res == null) return def;

		if (res instanceof DoubleReference d) return d.element;
		else if (res instanceof Number n) return n.doubleValue();

		throw new ClassCastException(res + " is not a number or double reference");
	}

	/**
	 * Retrieve the value of a double variable and initialize the variable value when it does not exist.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or double reference
	 * @see ExtraVariable#getVar(String, Prov)
	 */
	default double getVar(String field, Doublep initial) {
		Object res = getVar(field);
		if (res == null) {
			double d = initial.get();
			extra().put(field, new DoubleReference(d));
			return d;
		}

		if (res instanceof DoubleReference d) return d.element;
		else if (res instanceof Number n) return n.doubleValue();

		throw new ClassCastException(res + " is not a number or double reference");
	}

	/**
	 * Use processing functions to handle double variable values and update variable values with return values.
	 *
	 * @throws ClassCastException If the variable already exists and is not a double wrapper type or double reference
	 * @see ExtraVariable#handleVar(String, Func, Object)
	 */
	default double handleVar(String field, DoubleDoublef handle, double def) {
		double d;
		setVar(field, d = handle.get(getVar(field, def)));

		return d;
	}
}
