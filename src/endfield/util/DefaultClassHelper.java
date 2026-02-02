package endfield.util;

public class DefaultClassHelper implements ClassHelper {
	@Override
	public <T> T allocateInstance(Class<? extends T> clazz) {
		throw new UnsupportedOperationException("allocateInstance is not supported.");
	}

	@Override
	public Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) {
		throw new UnsupportedOperationException("defineClass is not supported.");
	}
}
