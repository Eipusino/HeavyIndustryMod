package endfield.core;

import endfield.util.CollectionObjectMap;
import endfield.util.ExtraVariable;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;

/** Classes for testing purposes only, do not use. */
@TestOnly
public class Test implements Cloneable, ExtraVariable {
	private static short count;

	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);
	public short id;

	public Test() {
		id = count++;
	}

	public static void test() throws Throwable {
		TestKt.testKt();
	}

	public Test copy() {
		try {
			return (Test) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
