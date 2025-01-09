package heavyindustry.ui.elements.markdown;

import arc.graphics.*;
import heavyindustry.ui.elements.markdown.highlighter.*;

import java.util.*;

public class ColorProvider {
    private final Map<String, ColorMap> languages = new HashMap<>();
    public Color defaultColor = Color.white;

    /** Get the color assigned to the scope in the specified language. */
    public Color getColor(String language, Scopec scope) {
        ColorMap map = languages.get(language);

        if (map == null) return defaultColor;

        return map.colorMap.getOrDefault(scope, defaultColor);
    }

    /** Create a color chart, add and return it. If the language color chart already exists, it will be overwritten by a new table. */
    public ColorMap createMap(String language) {
        ColorMap map = new ColorMap();
        languages.put(language.toLowerCase(), map);

        return map;
    }

    /** Retrieve an existing color table, return null if the color table does not exist. */
    public ColorMap getMap(String language) {
        return languages.get(language.toLowerCase());
    }

    public static class ColorMap {
        private final Map<Scopec, Color> colorMap = new HashMap<>();

        public ColorMap put(Color color, Scopec... scopes) {
            for (Scopec scope : scopes) {
                colorMap.put(scope, color);
            }
            return this;
        }
    }
}
