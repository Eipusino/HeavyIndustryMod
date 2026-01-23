package endfield.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.TextField.TextFieldStyle;
import endfield.ui.HoldImageButton.HoldImageButtonStyle;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Styles2 {
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
	private Styles2() {}

	@Internal
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
			font = Fonts2.inconsoiata;
			fontColor = Color.white;
			selection = Tex.selection;
			cursor = Tex.cursor;
		}};
		right = new ButtonStyle(Styles.defaultb) {{
			up = Tex2.buttonRight;
			down = Tex2.buttonRightDown;
			over = Tex2.buttonRightOver;
		}};
		round = new TextButtonStyle(Styles.defaultt) {{
			checked = up;
		}};
		toggleCentert = new TextButtonStyle(Styles.defaultt) {{
			up = Tex2.buttonCenter;
			down = Tex2.buttonCenterDown;
			over = Tex2.buttonCenterOver;
			checked = Tex2.buttonCenterOver;
			disabled = Tex2.buttonCenterDisabled;
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
			up = Tex2.buttonLeft;
			down = Tex2.buttonLeftDown;
			over = Tex2.buttonLeftOver;
		}};
		toggleLefti = new ImageButtonStyle(lefti) {{
			checked = Tex2.buttonLeftOver;
		}};
		righti = new ImageButtonStyle(Styles.defaulti) {{
			up = Tex2.buttonRight;
			down = Tex2.buttonRightDown;
			over = Tex2.buttonRightOver;
		}};
		toggleRighti = new ImageButtonStyle(righti) {{
			checked = Tex2.buttonRightOver;
		}};
		centeri = new ImageButtonStyle(Styles.defaulti) {{
			up = Tex2.buttonCenter;
			down = Tex2.buttonCenterDown;
			over = Tex2.buttonCenterOver;
		}};
		tuHoldImageStyle = new HoldImageButtonStyle(tuImageStyle);
		teamChanger = new HoldImageButtonStyle(Styles.clearNoneTogglei) {{
			down = Tex.whiteui;
			checked = Tex.whiteui;
		}};
	}
}
