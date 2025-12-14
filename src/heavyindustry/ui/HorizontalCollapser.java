package heavyindustry.ui;

import arc.func.Boolp;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.scene.Action;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.util.ArcRuntimeException;
import org.jetbrains.annotations.Nullable;

public class HorizontalCollapser extends WidgetGroup {
	protected Table table;
	protected @Nullable Boolp collapsedFunc;

	protected CollapseAction collapseAction = new CollapseAction();

	protected boolean collapsed, autoAnimate;
	protected boolean actionRunning;
	protected float currentWidth;
	protected float seconds = 0.4f;

	public HorizontalCollapser(Cons<Table> cons, boolean collapsed) {
		this(new Table(), collapsed);
		cons.get(table);
	}

	public HorizontalCollapser(Table tab, boolean col) {
		table = tab;
		collapsed = col;
		setTransform(true);

		updateTouchable();
		addChild(tab);
	}

	public HorizontalCollapser setDuration(float sec) {
		seconds = sec;
		return this;
	}

	public HorizontalCollapser setCollapsed(boolean aut, Boolp col) {
		collapsedFunc = col;
		autoAnimate = aut;
		return this;
	}

	public void toggle() {
		setCollapsed(!isCollapsed());
	}

	public void toggle(boolean animated) {
		setCollapsed(!isCollapsed(), animated);
	}

	public void setCollapsed(boolean collapse, boolean withAnimation) {
		collapsed = collapse;
		updateTouchable();

		if (table == null) return;

		actionRunning = true;

		if (withAnimation) {
			addAction(collapseAction);
		} else {
			if (collapse) {
				currentWidth = 0;
				collapsed = true;
			} else {
				currentWidth = table.getPrefWidth();
				collapsed = false;
			}

			actionRunning = false;
			invalidateHierarchy();
		}
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public HorizontalCollapser setCollapsed(Boolp collapsed) {
		collapsedFunc = collapsed;
		return this;
	}

	public void setCollapsed(boolean collapse) {
		setCollapsed(collapse, true);
	}

	protected void updateTouchable() {
		touchable = collapsed ? Touchable.disabled : Touchable.enabled;
	}

	@Override
	public void draw() {
		if (currentWidth > 1) {
			Draw.flush();
			if (clipBegin(x, y, currentWidth, getHeight())) {
				super.draw();
				Draw.flush();
				clipEnd();
			}
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (collapsedFunc != null) {
			boolean col = collapsedFunc.get();
			if (col != collapsed) {
				setCollapsed(col, autoAnimate);
			}
		}
	}

	@Override
	public void layout() {
		if (table == null) return;

		table.setBounds(0, 0, getWidth(), getHeight());

		if (!actionRunning) {
			if (collapsed)
				currentWidth = 0;
			else
				currentWidth = table.getPrefWidth();
		}
	}

	@Override
	public float getPrefHeight() {
		return table == null ? 0 : table.getPrefHeight();
	}

	@Override
	public float getPrefWidth() {
		if (table == null) return 0;

		if (!actionRunning) {
			if (collapsed)
				return 0;
			else
				return table.getPrefWidth();
		}

		return currentWidth;
	}

	public void setTable(Table tab) {
		table = tab;
		clearChildren();
		addChild(tab);
	}

	@Override
	public float getMinWidth() {
		return 0;
	}

	@Override
	public float getMinHeight() {
		return 0;
	}

	@Override
	protected void childrenChanged() {
		super.childrenChanged();
		if (getChildren().size > 1) throw new ArcRuntimeException("Only one actor can be added to CollapsibleWidget");
	}

	protected class CollapseAction extends Action {
		protected CollapseAction() {}

		@Override
		public boolean act(float delta) {
			if (collapsed) {
				currentWidth -= delta * table.getPrefWidth() / seconds;
				if (currentWidth <= 0) {
					currentWidth = 0;
					actionRunning = false;
				}
			} else {
				currentWidth += delta * table.getPrefWidth() / seconds;
				if (currentWidth > table.getPrefWidth()) {
					currentWidth = table.getPrefWidth();
					actionRunning = false;
				}
			}

			invalidateHierarchy();
			return !actionRunning;
		}
	}
}
