package heavyindustry.type;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import heavyindustry.type.pixmap.*;

public interface ImageGenerator {
    default Pixmap generate(Pixmap icon, Func<TextureRegion, Pixmap> pixmapProvider) {
        return icon;
    }

    default Pixmap generate(Pixmap icon, PixmapProcessor processor) {
        return generate(icon, processor::get);
    }
}
