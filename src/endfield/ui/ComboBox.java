package endfield.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.Button;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.Label;
import arc.scene.ui.Label.LabelStyle;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Tmp;
import dynamilize.Variable.ObjectReference;
import endfield.ui.Listeners.ClickOnOtherListener;
import endfield.util.CollectionList;
import org.jetbrains.annotations.Nullable;

public class ComboBox extends Table {
	private final Table mySelectionTable;

	public CollectionList<ComboBoxItem> items = new CollectionList<>(ComboBoxItem.class);
	public CollectionList<ComboBoxItemSelectListener> listeners = new CollectionList<>(ComboBoxItemSelectListener.class);

	private TextField myField;
	private Button myButton;
	private int prevSize = 0;
	private int selectedItem = 0;

	public ComboBox() {
		mySelectionTable = new Table();

		Core.scene.addListener(new ClickOnOtherListener(() -> {
			if (hasParent()) {
				remove();
			}
			return false;
		}, it -> isAscendantOf(it) || mySelectionTable.isAscendantOf(it)));

		add(myField = new TextField() {
			{
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

				float x0 = centerX;
				float y0 = centerY - size / 4f;
				float x1 = centerX - size / 4f;
				float y1 = centerY + size / 4f;
				float x2 = centerX + size / 4f;
				float y2 = centerY + size / 4f;

				//ComboBox.this.getBackground().
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
//            return;
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
				ObjectReference<LabelStyle> labelStyle = new ObjectReference<>();
				Cell<Label> label = it.label(() -> {
					if (labelStyle.value != null) {
						labelStyle.value.font = items.get(j).style.font;
					}
					return items.get(j).text;
				});
				label.get().setStyle(labelStyle.value = new LabelStyle(label.get().getStyle()) {{
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

	public @Nullable ComboBoxItem getItem(int index) {
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

		public ComboBoxItem(Drawable image, String text, ComboBoxItemStyle style) {
			this(style);
			style.image = image;
			this.text = text;
		}

		public ComboBoxItem(ComboBoxItemStyle style) {
			this.style = style;
		}

		public static class ComboBoxItemStyle {
			public Font font;
			public Drawable image;
			public Color backgroundColor = new Color();
			public ButtonStyle buttonStyle;

			public ComboBoxItemStyle() {

			}

			public ComboBoxItemStyle(ComboBoxItemStyle style) {
				this.font = style.font;
				this.image = style.image;
				this.backgroundColor = style.backgroundColor;
				this.buttonStyle = style.buttonStyle;
			}
		}
	}
}
