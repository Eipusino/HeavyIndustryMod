package heavyindustry.core;

import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ExtraVariable;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;

/** Classes for testing purposes only, do not use. */
@TestOnly
public class HTest implements Cloneable, ExtraVariable {
	private static short count;

	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);
	public short id;

	public HTest() {
		id = count++;
	}

	public static void test() throws Throwable {
		HTestKt.testKt();
	}

	public HTest copy() {
		try {
			return (HTest) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
