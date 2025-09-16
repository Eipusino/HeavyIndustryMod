package heavyindustry.graphics;

public interface StripDrawable {
	default void draw(float originX, float originY, float stripWidth, float angleDelta) {
		draw(originX, originY, 0f, 0f, angleDelta, stripWidth);
	}

	void draw(float originX, float originY, float angle, float distance, float angleDelta, float stripWidth);

	float getLeftOff();

	float getRightOff();

	float getOuterWidth();

	float getInnerWidth();

	float getMinOffset();

	float getMinWidth();

	void setLeftOff(float value);

	void setRightOff(float value);

	void setOuterWidth(float value);

	void setInnerWidth(float value);

	void setMinOffset(float value);

	void setMinWidth(float value);
}
