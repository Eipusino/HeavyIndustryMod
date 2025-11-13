package heavyindustry.ui.markdown.extensions;

import arc.scene.ui.layout.Table;
import arc.util.Align;
import heavyindustry.ui.markdown.DrawRendererContext;
import heavyindustry.ui.markdown.LayoutNodeRenderer;
import heavyindustry.ui.markdown.MDLayoutRenderer;
import heavyindustry.ui.markdown.Markdown;
import heavyindustry.ui.markdown.elemdraw.DrawTable;
import heavyindustry.util.CollectionObjectSet;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.internal.TableBlockParser;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Set;

public class TablesExtension implements Parser.ParserExtension, MDLayoutRenderer.DrawRendererExtension {
	protected TablesExtension() {}

	public static Extension create() {
		return new TablesExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customBlockParserFactory(new TableBlockParser.Factory());
	}

	@Override
	public void extend(MDLayoutRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(TableRenderer::new);
	}

	private static class TableRenderer extends LayoutNodeRenderer {
		private static final Set<Class<? extends Node>> typeSet = CollectionObjectSet.with(
				TableBlock.class,
				TableHead.class,
				TableBody.class,
				TableRow.class,
				TableCell.class
		);

		private Table currentTable;

		public TableRenderer(DrawRendererContext context) {
			super(context);
		}

		@Override
		public Set<Class<? extends Node>> getNodeTypes() {
			return typeSet;
		}

		@Override
		public void render(Node node) {
			if (node instanceof TableBlock block) renderBlock(block);
			else if (node instanceof TableHead head) renderHead(head);
			else if (node instanceof TableBody body) renderBody(body);
			else if (node instanceof TableRow row) renderRow(row);
			else if (node instanceof TableCell cell) renderCell(cell);
		}

		protected void renderBlock(TableBlock node) {
			Table last = currentTable;
			currentTable = new Table();

			visitChildren(node);

			Markdown.MarkdownStyle style = context.element.getStyle();
			context.row();
			context.lineOff += style.linesPadding;
			context.draw(DrawTable.get(context.element, new Table(t -> {
				t.image().color(style.lineColor).width(1.5f).growY();
				t.add(currentTable).fill();
				t.image().color(style.lineColor).width(1.5f).growY();
			}), context.rendOff, -context.lineOff));

			context.updateHeight(currentTable.getPrefHeight());
			context.row();
			context.lineOff += style.linesPadding;

			currentTable = last;
		}

		protected void renderHead(TableHead node) {
			visitChildren(node);
		}

		protected void renderBody(TableBody node) {
			visitChildren(node);
		}

		protected void renderRow(TableRow node) {
			visitChildren(node);
			currentTable.row();
		}

		protected void renderCell(TableCell node) {
			Markdown.MarkdownStyle style = context.element.getStyle();
			currentTable.table(currentTable.getRows() % 2 == 0 ? style.tableBack1 : style.tableBack2, t -> {
				t.image().color(style.lineColor).width(1.5f).growY();
				t.table(ce -> ce.add(new Markdown(context.element, node.getFirstChild())))
						.grow().pad(style.linesPadding * 2).get()
						.align(switch (node.getAlignment()) {
							case LEFT -> Align.left;
							case RIGHT -> Align.right;
							default -> Align.center;
						});
				t.image().color(style.lineColor).width(1.5f).growY();
			}).grow();
		}
	}
}
