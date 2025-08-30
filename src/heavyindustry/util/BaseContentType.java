package heavyindustry.util;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import heavyindustry.util.handler.EnumHandler;
import heavyindustry.util.handler.FieldHandler;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ContentType processing object, used to create new content types and process the display sorting of types.
 *
 * @since 1.0.8
 */
public class BaseContentType {
	private final static FieldHandler<ContentLoader> fieldHandler = new FieldHandler<>(ContentLoader.class);
	private final static EnumHandler<ContentType> handler = new EnumHandler<>(ContentType.class);

	public static final ObjectMap<ContentType, BaseContentType> allUncContentType = new ObjectMap<>();

	public static ContentType[] displayContentList = ContentType.all;

	static {
		for (ContentType value : ContentType.values()) {
			allUncContentType.put(value, new BaseContentType(value));
		}
	}

	public final ContentType value;
	public final int ordinal;

	public final boolean display;

	/** Internal method for creating identifiers associated with existing {@link ContentType}. */
	private BaseContentType(ContentType type) {
		value = type;
		ordinal = type.ordinal();
		display = true;
	}

	/**
	 * Create a new instance and bind it to a newly generated corresponding {@link ContentType} enumeration
	 * instance.
	 *
	 * @param name         The name of this type
	 * @param contentClass The content class associated with this contentType
	 */
	public BaseContentType(String name, Class<? extends Content> contentClass) {
		this(name, ContentType.values().length, contentClass);
	}

	/**
	 * Create a new instance and bind it to a newly generated corresponding {@link ContentType} enumeration
	 * instance.
	 *
	 * @param name         The name of this type
	 * @param ordinal      The ordinal number of the position displayed for this type in the database
	 * @param contentClass The content class associated with this contentType
	 */
	public BaseContentType(String name, int ordinal, Class<? extends Content> contentClass) {
		this(name, ordinal, contentClass, true);
	}

	/**
	 * Create a new instance and bind it to a newly generated corresponding {@link ContentType} enumeration
	 * instance.
	 *
	 * @param name         The name of this type
	 * @param ord          The ordinal number of the position displayed for this type in the database
	 * @param contentClass The content class associated with this contentType
	 * @param dis          Is it displayed in the database
	 */
	public BaseContentType(String name, int ord, Class<? extends Content> contentClass, boolean dis) {
		value = handler.addEnumItemTail(name, contentClass);
		ordinal = ord;
		display = dis;

		allUncContentType.put(value, this);

		FieldHandler.setValueDefault(ContentType.class, "all", ContentType.values());

		reloadDisplay();

		try {
			ObjectMap<String, MappableContent>[] contentNameMap = fieldHandler.getValue(Vars.content, "contentNameMap");
			Seq<Content>[] contentMap = fieldHandler.getValue(Vars.content, "contentMap");

			ArrayList<ObjectMap<String, MappableContent>> contentNameMapList = new ArrayList<>(Arrays.asList(contentNameMap));
			ArrayList<Seq<Content>> contentMapList = new ArrayList<>(Arrays.asList(contentMap));

			contentNameMapList.add(value.ordinal(), new ObjectMap<>());
			contentMapList.add(value.ordinal(), new Seq<>());

			fieldHandler.setValue(Vars.content, "contentNameMap", contentNameMapList.toArray(new ObjectMap[0]));
			fieldHandler.setValue(Vars.content, "contentMap", contentMapList.toArray(new Seq[0]));
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	protected static void reloadDisplay() {
		Seq<ContentType> list = new Seq<>(ContentType.values().length);

		for (ContentType type : ContentType.values()) {
			BaseContentType t = allUncContentType.get(type);
			if (!t.display) continue;
			list.add(t.value);
		}

		list.sort((a, b) -> a.ordinal() - b.ordinal());

		displayContentList = list.toArray(ContentType.class);
	}
}
