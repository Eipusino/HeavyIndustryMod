package endfield.util;

import dynamilize.DynamicObject;
import dynamilize.IVariable;

import java.lang.reflect.Field;

import static endfield.Vars2.fieldAccessHelper;

public class JavaFieldReference implements IVariable {
	final Field field;
	final String name;
	final Class<?> owner;

	public JavaFieldReference(Field field) {
		this.field = field;
		this.name = field.getName();
		this.owner = field.getDeclaringClass();

		Reflects.setAccessible(field);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public <T> T get(DynamicObject<?> obj) {
		if (!owner.isInstance(obj))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		return fieldAccessHelper.get(obj, field, false);
	}

	@Override
	public void set(DynamicObject<?> obj, Object value) {
		if (!owner.isInstance(obj))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		fieldAccessHelper.set(obj, field, value, false);
	}

	@Override
	public boolean get(DynamicObject<?> obj, boolean def) {
		return fieldAccessHelper.getBoolean(obj, field, false);
	}

	@Override
	public byte get(DynamicObject<?> obj, byte def) {
		return fieldAccessHelper.getByte(obj, field, false);
	}

	@Override
	public short get(DynamicObject<?> obj, short def) {
		return fieldAccessHelper.getShort(obj, field, false);
	}

	@Override
	public int get(DynamicObject<?> obj, int def) {
		return fieldAccessHelper.getInt(obj, field, false);
	}

	@Override
	public long get(DynamicObject<?> obj, long def) {
		return fieldAccessHelper.getLong(obj, field, false);
	}

	@Override
	public float get(DynamicObject<?> obj, float def) {
		return fieldAccessHelper.getFloat(obj, field, false);
	}

	@Override
	public double get(DynamicObject<?> obj, double def) {
		return fieldAccessHelper.getDouble(obj, field, false);
	}

	@Override
	public char get(DynamicObject<?> obj, char def) {
		return fieldAccessHelper.getChar(obj, field, false);
	}

	@Override
	public void set(DynamicObject<?> obj, boolean value) {
		fieldAccessHelper.setBoolean(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, byte value) {
		fieldAccessHelper.setByte(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, short value) {
		fieldAccessHelper.setShort(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, int value) {
		fieldAccessHelper.setInt(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, long value) {
		fieldAccessHelper.setLong(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, float value) {
		fieldAccessHelper.setFloat(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, double value) {
		fieldAccessHelper.setDouble(obj, field, value, false);
	}

	@Override
	public void set(DynamicObject<?> obj, char value) {
		fieldAccessHelper.setChar(obj, field, value, false);
	}
}
