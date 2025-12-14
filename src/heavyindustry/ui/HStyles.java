package heavyindustry.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.TextField.TextFieldStyle;
import heavyindustry.ui.HoldImageButton.HoldImageButtonStyle;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public final class HStyles {
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

	/** Don't let anyone instantiate this class. */
	private HStyles() {}

	public static void load() {
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
			up = HTex.buttonRight;
			down = HTex.buttonRightDown;
			over = HTex.buttonRightOver;
		}};
		round = new TextButtonStyle(Styles.defaultt) {{
			checked = up;
		}};
		toggleCentert = new TextButtonStyle(Styles.defaultt) {{
			up = HTex.buttonCenter;
			down = HTex.buttonCenterDown;
			over = HTex.buttonCenterOver;
			checked = HTex.buttonCenterOver;
			disabled = HTex.buttonCenterDisabled;
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
			up = HTex.buttonLeft;
			down = HTex.buttonLeftDown;
			over = HTex.buttonLeftOver;
		}};
		toggleLefti = new ImageButtonStyle(lefti) {{
			checked = HTex.buttonLeftOver;
		}};
		righti = new ImageButtonStyle(Styles.defaulti) {{
			up = HTex.buttonRight;
			down = HTex.buttonRightDown;
			over = HTex.buttonRightOver;
		}};
		toggleRighti = new ImageButtonStyle(righti) {{
			checked = HTex.buttonRightOver;
		}};
		centeri = new ImageButtonStyle(Styles.defaulti) {{
			up = HTex.buttonCenter;
			down = HTex.buttonCenterDown;
			over = HTex.buttonCenterOver;
		}};
		tuHoldImageStyle = new HoldImageButtonStyle(tuImageStyle);
		teamChanger = new HoldImageButtonStyle(Styles.clearNoneTogglei) {{
			down = Tex.whiteui;
			checked = Tex.whiteui;
		}};
	}
}
