package heavyindustry.graphics.g2d;

import arc.graphics.g2d.TextureRegion;

/**
 * An extension for {@link arc.graphics.g2d.TextureAtlas} that allows deleting the texture regions. Only ever used in icon generation where
 * temporary/pipeline-control sprites need not be in the final atlas to optimize used space.
 * @since 1.0.7
 */
public interface FreeableAtlas {
	void delete(String name);

	void delete(TextureRegion region);
}
