package heavyindustry.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.HVars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.MenuRenderer;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.MobileButton;
import mindustry.ui.Styles;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.becontrol;
import static mindustry.Vars.ios;
import static mindustry.Vars.mobile;
import static mindustry.Vars.mods;
import static mindustry.Vars.platform;
import static mindustry.Vars.steam;
import static mindustry.Vars.ui;

@Deprecated
public class UnemFragment extends MenuFragment {
	public int frames = 89;
	public float framesInterval = 2.5f;

	protected Table container, submenu;
	protected Button currentMenu;
	protected MenuRenderer renderer;
	protected Seq<MenuButton> customButtons = new Seq<>();

	public void build(Group parent) {
		renderer = new MenuRenderer();

		Group group = new WidgetGroup();
		group.setFillParent(true);
		group.visible(() -> !ui.editor.isShown());
		parent.addChild(group);

		parent = group;

		parent.fill((x, y, w, h) -> renderer.render());

		parent.fill(c -> {
			c.pane(Styles.noBarPane, cont -> {
				container = cont;
				cont.name = "menu container";

				if (!mobile) {
					c.left();
					buildDesktop();
					Events.on(EventType.ResizeEvent.class, event -> buildDesktop());
				} else {
					buildMobile();
					Events.on(EventType.ResizeEvent.class, event -> buildMobile());
				}
			}).with(pane -> {
				pane.setOverscroll(false, false);
			}).grow();
		});

		parent.fill(c -> c.bottom().right().button(Icon.discord, new ImageButton.ImageButtonStyle() {{
			up = Tex.discordBanner;
		}}, ui.discord::show).marginTop(9f).marginLeft(10f).tooltip("@discord").size(84, 45).name("discord"));

		//info icon
		if (mobile) {
			parent.fill(c -> c.bottom().left().button("", new TextButtonStyle() {{
				font = Fonts.def;
				fontColor = Color.white;
				up = Tex.infoBanner;
			}}, ui.about::show).size(84, 45).name("info"));

			parent.fill((x, y, w, h) -> {
				if (Core.scene.marginBottom > 0) {
					Tex.paneTop.draw(0, 0, Core.graphics.getWidth(), Core.scene.marginBottom);
				}
			});
		} else if (becontrol.active()) {
			parent.fill(c -> c.bottom().right().button("@be.check", Icon.refresh, () -> {
				ui.loadfrag.show();
				becontrol.checkUpdate(result -> {
					ui.loadfrag.hide();
					if (!result) {
						ui.showInfo("@be.noupdates");
					}
				});
			}).size(200, 60).name("becheck").update(t -> {
				t.getLabel().setColor(becontrol.isUpdateAvailable() ? Tmp.c1.set(Color.white).lerp(Pal.accent, Mathf.absin(5f, 1f)) : Color.white);
			}));
		}

		String versionText = ((Version.build == -1) ? "[#fc8140aa]" : "[#ffffffba]") + Version.combined();
		parent.fill((x, y, w, h) -> {

			TextureRegion[] logos = new TextureRegion[frames];

			for (int i = 0; i < frames; i++) {
				logos[i] = Core.atlas.find(HVars.MOD_NAME + "-image" + i);
			}

			int index = (int) ((Time.time / 100 * 60 / framesInterval) % frames);

			TextureRegion logo = logos[index];

			float width = Core.graphics.getWidth(), height = Core.graphics.getHeight() - Core.scene.marginTop;
			float logoscl = Scl.scl(1) * logo.scale;
			float logow = Math.min(logo.width * logoscl, Core.graphics.getWidth() - Scl.scl(20));
			float logoh = logow * (float) logo.height / logo.width;

			float fx = (int) (width / 2f);
			float fy = (int) (height - 6 - logoh) + logoh / 2 - (Core.graphics.isPortrait() ? Scl.scl(30f) : 0f);

			Draw.color();
			Draw.rect(logo, fx, fy, logow, logoh);

			Fonts.outline.setColor(Color.white);
			Fonts.outline.draw(versionText, fx, fy - logoh / 2f - Scl.scl(2f), Align.center);
		}).touchable = Touchable.disabled;
	}

	protected void buildMobile() {
		container.clear();
		container.name = "buttons";
		container.setSize(Core.graphics.getWidth(), Core.graphics.getHeight());

		float size = 120f;
		container.defaults().size(size).pad(5).padTop(4f);

		MobileButton
				play = new MobileButton(Icon.play, "@campaign", () -> checkPlay(ui.planet::show)),
				custom = new MobileButton(Icon.rightOpenOut, "@customgame", () -> checkPlay(ui.custom::show)),
				maps = new MobileButton(Icon.download, "@loadgame", () -> checkPlay(ui.load::show)),
				join = new MobileButton(Icon.add, "@joingame", () -> checkPlay(ui.join::show)),
				editor = new MobileButton(Icon.terrain, "@editor", () -> checkPlay(ui.maps::show)),
				tools = new MobileButton(Icon.settings, "@settings", ui.settings::show),
				mods = new MobileButton(Icon.book, "@mods", ui.mods::show),
				exit = new MobileButton(Icon.exit, "@quit", () -> Core.app.exit()),
				about = new MobileButton(Icon.info, "@about.button", ui.about::show);

		Seq<MobileButton> customs = customButtons.map(b -> new MobileButton(b.icon, b.text, b.runnable == null ? () -> {
		} : b.runnable));

		if (!Core.graphics.isPortrait()) {
			container.marginTop(60f);
			container.add(play);
			container.add(join);
			container.add(custom);
			container.add(maps);
			// add odd custom buttons
			for (int i = 1; i < customs.size; i += 2) {
				container.add(customs.get(i));
			}
			container.row();

			container.add(editor);
			container.add(tools);
			container.add(mods);
			// add even custom buttons (before the exit button)
			for (int i = 0; i < customs.size; i += 2) {
				container.add(customs.get(i));
			}
			container.add(ios ? about : exit);
		} else {
			container.marginTop(0f);
			container.add(play);
			container.add(maps);
			container.row();
			container.add(custom);
			container.add(join);
			container.row();
			container.add(editor);
			container.add(tools);
			container.row();
			container.add(mods);
			// add custom buttons
			for (int i = 0; i < customs.size; i++) {
				container.add(customs.get(i));
				if (i % 2 == 0) container.row();
			}
			container.add(ios ? about : exit);
		}
	}

	protected void buildDesktop() {
		container.clear();
		container.setSize(Core.graphics.getWidth(), Core.graphics.getHeight());

		float width = 230f;
		Drawable background = Styles.black6;

		container.left();
		container.add().width(Core.graphics.getWidth() / 10f);
		container.table(background, t -> {
			t.defaults().width(width).height(70f);
			t.name = "buttons";

			buttons(t,
					new MenuButton("@play", Icon.play,
							new MenuButton("@campaign", Icon.play, () -> checkPlay(ui.planet::show)),
							new MenuButton("@joingame", Icon.add, () -> checkPlay(ui.join::show)),
							new MenuButton("@customgame", Icon.terrain, () -> checkPlay(ui.custom::show)),
							new MenuButton("@loadgame", Icon.download, () -> checkPlay(ui.load::show))
					),
					new MenuButton("@database.button", Icon.menu,
							new MenuButton("@schematics", Icon.paste, ui.schematics::show),
							new MenuButton("@database", Icon.book, ui.database::show),
							new MenuButton("@about.button", Icon.info, ui.about::show)
					),
					new MenuButton("@editor", Icon.terrain, () -> checkPlay(ui.maps::show)), steam ? new MenuButton("@workshop", Icon.steam, platform::openWorkshop) : null,
					new MenuButton("@mods", Icon.book, ui.mods::show),
					new MenuButton("@settings", Icon.settings, ui.settings::show)
			);
			buttons(t, customButtons.toArray(MenuButton.class));
			buttons(t, new MenuButton("@quit", Icon.exit, Core.app::exit));
		}).width(width).growY();

		container.table(background, t -> {
			submenu = t;
			t.name = "submenu";
			t.color.a = 0f;
			t.top();
			t.defaults().width(width).height(70f);
			t.visible(() -> !t.getChildren().isEmpty());

		}).width(width).growY();
	}

	protected void checkPlay(Runnable run) {
		if (!mods.hasContentErrors()) {
			run.run();
		} else {
			ui.showInfo("@mod.noerrorplay");
		}
	}

	protected void fadeInMenu() {
		submenu.clearActions();
		submenu.actions(Actions.alpha(1f, 0.15f, Interp.fade));
	}

	protected void fadeOutMenu() {
		//nothing to fade out
		if (submenu.getChildren().isEmpty()) {
			return;
		}

		submenu.clearActions();
		submenu.actions(Actions.alpha(1f), Actions.alpha(0f, 0.2f, Interp.fade), Actions.run(() -> submenu.clearChildren()));
	}

	protected void buttons(Table t, MenuButton... buttons) {
		for (MenuButton b : buttons) {
			if (b == null) continue;
			Button[] out = {null};
			out[0] = t.button(b.text, b.icon, Styles.flatToggleMenut, () -> {
				if (currentMenu == out[0]) {
					currentMenu = null;
					fadeOutMenu();
				} else {
					if (b.submenu.length > 0) {
						currentMenu = out[0];
						submenu.clearChildren();
						fadeInMenu();
						//correctly offset the button
						submenu.add().height((Core.graphics.getHeight() - Core.scene.marginTop - Core.scene.marginBottom - out[0].getY(Align.topLeft)) / Scl.scl(1f));
						submenu.row();
						buttons(submenu, b.submenu);
					} else {
						currentMenu = null;
						fadeOutMenu();
						b.runnable.run();
					}
				}
			}).marginLeft(11f).get();
			out[0].update(() -> out[0].setChecked(currentMenu == out[0]));
			t.row();
		}
	}

	/** Adds a custom button to the menu. */
	public void addButton(String text, Drawable icon, Runnable callback) {
		addButton(new MenuButton(text, icon, callback));
	}

	/** Adds a custom button to the menu. */
	public void addButton(String text, Runnable callback) {
		addButton(text, Styles.none, callback);
	}

	/**
	 * Adds a custom button to the menu.
	 * If {@link MenuButton#submenu} is null or the player is on mobile, {@link MenuButton#runnable} is invoked on click.
	 * Otherwise, {@link MenuButton#submenu} is shown.
	 */
	public void addButton(MenuButton button) {
		customButtons.add(button);
	}

	/** Represents a menu button definition. */
	public static class MenuButton {
		public final Drawable icon;
		public final String text;
		/** Runnable ran when the button is clicked. Ignored on desktop if {@link #submenu} is not null. */
		public final Runnable runnable;
		/** Submenu shown when this button is clicked. Used instead of {@link #runnable} on desktop. */
		public final MenuButton[] submenu;

		/** Constructs a simple menu button, which behaves the same way on desktop and mobile. */
		public MenuButton(String tex, Drawable ico, Runnable run) {
			icon = ico;
			text = tex;
			runnable = run;
			submenu = new MenuButton[0];
		}

		/** Constructs a button that runs the runnable when clicked on mobile or shows the submenu on desktop. */
		public MenuButton(String tex, Drawable ico, Runnable run, MenuButton... sub) {
			icon = ico;
			text = tex;
			runnable = run;
			submenu = sub;
		}

		/** Comstructs a desktop-only button; used internally. */
		MenuButton(String tex, Drawable ico, MenuButton... sub) {
			icon = ico;
			text = tex;
			runnable = () -> {};
			submenu = sub;
		}
	}
}
