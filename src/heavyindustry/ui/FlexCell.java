package heavyindustry.ui;

import arc.func.Cons;
import arc.scene.Element;
import arc.util.Align;

public class FlexCell<E extends Element> {
	protected E element;
	protected float padLeft, padRight, padTop, padBottom;
	protected float minWidth, maxWidth;
	protected float minHeight, maxHeight;
	protected float expandX, expandY;
	protected float fillX, fillY;
	protected boolean isRow;
	protected int alignChild = Align.center;

	public FlexCell<E> update(Cons<E> cons) {
		element.update(() -> cons.get(element));

		return this;
	}
}
