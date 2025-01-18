package heavyindustry.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.pooling.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class MultiBar extends Bar {
    private static final Rect scissor = new Rect();
    Seq<BarPart> barParts;

    private String name = "";

    public MultiBar(String bun, Seq<BarPart> bar) {
        barParts = bar;
        name = Core.bundle.get(bun, bun);
        update(this::updateParts);
    }

    public MultiBar(Prov<String> prov, Seq<BarPart> bar) {
        barParts = bar;
        update(() -> {
            updateParts();
            name = prov.get();
        });
    }

    public static float normalize(float f) {
        if (Float.isNaN(f)) {
            return 0f;
        }

        if (Float.isInfinite(f)) {
            return 1f;
        }
        return f;
    }

    public void reset(float value) {
        float v = value / barParts.size;
        barParts.each((part) -> {
            part.value = part.lastValue = part.blink = v;
        });
    }

    public void updateParts() {
        barParts.each(BarPart::update);
    }

    public void drawParts() {
        float[] offset = new float[]{x};

        barParts.each((part) -> {
            part.draw(offset[0], y, width, height, barParts);
            offset[0] = part.offset;
        });
    }

    public void set(Prov<String> prov, Seq<BarPart> bar) {
        barParts = bar;
        update(() -> {
            name = prov.get();
            updateParts();
        });
    }

    @Override
    public void draw() {
        if (barParts != null && barParts.size > 0) {
            Drawable bar = Tex.bar;
            Draw.colorl(0.1F);
            bar.draw(x, y, width, height);

            drawParts();

            Draw.color();
            Font font = Fonts.outline;
            GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
            lay.setText(font, name);
            font.setColor(Color.white);
            font.draw(name, x + width / 2.0F - lay.width / 2.0F, y + height / 2.0F + lay.height / 2.0F + 1.0F);
            Pools.free(lay);
        }
    }

    public static class BarPart {
        public float lastValue;
        public float blink = 0;
        public float value;
        public Color color;
        public Color blinkColor = new Color();
        public Floatp fraction;

        Runnable runnable = () -> {};
        float x, y, width, height, offset;

        public BarPart(Color col, Floatp floatp) {
            fraction = floatp;
            blinkColor.set(col);
            lastValue = value = floatp.get();
            color = col;
        }

        public BarPart(Prov<Color> col, Floatp floatp) {
            fraction = floatp;

            lastValue = value = Mathf.clamp(floatp.get());

            update(() -> {
                blinkColor.set(col.get());
                color = col.get();
            });
        }

        public void update(Runnable run) {
            runnable = run;
        }

        public void update() {
            runnable.run();
        }

        public void draw(float a, float b, float w, float h, Seq<BarPart> bar) {
            if (fraction != null) {
                x = a;
                y = b;
                width = w;
                height = h;

                float computed = Mathf.clamp(fraction.get());

                if (lastValue > computed) {
                    blink = 1f;
                    lastValue = computed;
                }

                lastValue = normalize(lastValue);
                value = normalize(value);

                computed = normalize(computed);

                blink = Mathf.lerpDelta(blink, 0f, 0.2f);
                value = Mathf.lerpDelta(value, computed, 0.15f);

                Draw.color(color, blinkColor, blink);
                Drawable top = Tex.barTop;

                float topWidth = width * value;
                topWidth /= bar.size;

                if (topWidth > Core.atlas.find("bar-top").width) {
                    top.draw(x, y, topWidth, height);

                    offset = x + topWidth;
                } else if (ScissorStack.push(scissor.set(x, y, topWidth, height))) {
                    top.draw(x, y, Core.atlas.find("bar-top").width, height);

                    offset = x + topWidth;
                    ScissorStack.pop();
                } else {
                    offset = x;
                }
            }
        }
    }
}
