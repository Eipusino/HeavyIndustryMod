package heavyindustry.core;

import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ExtraVariable;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;

@TestOnly
public class HTest implements Cloneable, ExtraVariable {
	public static final HTest instance = new HTest();

	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	public HTest() {}

	public void test() throws Throwable {}

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
