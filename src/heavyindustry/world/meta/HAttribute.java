package heavyindustry.world.meta;

import mindustry.world.meta.Attribute;

public final class HAttribute {
	/// Arkycite content. Used for arkycite extractor yield.
	public static final Attribute arkycite = Attribute.add("arkycite");
	public static final Attribute radioactivity = Attribute.add("radioactivity");

	/// Don't let anyone instantiate this class.
	private HAttribute() {}
}
