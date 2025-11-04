package heavyindustry.ui;

import arc.Core;
import arc.func.Floatp;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Scl;
import arc.util.pooling.Pools;
import heavyindustry.math.Mathm;
import mindustry.gen.Tex;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;

public class CenterBar extends Bar {
	protected Floatp fraction;
	protected CharSequence name = "";
	protected float value, lastValue, blink, outlineRadius;
	protected Color blinkColor = new Color(), outlineColor = new Color();

	public CenterBar(String get, Color color, Floatp frac) {
		fraction = frac;
		name = Core.bundle.get(get, get);
		blinkColor.set(color);
		lastValue = value = frac.get();
		setColor(color);
	}

	public CenterBar(Prov<CharSequence> get, Prov<Color> color, Floatp frac) {
		fraction = frac;
		lastValue = value = Mathm.clamp(frac.get());
		update(() -> {
			name = get.get();
			blinkColor.set(color.get());
			setColor(color.get());
		});
	}

	public CenterBar() {}

	@Override
	public void reset(float v) {
		value = lastValue = blink = v;
	}

	@Override
	public void set(Prov<String> get, Floatp frac, Color color) {
		fraction = frac;
		lastValue = frac.get();
		blinkColor.set(color);
		setColor(color);
		update(() -> name = get.get());
	}

	@Override
	public void snap() {
		lastValue = value = fraction.get();
	}

	@Override
	public Bar outline(Color color, float stroke) {
		outlineColor.set(color);
		outlineRadius = Scl.scl(stroke);
		return this;
	}

	@Override
	public void flash() {
		blink = 1f;
	}

	@Override
	public Bar blink(Color color) {
		blinkColor.set(color);
		return this;
	}

	@Override
	public void draw() {
		if (fraction == null) return;

		float computed = Mathm.clamp(fraction.get(), -1f, 1f);

		if (lastValue > computed) {
			blink = 1f;
			lastValue = computed;
		}

		if (Float.isNaN(lastValue)) lastValue = 0;
		if (Float.isInfinite(lastValue)) lastValue = 1f;
		if (Float.isNaN(value)) value = 0;
		if (Float.isInfinite(value)) value = 1f;
		if (Float.isNaN(computed)) computed = 0;
		if (Float.isInfinite(computed)) computed = 1f;

		blink = Mathf.lerpDelta(blink, 0f, 0.2f);
		value = Mathf.lerpDelta(value, computed, 0.15f);

		Drawable bar = Tex.bar;

		if (outlineRadius > 0) {
			Draw.color(outlineColor);
			bar.draw(x - outlineRadius, y - outlineRadius, width + outlineRadius * 2, height + outlineRadius * 2);
		}

		Draw.colorl(0.1f);
		Draw.alpha(parentAlpha);
		bar.draw(x, y, width, height);
		Draw.color(color, blinkColor, blink);
		Draw.alpha(parentAlpha);

		Drawable top = Tex.barTop;

		top.draw(
				x + (width / 2f - Core.atlas.find("bar-top").width / 2f) * (Math.min(value, 0f) + 1f), y,
				Core.atlas.find("bar-top").width + (-Core.atlas.find("bar-top").width / 2f + width / 2f) * Math.abs(value), height
		);

		Draw.color();

		Font font = Fonts.outline;
		GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
		lay.setText(font, name);

		font.setColor(1f, 1f, 1f, 1f);
		font.getCache().clear();
		font.getCache().addText(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1);
		font.getCache().draw(parentAlpha);

		Pools.free(lay);
	}
}
