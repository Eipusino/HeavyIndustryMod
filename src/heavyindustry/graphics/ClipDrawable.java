package heavyindustry.graphics;

public interface ClipDrawable {
	void draw(float x, float y, float width, float height, float clipLeft, float clipRight, float clipTop, float clipBottom);

	void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, float clipLeft, float clipRight, float clipTop, float clipBottom);

	float getLeftWidth();

	float getRightWidth();

	float getTopHeight();

	float getBottomHeight();

	float getMinWidth();

	float getMinHeight();

	void setLeftWidth(float value);

	void stRightWidth(float value);

	void setTopHeight(float value);

	void setBottomHeight(float value);

	void setMinWidth(float value);

	void setMinHeight(float value);
}
