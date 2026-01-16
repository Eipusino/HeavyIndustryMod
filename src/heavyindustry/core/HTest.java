package heavyindustry.core;

import arc.util.Log;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ExtraVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.lang.annotation.Annotation;
import java.util.Map;

@TestOnly
public class HTest implements Cloneable, ExtraVariable, Comparable<HTest>, Annotation {
	public static final HTest instance = new HTest();

	private static short count;

	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);
	public short id;

	public HTest() {
		id = count++;
	}

	public static void test() {
		try {

		} catch (Throwable e) {
			Log.err(e);
		}
	}

	public HTest copy() {
		try {
			return (HTest) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public int compareTo(@NotNull HTest t) {
		return Short.compare(id, t.id);
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return HTest.class;
	}

	public enum HTestEnum {
		m;

		HTestEnum() {}
	}
}
