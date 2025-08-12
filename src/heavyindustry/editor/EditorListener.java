package heavyindustry.editor;

import arc.Events;
import mindustry.Vars;
import mindustry.core.GameState.State;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.game.EventType.Trigger;

/**
 * Base class for listeners that attach themselves when the game is in editor mode.
 *
 * @since 1.0.6
 */
public abstract class EditorListener {
	protected boolean attached;

	protected EditorListener() {
		Events.run(Trigger.update, () -> valid(this::update));
		Events.run(Trigger.drawOver, () -> valid(this::draw));
		Events.on(StateChangeEvent.class, e -> {
			if (e.from == State.menu && e.to == State.playing && Vars.state.isEditor()) {
				if (shouldAttach()) {
					Vars.state.map.tags.put("name", Vars.editor.tags.get("name"));

					attached = true;
					enter();
				}
			} else if (attached && e.to == State.menu) {
				exit();
				attached = false;
			}
		});
	}

	public boolean isAttached() {
		return attached;
	}

	public abstract boolean shouldAttach();

	public void enter() {}

	public void exit() {}

	public void update() {}

	public void draw() {}

	public void valid(Runnable run) {
		if (attached) run.run();
	}
}
