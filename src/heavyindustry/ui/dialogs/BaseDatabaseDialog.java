package heavyindustry.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.TextField;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import dynamilize.DynamicClass;
import dynamilize.runtimeannos.AspectInterface;
import heavyindustry.HVars;
import heavyindustry.util.BaseContentType;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.DatabaseDialog;
import mindustry.world.Block;

import static arc.Core.settings;
import static mindustry.Vars.content;
import static mindustry.Vars.mobile;
import static mindustry.Vars.state;
import static mindustry.Vars.ui;

/**
 * Rewrite the database dialog box to reorder types (who knows when Anuke will be able to change the read-write mechanism)
 *
 * @since 1.0.8
 */
public class BaseDatabaseDialog {
	private static final DynamicClass baseDatabase = DynamicClass.get("BaseDatabase");
	private static final ObjectSet<UnlockableContent> hiddenContents = new ObjectSet<>();

	static {
		baseDatabase.setFunction("rebuild", (s, su, a) -> {
			Table all = s.getVar("all");
			TextField search = s.getVar("search");

			all.clear();
			var text = search.getText();

			for (int j = 0; j < BaseContentType.displayContentList.length; j++) {
				ContentType type = BaseContentType.displayContentList[j];

				Seq<UnlockableContent> array = content.getBy(type).select(c -> c instanceof UnlockableContent u
						&& !u.isHidden() && !hiddenContents.contains(u)
						&& (text.isEmpty() || u.localizedName.toLowerCase().contains(text.toLowerCase()))).as();

				if (array.size == 0) continue;

				all.add("@content." + type.name() + ".name").growX().left().color(Pal.accent);
				all.row();
				all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
				all.row();
				all.table(list -> {
					list.left();

					int cols = (int) Mathf.clamp((Core.graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 12), 1, 22);
					int count = 0;

					for (int i = 0; i < array.size; i++) {
						UnlockableContent unlock = array.get(i);

						Image image = unlocked(unlock) ? new Image(unlock.uiIcon).setScaling(Scaling.fit) : new Image(Icon.lock, Pal.gray);

						//banned cross
						if (state.isGame() && (unlock instanceof UnitType u && u.isBanned() || unlock instanceof Block b && state.rules.isBanned(b))) {
							list.stack(image, new Image(Icon.cancel) {{
								setColor(Color.scarlet);
								touchable = Touchable.disabled;
							}}).size(8 * 4).pad(3);
						} else {
							list.add(image).size(8 * 4).pad(3);
						}

						ClickListener listener = new ClickListener();
						image.addListener(listener);
						if (!mobile && unlocked(unlock)) {
							image.addListener(new HandCursorListener());
							image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
						}

						if (unlocked(unlock)) {
							image.clicked(() -> {
								if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
									Core.app.setClipboardText((char) Fonts.getUnicode(unlock.name) + "");
									ui.showInfoFade("@copied");
								} else {
									ui.content.show(unlock);
								}
							});
							image.addListener(new Tooltip(t -> t.background(Tex.button).add(unlock.localizedName + (settings.getBool("console") ? "\n[gray]" + unlock.name : ""))));
						}

						if ((++count) % cols == 0) {
							list.row();
						}
					}
				}).growX().left().padBottom(10);
				all.row();
			}

			if (all.getChildren().isEmpty()) {
				all.add("@none.found");
			}
		});
	}

	public static void hide(UnlockableContent content) {
		hiddenContents.add(content);
	}

	public static DatabaseDialog make() {
		return HVars.classHandler.getDynamicMaker().newInstance(Vars.ui.database.getClass(), new Class[]{RebuildAsp.class}, baseDatabase).objSelf();
	}

	private static boolean unlocked(UnlockableContent content) {
		return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
	}

	@AspectInterface
	public interface RebuildAsp {
		void rebuild();
	}
}
