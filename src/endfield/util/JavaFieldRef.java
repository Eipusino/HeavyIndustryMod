package endfield.util;

import dynamilize.DynamicObject;
import dynamilize.IVariable;

import java.lang.reflect.Field;

import static endfield.Vars2.fieldAccessHelper;

public class JavaFieldRef implements IVariable {
	final String name;
	final Class<?> owner;

	public JavaFieldRef(Field field) {
		name = field.getName();
		owner = field.getDeclaringClass();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public <T> T get(DynamicObject<?> obj) {
		if (!owner.isInstance(obj))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		return fieldAccessHelper.get(obj, name);
	}

	@Override
	public void set(DynamicObject<?> obj, Object value) {
		if (!owner.isInstance(obj))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		fieldAccessHelper.set(obj, name, value);
	}

	@Override
	public boolean get(DynamicObject<?> obj, boolean def) {
		return fieldAccessHelper.getBoolean(obj, name);
	}

	@Override
	public byte get(DynamicObject<?> obj, byte def) {
		return fieldAccessHelper.getByte(obj, name);
	}

	@Override
	public short get(DynamicObject<?> obj, short def) {
		return fieldAccessHelper.getShort(obj, name);
	}

	@Override
	public int get(DynamicObject<?> obj, int def) {
		return fieldAccessHelper.getInt(obj, name);
	}

	@Override
	public long get(DynamicObject<?> obj, long def) {
		return fieldAccessHelper.getLong(obj, name);
	}

	@Override
	public float get(DynamicObject<?> obj, float def) {
		return fieldAccessHelper.getFloat(obj, name);
	}

	@Override
	public double get(DynamicObject<?> obj, double def) {
		return fieldAccessHelper.getDouble(obj, name);
	}

	@Override
	public char get(DynamicObject<?> obj, char def) {
		return fieldAccessHelper.getChar(obj, name);
	}

	@Override
	public void set(DynamicObject<?> obj, boolean value) {
		fieldAccessHelper.setBoolean(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, byte value) {
		fieldAccessHelper.setByte(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, short value) {
		fieldAccessHelper.setShort(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, int value) {
		fieldAccessHelper.setInt(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, long value) {
		fieldAccessHelper.setLong(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, float value) {
		fieldAccessHelper.setFloat(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, double value) {
		fieldAccessHelper.setDouble(obj, name, value);
	}

	@Override
	public void set(DynamicObject<?> obj, char value) {
		fieldAccessHelper.setChar(obj, name, value);
	}
}
