package heavyindustry.util.aspect;

import java.util.HashMap;
import java.util.LinkedHashSet;

public abstract class BaseTriggerControl<T extends BaseTriggerEntry<?>> {
	protected final HashMap<T, LinkedHashSet<AbstractAspect<?, ?>>> aspectEntry = new HashMap<>();

	public abstract void apply(T triggerEntry);

	public abstract void remove(T triggerEntry);
}
