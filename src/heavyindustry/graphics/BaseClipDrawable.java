package heavyindustry.graphics;

public abstract class BaseClipDrawable implements ClipDrawable {
	public String name;
	public float leftWidth, rightWidth, topHeight, bottomHeight, minWidth, minHeight;

	@Override
	public float getLeftWidth() {
		return leftWidth;
	}

	@Override
	public float getRightWidth() {
		return rightWidth;
	}

	@Override
	public float getTopHeight() {
		return topHeight;
	}

	@Override
	public float getBottomHeight() {
		return bottomHeight;
	}

	@Override
	public float getMinWidth() {
		return minWidth;
	}

	@Override
	public float getMinHeight() {
		return minHeight;
	}

	@Override
	public void setLeftWidth(float value) {
		leftWidth = value;
	}

	@Override
	public void setRightWidth(float value) {
		rightWidth = value;
	}

	@Override
	public void setTopHeight(float value) {
		topHeight = value;
	}

	@Override
	public void setBottomHeight(float value) {
		bottomHeight = value;
	}

	@Override
	public void setMinWidth(float value) {
		minWidth = value;
	}

	@Override
	public void setMinHeight(float value) {
		minHeight = value;
	}
}
