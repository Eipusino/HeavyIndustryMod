package heavyindustry.ui;

import arc.Core;
import arc.audio.Sound;
import arc.flabel.FLabel;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Floatc;
import arc.func.Floatp;
import arc.func.Intc;
import arc.func.Intp;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.Scene;
import arc.scene.actions.Actions;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.TextField;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.TextField.TextFieldValidator;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.HVars;
import heavyindustry.ui.dialogs.GameDataDialog;
import heavyindustry.ui.dialogs.PowerGraphInfoDialog;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.core.World;
import mindustry.ctype.UnlockableContent;
import mindustry.game.SpawnGroup;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Links;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;
import mindustry.world.modules.ItemModule;

import java.lang.reflect.Field;

public final class Elements {
	public static final float LEN = 60f, OFFSET = 12f;

	public static PowerGraphInfoDialog powerInfoDialog;
	public static GameDataDialog gameDataDialog;

	static final Vec2 ctrlVec = new Vec2();
	static final Vec2 point = new Vec2(-1, -1);
	static final Table starter = new Table(Tex.paneSolid);

	static long lastToast;
	static Table pTable = new Table(), floatTable = new Table();

	static Field clickListenerField;

	/// Don't let anyone instantiate this class.
	private Elements() {}

	public static String judge(boolean value) {
		return value ? "[heal]" + Core.bundle.get("yes") + "[]" : "[#ff7b69]" + Core.bundle.get("no") + "[]";
	}

	public static void load() {
		powerInfoDialog = new PowerGraphInfoDialog();

		gameDataDialog = new GameDataDialog();
	}

	/// Based on {@link UI#formatAmount(long)} but for floats.
	public static String formatAmount(float num) {
		if (Float.isNaN(num)) return "NaN";
		if (num == Float.MAX_VALUE) return "Infinite";
		if (num == Float.MIN_VALUE) return "-Infinite";

		float mag = Math.abs(num);
		String sign = num < 0 ? "-" : "";
		if (mag >= 1000000000f) {
			return sign + Strings.fixed(mag / 1000000000f, 2) + "[gray]" + UI.billions + "[]";
		} else if (mag >= 1000000f) {
			return sign + Strings.fixed(mag / 1000000f, 2) + "[gray]" + UI.millions + "[]";
		} else if (mag >= 1000f) {
			return sign + Strings.fixed(mag / 1000f, 2) + "[gray]" + UI.thousands + "[]";
		} else {
			return sign + Strings.fixed(mag, 2);
		}
	}

	/// Similar to {@link UI#formatAmount(long)} but for floats.
	public static String round(float num) {
		if (Float.isNaN(num)) return "NaN";
		if (num == Float.MAX_VALUE) return "Infinite";
		if (num == Float.MIN_VALUE) return "-Infinite";

		if (num >= 1000000000f) {
			return Strings.autoFixed(num / 1000000000f, 1) + UI.billions;
		} else if (num >= 1000000f) {
			return Strings.autoFixed(num / 1000000f, 1) + UI.millions;
		} else if (num >= 1000f) {
			return Strings.autoFixed(num / 1000f, 1) + UI.thousands;
		} else {
			return Strings.autoFixed(num, 2);
		}
	}

	public static void number(Table main, String text, Floatc cons, Floatp prov) {
		number(main, text, false, cons, prov, () -> true, 0, Float.MAX_VALUE);
	}

	public static void number(Table main, String text, Floatc cons, Floatp prov, float min, float max) {
		number(main, text, false, cons, prov, () -> true, min, max);
	}

	public static void number(Table main, String text, boolean integer, Floatc cons, Floatp prov, Boolp condition) {
		number(main, text, integer, cons, prov, condition, 0, Float.MAX_VALUE);
	}

	public static void number(Table main, String text, Floatc cons, Floatp prov, Boolp condition) {
		number(main, text, false, cons, prov, condition, 0, Float.MAX_VALUE);
	}

	public static void number(Table main, String text, Intc cons, Intp prov, int min, int max) {
		number(main, text, cons, prov, () -> true, min, max);
	}

	public static void number(Table main, String text, boolean integer, Floatc cons, Floatp prov, Boolp condition, float min, float max) {
		main.table(t -> {
			t.left();
			t.add(text).left().padRight(5)
					.update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
			t.field(String.valueOf(integer ? (int) prov.get() : prov.get()), s -> cons.get(Strings.parseFloat(s)))
					.padRight(100f)
					.update(a -> a.setDisabled(!condition.get()))
					.valid(f -> Strings.canParsePositiveFloat(f) && Strings.parseFloat(f) >= min && Strings.parseFloat(f) <= max).width(120f).left();
		}).padTop(0);
		main.row();
	}

	public static void number(Table main, String text, Intc cons, Intp prov, Boolp condition, int min, int max) {
		main.table(t -> {
			t.left();
			t.add(text).left().padRight(5)
					.update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
			t.field(String.valueOf(prov.get()), s -> cons.get(Strings.parseInt(s)))
					.update(a -> a.setDisabled(!condition.get()))
					.padRight(100f)
					.valid(f -> Strings.parseInt(f) >= min && Strings.parseInt(f) <= max).width(120f).left();
		}).padTop(0).row();
	}

	public static ImageButton selfStyleImageButton(Drawable imageUp, ImageButtonStyle is, Runnable listener) {
		ImageButton button = new ImageButton(new ImageButtonStyle(null, null, null, imageUp, null, null));
		ImageButtonStyle style = new ImageButtonStyle(is);
		style.imageUp = imageUp;
		button.setStyle(style);
		if (listener != null) button.changed(listener);
		return button;
	}

	public static int countSpawns(SpawnGroup group) {
		if (group.spawn != -1)
			return 1; //If the group has a set spawn pos, assume it's a valid position and count it as 1 spawn.

		//Otherwise count all.
		if (group.type.flying)
			return Vars.spawner.countFlyerSpawns();

		return Vars.spawner.countGroundSpawns();
	}

	public static Element[] sliderSet(Table t, Cons<String> changed, Prov<String> fieldText, TextFieldFilter filter, TextFieldValidator valid, float min, float max, float step, float def, Cons2<Float, TextField> sliderChanged, String title, String tooltip) {
		TextField field = textField(String.valueOf(def), changed, fieldText, filter, valid);

		Label tab = t.add(title).right().padRight(6f).get();
		Slider sl = t.slider(min, max, step, def, s -> sliderChanged.get(s, field)).right().width(HVars.sliderWidth).get();
		TextField f = t.add(field).left().padLeft(6f).width(HVars.fieldWidth).get();

		if (tooltip != null) {
			Tooltip tip = baseTooltip(tt -> tt.background(Tex.button).add(tooltip));
			tab.addListener(tip);
			sl.addListener(tip);
			f.addListener(tip);
		}

		return new Element[]{sl, f};
	}

	public static TextField textField(String text, Cons<String> changed, Prov<String> setText, TextFieldFilter filter, TextFieldValidator valid) {
		TextField field = new TextField(text);
		if (filter != null) field.setFilter(filter);
		if (valid != null) field.setValidator(valid);
		field.changed(() -> {
			if (field.isValid()) changed.get(field.getText());
		});
		if (setText != null) {
			field.update(() -> {
				Scene stage = field.getScene();
				if (!(stage != null && stage.getKeyboardFocus() == field))
					field.setText(setText.get());
			});
		}

		return field;
	}

	public static Cell<ImageButton> imageButton(Table t, Drawable icon, ImageButtonStyle style, float isize, Runnable listener, Prov<CharSequence> label, String tooltip) {
		Cell<ImageButton> bCell = t.button(icon, style, isize, listener);
		ImageButton b = bCell.get();
		if (label != null) {
			Cell<Label> lab = b.label(label).padLeft(6f).expandX().name("label");
			lab.update(l -> {
				l.setText(label.get());
				l.setColor(b.isDisabled() ? Color.lightGray : Color.white);
			});
		}
		if (tooltip != null) boxTooltip(b, tooltip);

		return bCell;
	}

	public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text) {
		return itemImage(region, text, Color.white, Color.white, 1f, Align.bottomLeft);
	}

	public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text, Color icolor, Color tcolor, float fontScl, int align) {
		Stack stack = new Stack();

		Table t = new Table().align(align);
		t.label(text).color(tcolor).fontScale(fontScl);

		Image i = new Image(region).setScaling(Scaling.fit);
		i.setColor(icolor);

		stack.add(i);
		stack.add(t);
		return stack;
	}

	public static void divider(Table t, String label, Color color, int colSpan) {
		if (label != null) {
			t.add(label).growX().color(color).colspan(colSpan).left();
			t.row();
		}
		t.image().growX().pad(5f, 0f, 5f, 0f).height(3f).color(color).colspan(colSpan).left();
		t.row();
	}

	public static void divider(Table t, String label, Color color) {
		divider(t, label, color, 1);
	}

	/// Adds a boxed tooltip, similar to in the Database.
	public static void boxTooltip(Element e, Prov<CharSequence> text) {
		e.addListener(baseTooltip(t -> t.background(Tex.button).label(text)));
	}

	/// Adds a boxed tooltip, similar to in the Database.
	public static void boxTooltip(Element e, String text) {
		e.addListener(baseTooltip(t -> t.background(Tex.button).add(text)));
	}

	/// Adds a flat tooltip, similar to setting descriptions.
	public static void flatTooltip(Element e, Prov<CharSequence> text) {
		e.addListener(baseTooltip(t -> t.background(Styles.black8).margin(4f).label(text)));
	}

	/// Adds a flat tooltip, similar to setting descriptions.
	public static void flatTooltip(Element e, String text) {
		e.addListener(baseTooltip(t -> t.background(Styles.black8).margin(4f).add(text)));
	}

	/// Creates a tooltip. Snaps to corner of parent element.
	public static Tooltip baseTooltip(Cons<Table> content) {
		return new BaseTooltip(content);
	}

	public static FLabel infinity() {
		return new FLabel("{wave}{rainbow}" + Core.bundle.get("hi-infinity"));
	}

	public static FLabel everything() {
		return new FLabel("{wave}{rainbow}" + Core.bundle.get("hi-everything"));
	}

	public static boolean hasMouse() {
		Element e = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), false);
		return e != null && !e.fillParent;
	}

	public static Stack itemImage(TextureRegion region, int amount) {
		Stack stack = new Stack();

		stack.add(new Table(o -> {
			o.left();
			o.add(new Image(region)).size(32f).scaling(Scaling.fit);
		}));

		if (amount > 0) {
			stack.add(new Table(t -> {
				t.left().bottom();
				t.add(amount >= 1000 ? formatAmount(amount) : String.valueOf(amount)).style(Styles.outlineLabel);
				t.pack();
			}));
		}

		return stack;
	}

	public static Stack itemImage(ItemStack stack) {
		return itemImage(stack.item.uiIcon, stack.amount);
	}

	public static Stack itemImage(PayloadStack stack) {
		return itemImage(stack.item.uiIcon, stack.amount);
	}

	public static void collapseTextToTable(Table t, String text) {
		Table ic = new Table();
		ic.add(text).wrap().fillX().width(500f).padTop(2).padBottom(6).left();
		ic.row();
		Collapser coll = new Collapser(ic, true);
		coll.setDuration(0.1f);
		t.row();
		t.table(st -> {
			st.add(Core.bundle.get("hi-click-to-show")).center();
			st.row();
			st.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).pad(5).size(8).center();
		}).left();
		t.row();
		t.add(coll);
		t.row();
	}

	public static void statToTable(Stats stat, Table table) {
		Seq<StatCat> m = stat.toMap().keys().toSeq();
		for (int i = 0; i < m.size; i++) {
			Seq<Stat> s = stat.toMap().get(m.get(i)).keys().toSeq();
			for (int j = 0; j < s.size; j++) {
				Seq<StatValue> v = stat.toMap().get(m.get(i)).get(s.get(j));
				for (int k = 0; k < v.size; k++) {
					v.get(k).display(table);
				}
			}
		}
	}

	public static void statTurnTable(Stats stats, Table table) {
		for (StatCat cat : stats.toMap().keys()) {
			OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

			if (map.size == 0) continue;

			if (stats.useCategories) {
				table.add("@category." + cat.name).color(Pal.accent).fillX();
				table.row();
			}

			for (Stat stat : map.keys()) {
				table.table(inset -> {
					inset.left();
					inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
					Seq<StatValue> arr = map.get(stat);
					for (StatValue value : arr) {
						value.display(inset);
						inset.add().size(10f);
					}

				}).fillX().padLeft(10);
				table.row();
			}
		}
	}

	public static void addToTable(UnlockableContent c, Table t) {
		t.row();
		t.table(log -> {
			log.table(Styles.grayPanel, img -> {
				img.button(bt -> bt.image(c.uiIcon).size(64).pad(5), Styles.cleari, () -> Vars.ui.content.show(c)).left().tooltip("click to show");
			}).pad(10).margin(10).left();
			log.image(Tex.whiteui, Pal.accent).growY().width(3).pad(4).margin(5).left();
			log.table(info -> {
				Label n = info.add(c.localizedName).wrap().fillX().left().maxWidth(Core.graphics.getWidth() / 2f).get();
				info.row();
				info.image(Tex.whiteui, Pal.accent).left().width(n.getWidth() * 1.3f).height(3f).row();
				info.add(c.description).wrap().fillX().left().width(Core.graphics.getWidth() / 2f).padTop(10).row();
				info.image(Tex.whiteui, Pal.accent).left().width(Core.graphics.getWidth() / 2f).height(3f).row();
			}).left().pad(6);
		});
	}

	static boolean pointValid() {
		return point.x >= 0 && point.y >= 0 && point.x <= Vars.world.width() * Vars.tilesize && point.y <= Vars.world.height() * Vars.tilesize;
	}

	public static int getLineNum(String string) {
		String dex = string.replaceAll("\r", "\n");
		return dex.split("\n").length;
	}

	public static void disableTable() {
		Core.scene.root.removeChild(starter);
	}

	public static void showTable() {
		Core.scene.root.addChildAt(3, starter);
	}

	public static void showInner(Table parent, Table children) {
		Inner inner = new Inner();

		parent.addChildAt(parent.getZIndex() + 1, inner);
		inner.init(parent.getWidth() + children.getWidth() + OFFSET);

		children.fill().pack();
		children.setTransform(true);
		inner.addChildAt(parent.getZIndex() + 2, children);
		inner.setScale(parent.scaleX, parent.scaleY);
		children.setScale(parent.scaleX, parent.scaleY);


		children.setPosition(inner.getWidth() - children.getWidth(), inner.y + (inner.getHeight() - children.getHeight()) / 2);

		inner.actions(Actions.moveTo(0, inner.y, 0.35f, Interp.pow3Out));
	}

	public static Table tableImageShrink(TextureRegion tex, float size, Table table) {
		return tableImageShrink(tex, size, table, c -> {});
	}

	public static Table tableImageShrink(TextureRegion tex, float size, Table table, Cons<Image> modifier) {
		float parma = Math.max(tex.height, tex.width);
		float f = Math.min(size, parma);
		Image image = new Image(tex);
		modifier.get(image);
		table.add(image).size(tex.width * f / parma, tex.height * f / parma);

		return table;
	}

	public static void itemStack(Table parent, ItemStack stack, ItemModule itemModule) {
		float size = LEN - OFFSET;
		parent.table(table -> {
			table.image(stack.item.fullIcon).size(size).left();
			table.table(table1 -> {
				Label label = new Label("");
				table1.add(stack.item.localizedName + " ").left();
				table1.add(label).left();
				table1.add("/" + UI.formatAmount(stack.amount)).left().growX();
				table1.update(() -> {
					int amount = itemModule == null ? 0 : itemModule.get(stack.item);
					label.setText(UI.formatAmount(amount));
					label.setColor(amount < stack.amount ? Pal.redderDust : Color.white);
				});
			}).growX().height(size).padLeft(OFFSET / 2).left();
		}).growX().height(size).left().row();
	}

	public static void selectPos(Table parentTable, Cons<Point2> cons) {
		Prov<Touchable> original = parentTable.touchablility;
		Touchable parentTouchable = parentTable.touchable;

		parentTable.touchablility = () -> Touchable.disabled;

		if (!pTable.hasParent()) ctrlVec.set(Core.camera.unproject(Core.input.mouse()));

		if (!pTable.hasParent()) pTable = new Table(Tex.clear) {{
			update(() -> {
				if (Vars.state.isMenu()) {
					remove();
				} else {
					Vec2 v = Core.camera.project(World.toTile(ctrlVec.x) * Vars.tilesize, World.toTile(ctrlVec.y) * Vars.tilesize);
					setPosition(v.x, v.y, 0);
				}
			});
		}
			@Override
			public void draw() {
				super.draw();

				Lines.stroke(9, Pal.gray);
				drawLines();
				Lines.stroke(3, Pal.accent);
				drawLines();
			}

			void drawLines() {
				Lines.square(x, y, 28, 45);
				Lines.line(x - OFFSET * 4, y, 0, y);
				Lines.line(x + OFFSET * 4, y, Core.graphics.getWidth(), y);
				Lines.line(x, y - OFFSET * 4, x, 0);
				Lines.line(x, y + OFFSET * 4, x, Core.graphics.getHeight());
			}
		};

		if (!pTable.hasParent()) floatTable = new Table(Tex.clear) {{
			update(() -> {
				if (Vars.state.isMenu()) remove();
			});
			touchable = Touchable.enabled;
			setFillParent(true);

			addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
					ctrlVec.set(Core.camera.unproject(x, y));
					return false;
				}
			});
		}};

		pTable.button(Icon.cancel, Styles.emptyi, () -> {
			cons.get(Tmp.p1.set(World.toTile(ctrlVec.x), World.toTile(ctrlVec.y)));
			parentTable.touchablility = original;
			parentTable.touchable = parentTouchable;
			pTable.remove();
			floatTable.remove();
		}).center();

		Core.scene.root.addChildAt(Math.max(parentTable.getZIndex() - 1, 0), pTable);
		Core.scene.root.addChildAt(Math.max(parentTable.getZIndex() - 2, 0), floatTable);
	}

	static void scheduleToast(Runnable run) {
		long duration = (int) (3.5 * 1000);
		long since = Time.timeSinceMillis(lastToast);
		if (since > duration) {
			lastToast = Time.millis();
			run.run();
		} else {
			Time.runTask((duration - since) / 1000f * 60f, run);
			lastToast += duration;
		}
	}

	public static void countdown(Element element, Floatp remainTime) {
		element.addListener(new Tooltip(table -> {
			table.background(Tex.bar);
			table.color.set(Color.black);
			table.color.a = 0.35f;
			table.add("Remain Time: 00:00 ").update(label -> {
				float remain = remainTime.get();
				label.setText("[gray]Remain Time: " + ((remain / Time.toSeconds > 15) ? "[]" : "[accent]") + Mathf.floor(remain / Time.toMinutes) + ":" + Mathf.floor((remain % Time.toMinutes) / Time.toSeconds));
			}).left().fillY().growX().row();
		}));
	}

	public static void showToast(Drawable icon, String text, Sound sound) {
		if (Vars.state.isMenu()) return;

		scheduleToast(() -> {
			sound.play();

			Table table = new Table(Tex.button);
			table.update(() -> {
				if (Vars.state.isMenu() || !Vars.ui.hudfrag.shown) {
					table.remove();
				}
			});
			table.margin(12);
			table.image(icon).pad(3);
			table.add(text).wrap().width(280f).get().setAlignment(Align.center, Align.center);
			table.pack();

			//create container table which will align and move
			Table container = Core.scene.table();
			container.top().add(table);
			container.setTranslation(0, table.getPrefHeight());
			container.actions(
					Actions.translateBy(0, -table.getPrefHeight(), 1f, Interp.fade), Actions.delay(2.5f),
					//nesting actions() calls is necessary so the right prefHeight() is used
					Actions.run(() -> container.actions(Actions.translateBy(0, table.getPrefHeight(), 1f, Interp.fade), Actions.remove()))
			);
		});
	}

	public static void link(Table parent, Links.LinkEntry link) {
		parent.add(new LinkTable(link)).size(LinkTable.w + OFFSET * 2f, LinkTable.h).padTop(OFFSET / 2f).row();
	}

	/// Adds '\n' if text not within maxWidth.
	public static String wrapText(String originalString, Font font, float maxWidth) {
		GlyphLayout obtain = GlyphLayout.obtain();

		obtain.setText(font, originalString);
		if (obtain.width <= maxWidth) {
			obtain.free();
			return originalString;
		}
		String[] words = originalString.split(" ");
		StringBuilder builder = new StringBuilder();
		int wordIndex = 0;
		while (wordIndex < words.length) {
			builder.append(words[wordIndex]);
			if (wordIndex + 1 == words.length) {
				break;
			}
			obtain.setText(font, builder + " " + words[wordIndex + 1]);
			if (obtain.width <= maxWidth) {
				builder.append(" ");
			} else {
				builder.append("\n");
			}
			wordIndex++;
		}
		obtain.free();
		return builder.toString();
	}

	@Nullable
	public static Element hovered(Boolf<Element> validator) {
		Element e = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);
		if (e != null) {
			while (e != null && !validator.get(e)) {
				e = e.parent;
			}
		}
		return e;
	}

	public static <T extends Element> T hitChild(Group group, float stageX, float stageY) {
		return hitChild(group, stageX, stageY, null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Element> T hitChild(Group group, float stageX, float stageY, @Nullable Boolf<Element> filter) {
		//noinspection unchecked
		return (T) group.getChildren().find(it -> {
			if (filter != null && !filter.get(it)) {
				return false;
			}

			it.stageToLocalCoordinates(Tmp.v1.set(stageX, stageY));
			return it.hit(Tmp.v1.x, Tmp.v1.y, false) != null;
		});
	}

	public static int invertAlign(int align) {
		int result = align & Align.center;

		if ((align & Align.left) != 0) {
			result |= Align.right;
		}
		if ((align & Align.right) != 0) {
			result |= Align.left;
		}
		if ((align & Align.top) != 0) {
			result |= Align.bottom;
		}
		if ((align & Align.bottom) != 0) {
			result |= Align.top;
		}
		return result;
	}

	public static float getX(float x, float width, int align) {
		float offset = 0;
		if ((align & Align.right) != 0) {
			offset = width;
		}
		if ((align & Align.center) != 0) {
			offset = width / 2f;
		}
		return x + offset;
	}

	public static float getY(float y, float height, int align) {
		float offset = 0;
		if ((align & Align.top) != 0) {
			offset = height;
		}
		if ((align & Align.center) != 0) {
			offset = height / 2f;
		}
		return y + offset;
	}

	public static void replaceClickListener(Button button, ClickListener newListener) {
		button.removeListener(button.getClickListener());

		try {
			if (clickListenerField == null) {
				clickListenerField = Button.class.getDeclaredField("clickListener");
				clickListenerField.setAccessible(true);
			}
			clickListenerField.set(button, newListener);
			button.addListener(newListener);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Log.err(e);
		}
	}

	public static class BaseTooltip extends Tooltip {
		public BaseTooltip(Cons<Table> contents) {
			super(contents);

			allowMobile = true;
		}

		@Override
		protected void setContainerPosition(Element element, float x, float y) {
			targetActor = element;
			Vec2 pos = element.localToStageCoordinates(Tmp.v1.set(0, 0));
			container.pack();

			boolean offBottom = pos.y - container.getHeight() < 0;
			boolean offRight = pos.x + container.getWidth() > Core.graphics.getWidth();

			float pY = offBottom ? pos.y + element.getHeight() : pos.y;
			float pX = offRight ? pos.x + element.getWidth() : pos.x;
			container.setPosition(pX, pY, (offBottom ? Align.bottom : Align.top) | (offRight ? Align.right : Align.left));
			container.setOrigin(offRight ? container.getWidth() : 0, offBottom ? 0 : container.getHeight());
		}
	}

	static class Inner extends Table {
		public Inner() {
			init();
		}

		public void init() {
			name = "INNER";
			background(Tex.paneSolid);

			left();
			table(table -> {
				table.button(Icon.cancel, Styles.cleari, () -> {
					actions(Actions.touchable(Touchable.disabled), Actions.moveBy(-width, 0, 0.4f, Interp.pow3In), Actions.remove());
				}).width(LEN).growY();
			}).growY().fillX().padRight(OFFSET);
		}

		public void init(float wid) {
			setSize(wid, starter.getHeight());
			setPosition(-width, starter.originY);
		}
	}

	public static class LinkTable extends Table {
		protected static float h = Core.graphics.isPortrait() ? 90f : 80f;
		protected static float w = Core.graphics.isPortrait() ? 330f : 600f;

		public LinkTable(Links.LinkEntry link) {
			background(Tex.underline);
			margin(0);
			table(img -> {
				img.image().height(h - OFFSET / 2).width(LEN).color(link.color);
				img.row();
				img.image().height(OFFSET / 2).width(LEN).color(link.color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
			}).expandY();

			table(i -> {
				i.background(Tex.buttonEdge3);
				i.image(link.icon);
			}).size(h - OFFSET / 2, h);

			table(inset -> {
				inset.add("[accent]" + link.title).growX().left();
				inset.row();
				inset.labelWrap(link.description).width(w - LEN).color(Color.lightGray).growX();
			}).padLeft(OFFSET / 1.5f);

			button(Icon.link, () -> {
				if (!Core.app.openURI(link.link)) {
					Vars.ui.showErrorMessage("@linkfail");
					Core.app.setClipboardText(link.link);
				}
			}).size(h);
		}

		public static void sync() {
			h = Core.graphics.isPortrait() ? 90f : 80f;
			w = Core.graphics.isPortrait() ? 300f : 600f;
		}
	}
}