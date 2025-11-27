package heavyindustry.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.TextField.TextFieldStyle;
import heavyindustry.ui.HoldImageButton.HoldImageButtonStyle;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import static heavyindustry.ui.HIcon.drawable;

public final class HStyles {
	public static Drawable buttonLeft, buttonLeftDown, buttonLeftOver;
	public static Drawable buttonCenter, buttonCenterDown, buttonCenterOver, buttonCenterDisabled;
	public static Drawable buttonRight, buttonRightOver, buttonRightDown;
	public static Drawable paneBottom;

	public static TextFieldStyle scriptArea;
	public static ButtonStyle right;
	public static TextButtonStyle round, toggleCentert;

	public static ImageButtonStyle modImageStyle;

	public static ImageButtonStyle clearToggle;

	public static ImageButtonStyle tuImageStyle;
	public static ImageButtonStyle togglei;
	public static ImageButtonStyle lefti, toggleLefti;
	public static ImageButtonStyle righti, toggleRighti;
	public static ImageButtonStyle centeri;

	public static HoldImageButtonStyle defaultHoldi, modHoldImageStyle;

	public static HoldImageButtonStyle tuHoldImageStyle;
	public static HoldImageButtonStyle teamChanger;

	/// Don't let anyone instantiate this class.
	private HStyles() {}

	public static void load() {
		//drawable
		buttonLeft = drawable("button-left");
		buttonLeftDown = drawable("button-left-down");
		buttonLeftOver = drawable("button-left-over");
		buttonCenter = drawable("button-center");
		buttonCenterDown = drawable("button-center-down");
		buttonCenterOver = drawable("button-center-over");
		buttonCenterDisabled = drawable("button-center-disabled");
		buttonRight = drawable("button-right");
		buttonRightDown = drawable("button-right-down");
		buttonRightOver = drawable("button-right-over");
		paneBottom = drawable("pane-bottom");
		//style
		modImageStyle = new ImageButtonStyle(Styles.logici) {{
			down = Styles.flatDown;
			over = Styles.flatOver;
			imageDisabledColor = Color.gray;
			imageUpColor = Color.white;
		}};
		defaultHoldi = new HoldImageButtonStyle(Styles.defaulti);
		Core.scene.addStyle(HoldImageButtonStyle.class, defaultHoldi);
		modHoldImageStyle = new HoldImageButtonStyle(modImageStyle);
		//style-2
		clearToggle = new ImageButtonStyle() {{
			down = Styles.flatDown;
			checked = Styles.flatDown;
			up = Styles.black;
			over = Styles.flatOver;
		}};
		//style-3
		scriptArea = new TextFieldStyle() {{
			font = HFonts.inconsoiata;
			fontColor = Color.white;
			selection = Tex.selection;
			cursor = Tex.cursor;
		}};
		right = new ButtonStyle(Styles.defaultb) {{
			up = buttonRight;
			down = buttonRightDown;
			over = buttonRightOver;
		}};
		round = new TextButtonStyle(Styles.defaultt) {{
			checked = up;
		}};
		toggleCentert = new TextButtonStyle(Styles.defaultt) {{
			up = buttonCenter;
			down = buttonCenterDown;
			over = buttonCenterOver;
			checked = buttonCenterOver;
			disabled = buttonCenterDisabled;
		}};
		tuImageStyle = new ImageButtonStyle(Styles.logici) {{
			down = Styles.flatDown;
			over = Styles.flatOver;
			imageDisabledColor = Color.gray;
			imageUpColor = Color.white;
		}};
		togglei = new ImageButtonStyle(Styles.defaulti) {{
			checked = Tex.buttonOver;
		}};
		lefti = new ImageButtonStyle(Styles.defaulti) {{
			up = buttonLeft;
			down = buttonLeftDown;
			over = buttonLeftOver;
		}};
		toggleLefti = new ImageButtonStyle(lefti) {{
			checked = buttonLeftOver;
		}};
		righti = new ImageButtonStyle(Styles.defaulti) {{
			up = buttonRight;
			down = buttonRightDown;
			over = buttonRightOver;
		}};
		toggleRighti = new ImageButtonStyle(righti) {{
			checked = buttonRightOver;
		}};
		centeri = new ImageButtonStyle(Styles.defaulti) {{
			up = buttonCenter;
			down = buttonCenterDown;
			over = buttonCenterOver;
		}};
		tuHoldImageStyle = new HoldImageButtonStyle(tuImageStyle);
		teamChanger = new HoldImageButtonStyle(Styles.clearNoneTogglei) {{
			down = Tex.whiteui;
			checked = Tex.whiteui;
		}};
	}
}
