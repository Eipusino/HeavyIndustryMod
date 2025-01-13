package heavyindustry.ui.elements.markdown.elemdraw;

import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.Markdown.*;

public class DrawCurtain extends DrawObj implements ActivityDrawer {
    float width, height;
    Drawable drawable;

    Image image;

    //use get
    DrawCurtain() {}

    public static DrawCurtain get(Markdown owner, Drawable drawable, float offX, float offY, float width, float height) {
        DrawCurtain res = Pools.obtain(DrawCurtain.class, DrawCurtain::new);
        res.parent = owner;
        res.drawable = drawable;
        res.offsetX = offX;
        res.offsetY = offY;
        res.width = width;
        res.height = height;

        res.image = new Image(drawable) {
            boolean alp = false;

            {
                setSize(width, height);

                clicked(() -> {
                    alp = !alp;
                    clearActions();
                    actions(Actions.alpha(alp ? 0.2f : 1, 0.3f));
                });

                hovered(() -> {
                    if (alp) return;

                    clearActions();
                    actions(Actions.alpha(0.2f, 0.3f));
                });

                exited(() -> {
                    if (alp) return;

                    clearActions();
                    actions(Actions.alpha(1f, 0.3f));
                });
            }
        };

        return res;
    }

    @Override
    protected void draw() {
    }

    @Override
    public Element getElem() {
        return image;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }
}
