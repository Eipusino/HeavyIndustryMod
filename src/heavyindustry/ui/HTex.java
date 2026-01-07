package heavyindustry.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.NinePatch;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.scene.style.Drawable;
import arc.scene.style.ScaledNinePatchDrawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Scl;
import heavyindustry.graphics.Blur;
import heavyindustry.graphics.EdgeLineStripDrawable;
import heavyindustry.graphics.FillStripDrawable;
import heavyindustry.graphics.HRegions;
import heavyindustry.graphics.StripDrawable;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import org.jetbrains.annotations.ApiStatus.Internal;

import static heavyindustry.HVars.MOD_PREFIX;

public final class HTex {
	public static Drawable buttonLeft, buttonLeftDown, buttonLeftOver;
	public static Drawable buttonCenter, buttonCenterDown, buttonCenterOver, buttonCenterDisabled;
	public static Drawable buttonRight, buttonRightOver, buttonRightDown;
	public static Drawable paneBottom;

	public static StripDrawable whiteStrip, whiteEdge;
	public static StripDrawable innerLight, outerLight;

	public static Drawable blurBack;
	public static StripDrawable blurStrip;

	public static StripDrawable none;
	public static StripDrawable black, black9, black8, black7, black6, black5;
	public static StripDrawable boundBlack, boundBlack9, boundBlack8, boundBlack7, boundBlack6, boundBlack5;
	public static StripDrawable grayPanel;
	public static StripDrawable flatOver, edgeFlatOver;
	public static StripDrawable flatDown;
	public static StripDrawable clearEdge;
	public static StripDrawable accent;

	public static Blur uiBlur;

	static int drawingCounter = 0;
	static int lastDialogs = 0;

	private HTex() {}

	@Internal
	public static void load() {
		//drawable
		buttonLeft = getDrawable("button-left");
		buttonLeftDown = getDrawable("button-left-down");
		buttonLeftOver = getDrawable("button-left-over");
		buttonCenter = getDrawable("button-center");
		buttonCenterDown = getDrawable("button-center-down");
		buttonCenterOver = getDrawable("button-center-over");
		buttonCenterDisabled = getDrawable("button-center-disabled");
		buttonRight = getDrawable("button-right");
		buttonRightDown = getDrawable("button-right-down");
		buttonRightOver = getDrawable("button-right-over");
		paneBottom = getDrawable("pane-bottom");

		whiteStrip = new FillStripDrawable(Color.white);
		whiteEdge = new EdgeLineStripDrawable(Scl.scl(3f), Color.white);
		innerLight = new FillStripDrawable(Color.white.cpy().a(0f), Color.white);
		outerLight = new FillStripDrawable(Color.white, Color.white.cpy().a(0f));

		Events.run(Trigger.uiDrawBegin, () -> drawingCounter = 0);
		Events.run(Trigger.uiDrawEnd, () -> lastDialogs = drawingCounter);

		blurBack = new TextureRegionDrawable(HRegions.white) {
			@Override
			public void draw(float x, float y, float width, float height) {
				drawingCounter++;
				if (drawingCounter == lastDialogs) {
					uiBlur.directDraw(() -> {
						super.draw(x, y, width, height);
					});
				}

				Styles.black5.draw(x, y, width, height);
			}

			@Override
			public void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
				drawingCounter++;
				if (drawingCounter == lastDialogs) {
					uiBlur.directDraw(() -> {
						super.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
					});
				}

				Styles.black5.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
			}
		};
		blurStrip = new FillStripDrawable(Color.white) {
			@Override
			public void draw(float originX, float originY, float angle, float distance, float angleDelta, float stripWidth) {
				drawingCounter++;
				if (drawingCounter == lastDialogs) {
					uiBlur.directDraw(() -> {
						super.draw(originX, originY, angle, distance, angleDelta, stripWidth);
					});
				}
			}
		};

		none = new FillStripDrawable(Color.clear);
		black = new FillStripDrawable(Color.black);
		black9 = new FillStripDrawable(Color.black.cpy().a(0.9f));
		black8 = new FillStripDrawable(Color.black.cpy().a(0.8f));
		black7 = new FillStripDrawable(Color.black.cpy().a(0.6f));
		black6 = new FillStripDrawable(Color.black.cpy().a(0.5f));
		black5 = new FillStripDrawable(Color.black.cpy().a(0.3f));
		boundBlack = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black);
		boundBlack9 = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black.cpy().a(0.9f));
		boundBlack8 = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black.cpy().a(0.8f));
		boundBlack7 = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black.cpy().a(0.6f));
		boundBlack6 = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black.cpy().a(0.5f));
		boundBlack5 = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, Color.black.cpy().a(0.3f));
		grayPanel = new FillStripDrawable(Pal.darkestGray);
		flatOver = new FillStripDrawable(new Color(0x454545AA));
		flatDown = new EdgeLineStripDrawable(Scl.scl(3f), Pal.accent);
		edgeFlatOver = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray, new Color(0x454545FF));
		clearEdge = new EdgeLineStripDrawable(Scl.scl(3f), Pal.darkestGray);
		accent = new FillStripDrawable(Pal.accent);
	}

	public static void init() {
		uiBlur = new Blur(Blur.default2);
	}

	public static Drawable getDrawable(String name) {
		return drawable(MOD_PREFIX + name);
	}

	public static Drawable drawable(String name) {
		AtlasRegion region = Core.atlas.find(name);

		if (region.splits != null) {
			int[] splits = region.splits;
			NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
			int[] pads = region.pads;
			if (pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
			return new ScaledNinePatchDrawable(patch);
		} else {
			return new TextureRegionDrawable(region);
		}
	}
}
