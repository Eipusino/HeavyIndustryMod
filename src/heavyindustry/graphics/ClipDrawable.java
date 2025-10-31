package heavyindustry.graphics;

import arc.scene.style.Drawable;

public interface ClipDrawable extends Drawable {
	@Override
	default void draw(float x, float y, float width, float height) {}

	@Override
	default void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {}

	void draw(float x, float y, float width, float height, float clipLeft, float clipRight, float clipTop, float clipBottom);

	void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, float clipLeft, float clipRight, float clipTop, float clipBottom);
}
