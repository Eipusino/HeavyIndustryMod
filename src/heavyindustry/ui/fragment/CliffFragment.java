package heavyindustry.ui.fragment;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import heavyindustry.world.blocks.environment.DPCliff;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class CliffFragment extends Table {
	private final Table layout;
	private final Color col = Color.valueOf("645654");
	private boolean enabled;

	public CliffFragment() {
		setFillParent(true);
		visible(() -> Vars.ui.hudfrag.shown && Vars.state.isEditor());
		touchable(() -> enabled && visible ? Touchable.enabled : Touchable.disabled);
		left();

		add(layout = new Table(Styles.black5, t -> {
			t.table(title -> {
				title.image(Icon.treeSmall).size(15f).center().padRight(15f).color(col);
				title.label(() -> "@hi-cliff-placer").grow();
				title.image(Icon.treeSmall).size(15f).center().padLeft(15f).color(col);
			}).growX().padBottom(10f).row();
			t.table(Styles.black3, buttons -> {
				buttons.button("@hi-process-cliffs", Icon.play, Styles.nonet, DPCliff::processCliffs).growX().height(50f).pad(5f).row();
				buttons.button("@hi-un-process-cliffs", Icon.undo, Styles.nonet, DPCliff::unProcessCliffs).growX().height(50f).pad(5f);
			}).growX();
		})).margin(10f);
	}

	public void build(Group parent) {
		layout.actions(Actions.alpha(0));
		parent.addChildAt(0, this);

		if (!Vars.mobile) {
			Core.scene.addListener(new InputListener() {
				@Override
				public boolean keyDown(InputEvent event, KeyCode keycode) {
					if (Core.input.keyTap(KeyCode.p) && visible) {
						toggle();
						return true;
					}
					return false;
				}
			});
		}
	}

	private void toggle() {
		if (!visible || layout.hasActions()) return;
		enabled = !enabled;
		if (enabled) {
			layout.actions(
					Actions.moveBy(-layout.getWidth(), 0),
					Actions.parallel(
							Actions.alpha(1, 0.3f, Interp.pow3Out),
							Actions.moveBy(layout.getWidth(), 0, 0.3f, Interp.pow3Out)
					)
			);
		} else {
			layout.actions(
					Actions.parallel(
							Actions.moveBy(-layout.getWidth(), 0, 0.3f, Interp.pow3Out),
							Actions.alpha(0, 0.3f, Interp.pow3Out)
					),
					Actions.moveBy(layout.getWidth(), 0)
			);
		}
	}
}
