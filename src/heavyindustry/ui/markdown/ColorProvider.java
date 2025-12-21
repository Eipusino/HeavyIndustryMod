package heavyindustry.ui.markdown;

import arc.func.Boolf;
import arc.func.Func;
import arc.graphics.Color;
import heavyindustry.ui.markdown.highlighter.Scope;
import heavyindustry.util.CollectionObjectMap;

import java.util.Map;

public class ColorProvider {
	public Color defaultColor = Color.white;

	protected final Map<String, ColorMap> languages = new CollectionObjectMap<>(String.class, ColorMap.class);

	/** Get the color assigned to the scope in the specified language. */
	public Color getColor(String language, Scope scope) {
		ColorMap map = languages.get(language);

		if (map == null) return defaultColor;

		return map.dyeing(scope, defaultColor);
	}

	/**
	 * Create a color chart, add and return it. If the language color chart already exists, it will be
	 * overwritten by a new table.
	 */
	public ColorMap createMap(String language) {
		ColorMap map = new ColorMap();
		languages.put(language.toLowerCase(), map);

		return map;
	}

	/** Retrieve an existing color table, return {@code null} if the color table does not exist. */
	public ColorMap getMap(String language) {
		return languages.get(language.toLowerCase());
	}

	public static class ColorMap {
		protected final Map<Scope, Color> colorMap = new CollectionObjectMap<>(Scope.class, Color.class);
		protected final Map<Boolf<Scope>, Func<Scope, Color>> colorMapProvider = new CollectionObjectMap<>(Boolf.class, Func.class);

		public ColorMap put(Color color, Scope... scopes) {
			for (Scope scope : scopes) {
				colorMap.put(scope, color);
			}
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T extends Scope> ColorMap putProv(Boolf<Scope> filter, Func<T, Color> color) {
			colorMapProvider.put(filter, (Func<Scope, Color>) color);
			return this;
		}

		public Color dyeing(Scope scope, Color defaultColor) {
			for (Map.Entry<Boolf<Scope>, Func<Scope, Color>> entry : colorMapProvider.entrySet()) {
				if (entry.getKey().get(scope)) {
					return entry.getValue().get(scope);
				}
			}

			return colorMap.getOrDefault(scope, defaultColor);
		}
	}
}
