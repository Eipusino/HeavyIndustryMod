package heavyindustry.ui.listeners;

import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.Button;

/**
 * Default implementation of {@link ClickListener} for {@link Button}.
 *  <br>
 * {@link Button} uses abstract implementation
 */
public class ButtonClickListener extends ClickListener {
	public Button buttonObject;

	public ButtonClickListener(Button button) {
		buttonObject = button;
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		if (buttonObject.isDisabled()) return;
		buttonObject.setProgrammaticChangeEvents(true);
		buttonObject.setChecked(!buttonObject.isChecked());
		buttonObject.setProgrammaticChangeEvents(false);
	}
}
