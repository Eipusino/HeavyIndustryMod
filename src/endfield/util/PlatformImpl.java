package endfield.util;

import java.lang.invoke.MethodHandles.Lookup;

public interface PlatformImpl {
	Lookup lookup(Class<?> clazz);

	void copyMemory(long srcAddr, long dstAddr, long bytes);

	void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
}
