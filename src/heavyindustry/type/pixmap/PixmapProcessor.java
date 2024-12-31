package heavyindustry.type.pixmap;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.graphics.*;

public interface PixmapProcessor {
    static void drawScaledFit(Pixmap base, Pixmap image) {
        Vec2 size = Scaling.fit.apply(image.width, image.height, base.width, base.height);
        int wx = (int) size.x, wy = (int) size.y;
        // TODO bad linear scaling
        base.draw(image, 0, 0, image.width, image.height, base.width / 2 - wx / 2, base.height / 2 - wy / 2, wx, wy, true, true);
    }

    static void drawCenter(Pixmap pix, Pixmap other) {
        pix.draw(other, pix.width / 2 - other.width / 2, pix.height / 2 - other.height / 2, true);
    }

    static Pixmap clearAlpha(Pixmap image) {
        int x = 0, y = 0, topx = image.width, topy = image.height;
        //check x-
        for (int dx = 0; dx < image.width; dx++) {
            for (int dy = 0; dy < image.height; dy++) {
                if (image.getA(dx, dy) != 0) {
                    dx = topx;
                    break;
                }
                x = dx;
            }
        }
        //check y-
        for (int dy = 0; dy < image.height; dy++) {
            for (int dx = 0; dx < image.width; dx++) {
                if (image.getA(dx, dy) != 0) {
                    dy = topy;
                    break;
                }
                y = dy;
            }
        }
        //check x+
        for (int dx = image.width - 1; dx > -1; dx--) {
            for (int dy = image.height - 1; dy > -1; dy--) {
                if (image.getA(dx, dy) != 0) {
                    dx = -1;
                    break;
                }
                topx = dx;

            }
        }
        //check y+
        for (int dy = image.height - 1; dy > -1; dy--) {
            for (int dx = image.width - 1; dx > -1; dx--) {
                if (image.getA(dx, dy) != 0) {
                    dy = -1;
                    break;
                }
                topy = dy + 1;

            }
        }
        if (x != 0 || y != 0 || topx != image.width || topy != image.height) {
            int width = Math.min(x, image.width - topx);
            int height = Math.min(y, image.height - topy);
            Pixmap pixmap = new Pixmap(image.width - width * 2, image.height - height * 2);
            //pixmap.draw(image, 0, 0, x, y, topx, topy);
            drawCenter(pixmap, image);
            return pixmap;
        }
        return image;
    }

    static Pixmap drawScaleAt(Pixmap image, Pixmap other, int destx, int desty) {
        int widthScale = 0, heightScale = 0;
        if (destx > image.width) {
            widthScale = destx - image.width + other.width;
        } else if (destx + other.width < 0) {
            widthScale = -(destx);
        } else if (destx + other.width > image.width || destx < 0) {
            int dif = destx + other.width - image.width;
            int dx;
            for (int y = 0; y < other.height; y++) {
                for (dx = 0; dx < dif; dx++) {
                    if (other.getA(other.width - dx - 1, y) == 0) continue;
                    widthScale = Math.max(widthScale, dx);
                }
                for (dx = 0; dx < -destx; dx++) {
                    if (other.getA(dx, y) == 0) continue;
                    widthScale = Math.max(widthScale, dx);
                }
            }
        }

        if (image.height < desty) {
            heightScale = desty - image.height + other.height;
        } else if (desty + other.height < 0) {
            heightScale = -(desty + other.height);
        } else if (desty + other.height > image.height || desty < 0) {
            int dif = desty + other.height - image.height;
            int dy;
            for (int x = 0; x < other.width; x++) {
                for (dy = 0; dy < dif; dy++) {
                    if (other.getA(x, other.height - dy - 1) == 0) continue;
                    heightScale = Math.max(heightScale, dy);
                }
                for (dy = 0; dy < -destx; dy++) {
                    if (other.getA(x, dy) == 0) continue;
                    heightScale = Math.max(heightScale, dy);
                }
            }
        }
        if (widthScale != 0 || heightScale != 0) {
            Pixmap pixmap;

            try {
                pixmap = new Pixmap(widthScale * 2 + image.width, image.height + heightScale * 2);
            } catch (ArcRuntimeException arcRuntimeException) {
                Log.err(arcRuntimeException);
                return image;
            }
            drawCenter(pixmap, image);
            pixmap.draw(other, destx + widthScale, desty + heightScale, true);
            return pixmap;
        }
        image.draw(other,
                destx,
                desty,
                true
        );
        return image;
    }

    static Pixmap outline(Pixmap i) {
        int upScale = 0;
        int x = 0, y = 0;
        for (x = 0; x < i.width; x++) {
            for (y = 0; y < 3; y++) {
                boolean bool = i.getA(x, y) == 0 && i.getA(x, i.height - y - 1) == 0;
                if (!bool) {
                    upScale = Math.max(y, upScale);
                }
            }
        }
        for (y = 0; y < i.height; y++) {
            for (x = 0; x < 3; x++) {
                boolean bool = i.getA(x, y) == 0 && i.getA(i.width - x - 1, y) == 0;
                if (!bool) {
                    upScale = Math.max(x, upScale);
                }
            }
        }
        if (upScale != 0) {
            Pixmap pixmap = new Pixmap(i.width + upScale * 2, i.height + upScale * 2);
            pixmap.draw(i, pixmap.width / 2 - i.width / 2, pixmap.height / 2 - i.height / 2);
            i = pixmap;
        }
        return i.outline(Pal.darkerMetal, 3);
    }

    static Pixmap rotatePixmap(Pixmap pixmap, int steps) {
        Pixmap copy = pixmap.copy();
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        Vec2 center = Tmp.v2.set(width - 1, height - 1).scl(0.5f);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tmp.v1.set(x, y).rotateAround(center, steps * 90);
                //pixmap.set(x,y,copy.get(Tmp.p3.x,Tmp.p3.y));
                int nx = (int) Tmp.v1.x, ny = (int) Tmp.v1.y;
                copy.getA(nx, ny);
                pixmap.set(x, y, copy.get(nx, ny));
                //pixmap.set(nx,ny,copy.get(x,y));
            }
        }
        copy.dispose();
        return pixmap;
    }

    void save(Pixmap pixmap, String path);

    Pixmap get(String name);

    boolean has(String name);

    Pixmap get(TextureRegion region);

    void replace(String name, Pixmap image);

    void replace(TextureRegion name, Pixmap image);

    boolean replaceAbsolute(TextureRegion name, Pixmap image);

    void delete(String name);

    default void saveScaled(Pixmap pix, String name, int size) {
        Pixmap scaled = new Pixmap(size, size);
        // TODO bad linear scaling
        scaled.draw(pix, 0, 0, pix.width, pix.height, 0, 0, size, size, true, true);
        save(scaled, name);
    }
}
