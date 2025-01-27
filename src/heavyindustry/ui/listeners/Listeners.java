package heavyindustry.ui.listeners;

import arc.*;
import arc.func.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.event.*;

public final class Listeners {
	/** Don't let anyone instantiate this class. */
	private Listeners() {}

	/** Invoke action on clicked element on screen */
	public static void onScreenClick(Cons<Element> action) {
		Core.scene.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
				action.get(Core.scene.hit(x, y, false));
				return super.touchDown(event, x, y, pointer, button);
			}
		});
	}

	/** Adds an action to an element when any of its descendants are clicked */
	public static void onScreenClick(Element element, Action action) {
		onScreenClick(element, action, true);
	}

	/**
	 * Adds an action to an element when any of its descendants are clicked
	 *
	 * @param removeIfRemoved marks remove listener after invocation or not
	 */
	public static void onScreenClick(Element element, Action action, boolean removeIfRemoved) {
		Core.scene.addListener(new ClickOnOtherListener(() -> {
			if (element.getScene() != null) {
				element.addAction(action);
			}
			return removeIfRemoved;
		}, element::isAscendantOf));
	}

	public static class ClickOnOtherListener extends ClickListener {
		public Boolp shouldRemove;
		public HitChecker hitChecker;

		public ClickOnOtherListener(Boolp should, HitChecker hit) {
			shouldRemove = should;
			hitChecker = hit;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
			Element hit = Core.scene.hit(x, y, false);

			if (hit == null || !hitChecker.hit(hit)) {
				if (shouldRemove.get()) {
					Core.scene.removeListener(this);
				}
			}
			return super.touchDown(event, x, y, pointer, button);
		}

		public interface HitChecker {
			boolean hit(Element element);
		}
	}
}
