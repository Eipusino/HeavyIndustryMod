package heavyindustry.ui;

import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Scene;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;

public class SideTooltip extends Tooltip {
	private static final Vec2 tmp = new Vec2();

	public final int align;
	public final int invertedAlign;

	public SideTooltip(int ali, Cons<Table> contents) {
		super(contents);
		align = ali;
		invertedAlign = Elements.invertAlign(ali);
	}

	@Override
	protected void setContainerPosition(Element element, float x, float y) {
		setContainerPositionInternal(element);
	}

	protected void setContainerPositionInternal(Element element) {
		targetActor = element;
		Scene stage = element.getScene();
		if (stage == null) return;
		container.pack();
		float dist = manager.edgeDistance;

		float x = Elements.getX(0, element.getWidth(), align);
		float y = Elements.getX(0, element.getHeight(), align);
		Vec2 point = element.localToStageCoordinates(tmp.set(x, y - container.getHeight()));
		if (point.y < dist) point = element.localToStageCoordinates(tmp.set(x, y));
		if (point.x < dist) point.x = dist;
		if (point.x + container.getWidth() > stage.getWidth() - dist)
			point.x = stage.getWidth() - dist - container.getWidth();
		if (point.y + container.getHeight() > stage.getHeight() - dist)
			point.y = stage.getHeight() - dist - container.getHeight();
		container.setPosition(point.x, point.y, invertedAlign);

		point = element.localToStageCoordinates(tmp.set(element.getWidth() / 2f, element.getHeight() / 2f));
		point.sub(container.x, container.y);
		container.setOrigin(point.x, point.y);
	}
}
