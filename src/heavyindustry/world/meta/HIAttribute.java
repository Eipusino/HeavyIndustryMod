package heavyindustry.world.meta;

import mindustry.world.meta.Attribute;

import static mindustry.world.meta.Attribute.add;

public final class HIAttribute {
	/** Arkycite content. Used for arkycite extractor yield. */
	public static final Attribute
			arkycite = add("arkycite");

	/** Don't let anyone instantiate this class. */
	private HIAttribute() {}
}
