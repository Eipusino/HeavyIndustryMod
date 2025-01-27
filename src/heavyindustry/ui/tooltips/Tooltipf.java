package heavyindustry.ui.tooltips;

import arc.func.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.ui.defaults.*;
import kotlin.jvm.internal.Ref.*;

public final class Tooltipf {
	public static Cons2<String, Table> defaultTooltipTable = (text, table) ->
			table.background(DefaultBackground.black6()).margin(4f).add(text);

	/** Don't let anyone instantiate this class. */
	private Tooltipf() {}

	public static <T extends Element> Cell<T> mutableTooltip(Table table, T element, ObjectRef<String> tooltipText) {
		return table.add(element).tooltip(it ->
				it.background(DefaultBackground.black6())
						.margin(4)
						.label(() -> tooltipText.element)
						.visible(() -> tooltipText.element != null)
		);
	}

	public static SideTooltip create(String text) {
		return create(Align.topRight, text);
	}

	public static SideTooltip create(int align, String text) {
		return new SideTooltip(align, it -> defaultTooltipTable.get(text, it));
	}

	public static <T extends Element> Cell<T> tooltipSide(Cell<T> self, int align, String tooltip) {
		return tooltipSide(self, align, it -> defaultTooltipTable.get(tooltip, it));
	}

	public static <T extends Element> Cell<T> tooltipSide(Cell<T> self, int align, Cons<Table> builder) {
		self.get().addListener(new SideTooltip(align, builder));
		return self;
	}
}
