package heavyindustry.core;

import arc.util.Log;
import arc.util.OS;
import heavyindustry.android.field.FieldUtils;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ExtraVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Map;

import static heavyindustry.util.Unsafer.unsafe;

@TestOnly
public class HTest implements Cloneable, ExtraVariable, Comparable<HTest>, Annotation {
	public static final HTest instance = new HTest();

	private static short count;

	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);
	public short id;

	public HTest() {
		id = count++;
	}

	public static void test() {}

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
}
