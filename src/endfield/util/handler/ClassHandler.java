package endfield.util.handler;

import dynamilize.DynamicMaker;
import dynamilize.classmaker.AbstractClassGenerator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static endfield.Vars2.classHelper;

public interface ClassHandler {
	ClassHandler newInstance(Class<?> modMain);

	AbstractClassGenerator getGenerator();

	DynamicMaker getDynamicMaker();

	//AbstractFileClassLoader currLoader();

	void finishGenerate();

	static Field findField(Class<?> clazz, String name) {
		return classHelper.findField(clazz, name);
	}

	static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return classHelper.findMethod(clazz, name, parameterTypes);
	}

	static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		return classHelper.findConstructor(clazz, parameterTypes);
	}

	static Field getField(Class<?> clazz, String name) {
		return classHelper.getField(clazz, name);
	}

	static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return classHelper.getMethod(clazz, name, parameterTypes);
	}

	static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		return classHelper.getConstructor(clazz, parameterTypes);
	}

	static Field[] getFields(Class<?> clazz) {
		return classHelper.getFields(clazz);
	}

	static Method[] getMethods(Class<?> clazz) {
		return classHelper.getMethods(clazz);
	}

	static <T> Constructor<T>[] getConstructors(Class<T> clazz) {
		return classHelper.getConstructors(clazz);
	}

	static <T> T allocateInstance(Class<? extends T> clazz) {
		return classHelper.allocateInstance(clazz);
	}

	static Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) {
		return classHelper.defineClass(name, bytes, loader);
	}
}
