package heavyindustry.util.aspect.triggers;

import arc.func.Cons2;
import heavyindustry.util.aspect.BaseTriggerEntry;

public class EventEntry<T, E> extends BaseTriggerEntry<T> {
	public final Class<E> eventType;
	public final Cons2<E, T> listener;

	public EventEntry(Class<E> type, Cons2<E, T> handle) {
		super(EventControl.class);
		eventType = type;
		listener = handle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Object... child) {
		listener.get((E) child[0], (T) child[1]);
	}
}
