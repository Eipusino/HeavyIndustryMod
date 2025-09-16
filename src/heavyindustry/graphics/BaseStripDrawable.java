package heavyindustry.graphics;

public abstract class BaseStripDrawable implements StripDrawable {
	public float leftOff, rightOff, outerWidth, innerWidth, minOffset, minWidth = 0f;

	@Override
	public float getLeftOff() {
		return leftOff;
	}

	@Override
	public float getRightOff() {
		return rightOff;
	}

	@Override
	public float getOuterWidth() {
		return outerWidth;
	}

	@Override
	public float getInnerWidth() {
		return innerWidth;
	}

	@Override
	public float getMinOffset() {
		return minOffset;
	}

	@Override
	public float getMinWidth() {
		return minWidth;
	}

	@Override
	public void setLeftOff(float value) {
		leftOff = value;
	}

	@Override
	public void setRightOff(float value) {
		rightOff = value;
	}

	@Override
	public void setOuterWidth(float value) {
		outerWidth = value;
	}

	@Override
	public void setInnerWidth(float value) {
		innerWidth = value;
	}

	@Override
	public void setMinOffset(float value) {
		minOffset = value;
	}

	@Override
	public void setMinWidth(float value) {
		minWidth = value;
	}
}
