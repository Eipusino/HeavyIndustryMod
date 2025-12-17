package heavyindustry.graphics;

import arc.func.Prov;
import arc.scene.style.Drawable;

public class LazyDrawable implements Drawable {
	Prov<Drawable> prov;

	Drawable drawable;

	public LazyDrawable(Prov<Drawable> lazy) {
		prov = lazy;
	}

	void check() {
		if (drawable == null) drawable = prov.get();
	}

	@Override
	public void draw(float x, float y, float width, float height) {
		check();
		drawable.draw(x, y, width, height);
	}

	@Override
	public void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
		check();
		drawable.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
	}

	@Override
	public float getLeftWidth() {
		return drawable == null ? 0f : drawable.getLeftWidth();
	}

	@Override
	public void setLeftWidth(float value) {
		if (drawable != null) drawable.setLeftWidth(value);
	}

	@Override
	public float getRightWidth() {
		return drawable == null ? 0f : drawable.getRightWidth();
	}

	@Override
	public void setRightWidth(float value) {
		if (drawable != null) drawable.setRightWidth(value);
	}

	@Override
	public float getTopHeight() {
		return drawable == null ? 0f : drawable.getTopHeight();
	}

	@Override
	public void setTopHeight(float value) {
		if (drawable != null) drawable.setTopHeight(value);
	}

	@Override
	public float getBottomHeight() {
		return drawable == null ? 0f : drawable.getBottomHeight();
	}

	@Override
	public void setBottomHeight(float value) {
		if (drawable != null) drawable.setBottomHeight(value);
	}

	@Override
	public float getMinWidth() {
		return drawable == null ? 0f : drawable.getMinWidth();
	}

	@Override
	public void setMinWidth(float value) {
		if (drawable != null) drawable.setMinWidth(value);
	}

	@Override
	public float getMinHeight() {
		return drawable == null ? 0f : drawable.getMinHeight();
	}

	@Override
	public void setMinHeight(float value) {
		if (drawable != null) drawable.setMinHeight(value);
	}

	@Override
	public float imageSize() {
		return getMinWidth();
	}
}
