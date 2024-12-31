package heavyindustry.ui.elements;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.*;

import static arc.Core.*;

/**
 * The floodlight effect container is used to filter the elements inside the container, giving them a light effect.
 * The default floodlight parameters come from the game settings.
 *
 * <p>This component conflicts with {@link arc.graphics.g2d.ScissorStack} or {@link arc.graphics.Gl#scissor(int, int, int, int)},
 * This container should not be stored in any parent that will perform cutting, such as {@link arc.scene.ui.ScrollPane}.
 * However, this container supports cutting inside, so it should be covered above the cutting element and the clip of this container should be set to true.
 */
public class BloomGroup extends Group {
    private final Bloom bloom = new Bloom(true);
    public boolean bloomEnabled = settings.getBool("bloom", true);
    public float bloomIntensity = settings.getInt("bloomintensity", 6) / 4f + 1f;
    public int bloomBlur = settings.getInt("bloomblur", 1);
    private boolean clip = false;

    /** @see arc.scene.ui.layout.Table#getClip() */
    public boolean getClip() {
        return clip;
    }

    /** @see arc.scene.ui.layout.Table#setClip(boolean) */
    public void setClip(boolean clip) {
        this.clip = clip;
    }

    @Override
    protected void drawChildren() {
        if (bloomEnabled) {
            bloom.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            bloom.setBloomIntensity(bloomIntensity);
            bloom.blurPasses = bloomBlur;

            bloom.capture();
        }

        if (clip) {
            boolean applied = clipBegin();
            super.drawChildren();
            if (applied) clipEnd();
        } else super.drawChildren();

        if (bloomEnabled) bloom.render();
    }
}
