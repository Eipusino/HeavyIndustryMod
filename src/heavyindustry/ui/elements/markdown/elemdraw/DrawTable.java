package heavyindustry.ui.elements.markdown.elemdraw;

import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.Markdown.*;
import mindustry.ui.*;

public class DrawTable extends DrawObj implements ActivityDrawer {
    Table table;
    ScrollPane pane;

    //use get
    DrawTable() {}

    public static DrawTable get(Markdown owner, Table table, float ox, float oy) {
        DrawTable res = Pools.obtain(DrawTable.class, DrawTable::new);
        res.parent = owner;
        res.table = table;
        res.pane = new ScrollPane(table, Styles.noBarPane);
        res.offsetX = ox;
        res.offsetY = oy;

        return res;
    }

    @Override
    protected void draw() {
    }

    @Override
    public Element getElem() {
        return pane;
    }

    @Override
    public float width() {
        return Math.min(parent.getWidth(), table.getPrefWidth());
    }

    @Override
    public float height() {
        return table.getPrefHeight();
    }

    @Override
    public void reset() {
        super.reset();
        table = null;
        pane = null;
    }
}
