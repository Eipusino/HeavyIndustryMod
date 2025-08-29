package heavyindustry.util.aspect.triggers;

import arc.func.Cons;
import heavyindustry.util.aspect.BaseTriggerEntry;
import mindustry.game.EventType.Trigger;

public class TriggerEntry<T> extends BaseTriggerEntry<T> {
	public final Trigger trigger;
	public final Cons<T> handle;

	public TriggerEntry(Trigger trigger, Cons<T> handle) {
		super(TriggerControl.class);
		this.handle = handle;
		this.trigger = trigger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Object... child) {
		handle.get((T) child[0]);
	}
}
