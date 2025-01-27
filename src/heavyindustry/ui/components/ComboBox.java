package heavyindustry.ui.components;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.ui.listeners.Listeners.*;
import kotlin.jvm.internal.Ref.*;

public class ComboBox extends Table {
	private final Table mySelectionTable = new Table() {{
		Core.scene.addListener(new ClickOnOtherListener(() -> {
			if (hasParent()) {
				remove();
			}
			return false;
		}, it -> ComboBox.this.isAscendantOf(it) || isAscendantOf(it)));
	}};
	public Seq<ComboBoxItem> items = new Seq<>();
	public Seq<ComboBoxItemSelectListener> listeners = new Seq<>();

	private TextField myField;
	private Button myButton;
	private int prevSize = 0;
	private int selectedItem = 0;

	public ComboBox() {
		add(myField = new TextField() {{
			setDisabled(true);
		}
			@Override
			public float getPrefWidth() {
				return mySelectionTable.getPrefWidth();
			}
		}).grow();
		add(myButton = new Button()).fillX().minHeight(48f).width(48f)
				.checked(i -> mySelectionTable.hasParent())
				.get().clicked(this::toggleSelectionTable);

		myButton.add(new Element() {
			@Override
			public void draw() {
				float centerY = y + height / 2f;
				float centerX = x + width / 2f;
				float size = Scl.scl(48f) * Mathf.sign(!mySelectionTable.hasParent());

				//noinspection UnnecessaryLocalVariable
				float x0 = centerX;
				float y0 = centerY - size / 4f;
				float x1 = centerX - size / 4f;
				float y1 = centerY + size / 4f;
				float x2 = centerX + size / 4f;
				float y2 = centerY + size / 4f;

				Draw.color(color);
				Fill.tri(x0, y0, x1, y1, x2, y2);
			}
		});
	}

	private void toggleSelectionTable() {
		if (mySelectionTable.hasParent()) {
			hideSelectionTable();
		} else {
			showSelectionTable();
		}
	}

	public void addItems(String... names) {
		for (String name : names) {
			addItem(name);
		}
	}

	public ComboBoxItem addItem(String name) {
		ComboBoxItem item = new ComboBoxItem(name);
		items.add(item);
		return item;
	}

	private void hideSelectionTable() {
		mySelectionTable.remove();
	}

	private void showSelectionTable() {
		if (mySelectionTable.hasParent()) {
			hideSelectionTable();
		}

		mySelectionTable.update(() -> {
			Vec2 vec2 = localToStageCoordinates(Tmp.v1.set(0f, 0f));

			mySelectionTable.setSize(myField.getWidth(), mySelectionTable.getPrefHeight());
			mySelectionTable.setPosition(vec2.x, vec2.y - mySelectionTable.getHeight());
		});
		mySelectionTable.act(0);
		mySelectionTable.pack();


		Core.scene.add(mySelectionTable);
	}

	@Override
	public void act(float delta) {
		if (prevSize != items.size) {
			rebuildItems();
		}
		Vec2 vec2 = localToStageCoordinates(Tmp.v1.set(width / 2f, height / 2f));
		float centerY = vec2.y;
		Vec2 thisPosition = localToStageCoordinates(vec2.set(0, 0));
		if (centerY < Core.scene.getHeight() / 3f) {
			thisPosition.set(thisPosition.x, thisPosition.y + height);
		} else {
			thisPosition.set(thisPosition.x, thisPosition.y - mySelectionTable.getHeight());
		}
		mySelectionTable.setPosition(thisPosition.x, thisPosition.y);
		mySelectionTable.keepInStage();
		mySelectionTable.invalidateHierarchy();
		mySelectionTable.pack();

		ComboBoxItem item = getItem(selectedItem);
		if (item != null) myField.setText(item.text);

		super.act(delta);
	}

	private void rebuildItems() {
		mySelectionTable.clearChildren();

		for (int i = 0; i < items.size; i++) {
			int j = i;
			mySelectionTable.button(it -> {
				it.fill((x, y, width, height) -> {
					Draw.color(items.get(j).style.backgroundColor);
					Fill.crect(x, y, width, height);
				}).toBack();

				it.imageDraw(() -> items.get(j).style.image).self(cell -> {
				});
				ObjectRef<LabelStyle> labelStyleObjectRef = new ObjectRef<>();
				Cell<Label> label = it.label(() -> {
					if (labelStyleObjectRef.element != null) {
						labelStyleObjectRef.element.font = items.get(j).style.font;
					}
					return items.get(j).text;
				});
				label.get().setStyle(labelStyleObjectRef.element = new LabelStyle(label.get().getStyle()) {{
					font = items.get(j).style.font;
				}});
				label.left().grow().labelAlign(Align.left, Align.left);
			}, () -> {
				if (items.get(j).canSelect) {
					selectItem(j);
				}
			}).minHeight(32f).growX().fillY().update(button -> {
				ComboBoxItem item = items.get(j);
				button.setDisabled(!item.canSelect);
				button.setStyle(item.style.buttonStyle);

			});
			mySelectionTable.row();
		}
		mySelectionTable.setHeight(32f * 5f * Scl.scl());
		prevSize = items.size;
	}

	public void selectItem(int index) {
		hideSelectionTable();

		ComboBoxItem newItem = getItem(index);
		ComboBoxItem oldItem = getItem(selectedItem);

		selectedItem = index;
		for (ComboBoxItemSelectListener listener : listeners) {
			listener.listen(oldItem, newItem);
		}
	}

	@Nullable
	public ComboBoxItem getItem(int index) {
		return index == -1 ? null : items.get(index);
	}

	public void addItemListener(ComboBoxItemSelectListener listener) {
		listeners.add(listener);
	}

	public interface ComboBoxItemSelectListener {
		void listen(@Nullable ComboBoxItem old, ComboBoxItem newItem);
	}

	public static class ComboBoxItem {
		public @Nullable String text;
		public boolean canSelect = true;
		public Object object;
		public ComboBoxItemStyle style;

		public ComboBoxItem() {
			this(Core.scene.getStyle(ComboBoxItemStyle.class));
		}

		public ComboBoxItem(String text) {
			this(text, Core.scene.getStyle(ComboBoxItemStyle.class));
		}

		public ComboBoxItem(String text, ComboBoxItemStyle style) {
			this(style);
			this.text = text;
		}

		public ComboBoxItem(Drawable image, String text) {
			this(image, text, new ComboBoxItemStyle(Core.scene.getStyle(ComboBoxItemStyle.class)));
		}

		public ComboBoxItem(Drawable image, String string, ComboBoxItemStyle style) {
			this(style);
			style.image = image;
			text = string;
		}

		public ComboBoxItem(ComboBoxItemStyle style) {
			this.style = style;
		}

		public static class ComboBoxItemStyle {
			public Font font;
			public Drawable image;
			public Color backgroundColor = new Color();
			public ButtonStyle buttonStyle;

			public ComboBoxItemStyle() {}

			public ComboBoxItemStyle(ComboBoxItemStyle style) {
				font = style.font;
				image = style.image;
				backgroundColor = style.backgroundColor;
				buttonStyle = style.buttonStyle;
			}
		}
	}
}
