package heavyindustry.util;

import dynamilize.DynamicObject;
import dynamilize.IVariable;

import java.lang.reflect.Field;

import static heavyindustry.HVars.fieldAccessHelper;

public class JavaFieldRef implements IVariable {
	private final String name;
	private final Class<?> owner;

	public JavaFieldRef(Field field) {
		name = field.getName();
		owner = field.getDeclaringClass();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void init(DynamicObject<?> object) { /*no action*/ }

	@Override
	public <T> T get(DynamicObject<?> obj) {
		if (!owner.isAssignableFrom(obj.getClass()))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		return fieldAccessHelper.get(obj, name);
	}

	@Override
	public void set(DynamicObject<?> obj, Object value) {
		if (!owner.isAssignableFrom(obj.getClass()))
			throw new ClassCastException(obj.getClass() + " can not be cast to " + owner);

		fieldAccessHelper.set(obj, name, value);
	}

	@Override
	public boolean get(DynamicObject<?> dynamicObject, boolean b) {
		return fieldAccessHelper.getBoolean(dynamicObject, name);
	}

	@Override
	public byte get(DynamicObject<?> dynamicObject, byte b) {
		return fieldAccessHelper.getByte(dynamicObject, name);
	}

	@Override
	public short get(DynamicObject<?> dynamicObject, short i) {
		return fieldAccessHelper.getShort(dynamicObject, name);
	}

	@Override
	public int get(DynamicObject<?> dynamicObject, int i) {
		return fieldAccessHelper.getInt(dynamicObject, name);
	}

	@Override
	public long get(DynamicObject<?> dynamicObject, long l) {
		return fieldAccessHelper.getLong(dynamicObject, name);
	}

	@Override
	public float get(DynamicObject<?> dynamicObject, float v) {
		return fieldAccessHelper.getFloat(dynamicObject, name);
	}

	@Override
	public double get(DynamicObject<?> dynamicObject, double v) {
		return fieldAccessHelper.getDouble(dynamicObject, name);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, boolean b) {
		fieldAccessHelper.set(dynamicObject, name, b);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, byte b) {
		fieldAccessHelper.set(dynamicObject, name, b);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, short s) {
		fieldAccessHelper.set(dynamicObject, name, s);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, int i) {
		fieldAccessHelper.set(dynamicObject, name, i);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, long l) {
		fieldAccessHelper.set(dynamicObject, name, l);
	}

	@Override
	public void set(DynamicObject<?> dynamicObject, float f) {
		fieldAccessHelper.set(dynamicObject, name, f);

	}

	@Override
	public void set(DynamicObject<?> dynamicObject, double d) {
		fieldAccessHelper.set(dynamicObject, name, d);
	}
}
