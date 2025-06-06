package heavyindustry.mod;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.gen.Iconc;

import java.lang.reflect.Field;

/**
 * Text icon mapping.
 *
 * @deprecated deprecated
 */
@Deprecated
public class ContentIcons {
	private final static ObjectMap<ContentType, ObjectMap<MappableContent, Character>> iconMap = new ObjectMap<>();

	static Seq<Field> iconcFields = Seq.with(Iconc.class.getFields());

	public static char icon(MappableContent content) {
		return iconMap.get(content.getContentType(), () -> createFor(content.getContentType())).get(content, ' ');
	}

	private static ObjectMap<MappableContent, Character> createFor(ContentType type) {
		ObjectMap<MappableContent, Character> charMap = new ObjectMap<>();
		Seq<MappableContent> contents = Vars.content.getBy(type).select(c -> c instanceof MappableContent).as();
		for (MappableContent content : contents) {
			addIcon(content, charMap, getPrefix(type));
		}
		return charMap;
	}

	private static void addIcon(MappableContent content, ObjectMap<MappableContent, Character> map, String prefix) {
		String localizedName = Strings.kebabToCamel(prefix + content.name);
		Field field1 = iconcFields.find(field -> field.getName().equals(localizedName));
		try {
			Object o = field1.get(null);
			if (o == null) {
				o = "";
			}
			map.put(content, o.toString().charAt(0));
		} catch (Exception e) {
			Log.err(e);
		}
	}

	private static String getPrefix(ContentType type) {
		return type.name() + "-";
	}
}
