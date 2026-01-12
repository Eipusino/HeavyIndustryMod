package heavyindustry.graphics.g2d;

import mindustry.graphics.MultiPacker.PageType;

/**
 * An extension for {@link mindustry.graphics.MultiPacker} that allows deleting the pixmaps. Only ever used in icon generation where
 * temporary/pipeline-control sprites need not be in the final atlas to optimize used space.
 * @since 1.0.7
 */
public interface FreeablePacker extends Freeable {
	void delete(PageType type, String name);
}
