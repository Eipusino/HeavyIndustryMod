package heavyindustry.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.TextField.TextFieldStyle;
import heavyindustry.scene.ui.HoldImageButton.HoldImageButtonStyle;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import static heavyindustry.gen.HIcon.modDrawable;

public final class HStyles {
	public static Drawable
			buttonLeft, buttonLeftDown, buttonLeftOver,
			buttonCenter, buttonCenterDown, buttonCenterOver, buttonCenterDisabled,
			buttonRight, buttonRightOver, buttonRightDown,
			paneBottom;
	public static TextFieldStyle scriptArea;
	public static ButtonStyle right;
	public static TextButtonStyle round, toggleCentert;
	public static ImageButtonStyle
			modImageStyle,

			clearToggle,

			tuImageStyle,
			togglei,
			lefti, toggleLefti,
			righti, toggleRighti,
			centeri;

	public static HoldImageButtonStyle
			defaultHoldi,
			modHoldImageStyle,

			tuHoldImageStyle,
			teamChanger;

	/** Don't let anyone instantiate this class. */
	private HStyles() {}

	public static void init() {
		//drawable
		buttonLeft = modDrawable("button-left");
		buttonLeftDown = modDrawable("button-left-down");
		buttonLeftOver = modDrawable("button-left-over");
		buttonCenter = modDrawable("button-center");
		buttonCenterDown = modDrawable("button-center-down");
		buttonCenterOver = modDrawable("button-center-over");
		buttonCenterDisabled = modDrawable("button-center-disabled");
		buttonRight = modDrawable("button-right");
		buttonRightDown = modDrawable("button-right-down");
		buttonRightOver = modDrawable("button-right-over");
		paneBottom = modDrawable("pane-bottom");
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
