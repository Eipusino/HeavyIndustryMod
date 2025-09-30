package heavyindustry.entities.shift;

import arc.math.Interp;
import arc.math.Mathf;

public class AoEShift extends ShiftHandler {
	public AoEShift(String name) {
		super(name, 2);

		critPoints = new float[]{
				80f, 20f,
				-80f, 20f
		};

		panels.addAll(new ShiftPanel() {{
			mirror = false;

			y1 = -50f;

			y2 = -82.5f;
			moveCurve = Interp.pow2Out;

			//rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			heightTo = 0.3375f;
			widthTo = 0.4921875f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0.5f, 1f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				y2 = y1 - 15f;

				heightFrom = heightTo = 0.3375f;
				widthFrom = widthTo = 0.4921875f;

				//rotationTo = rotationFrom + 90f;
				//rotCurve = pow3In;

				vTo = 0.5f;

				setStartEnd(0f, 0.55f);
			}};
		}}, new ShiftPanel() {{
			mirror = false;

			y1 = -40f;

			y2 = -72.5f;
			moveCurve = Interp.pow2Out;

			//rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			heightTo = 0.45f;
			widthTo = 0.65625f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0.4f, 0.9f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				y2 = y1 - 15f;

				heightFrom = heightTo = 0.45f;
				widthFrom = widthTo = 0.65625f;

				//rotationTo = rotationFrom + 90f;
				//rotCurve = pow3In;

				vTo = 0.5f;

				setStartEnd(0.1f, 0.65f);
			}};
		}}, new ShiftPanel() {{
			mirror = false;

			y1 = -20f;

			y2 = -60f;
			moveCurve = Interp.pow2Out;

			//rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			heightTo = 0.6f;
			widthTo = 0.875f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> Interp.pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0.3f, 0.8f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				y2 = y1 - 15f;

				heightFrom = heightTo = 0.6f;
				widthFrom = widthTo = 0.875f;

				//rotationTo = rotationFrom + 90f;
				//rotCurve = pow3In;

				vTo = 0.5f;

				setStartEnd(0.2f, 0.75f);
			}};
		}}, new ShiftPanel() {{
			x1 = 50f;
			y1 = -50f;

			x2 = 60f;
			y2 = -60f;
			moveCurve = Interp.pow2Out;

			rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			heightTo = 0.5625f;
			widthTo = 0.5625f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> Interp.pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0.2f, 0.7f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				x2 = x1 + 15f;
				y2 = y1 - 15f;

				heightFrom = heightTo = 0.5625f;
				widthFrom = widthTo = 0.5625f;

				rotationTo = rotationFrom - 90f;
				rotCurve = Interp.pow3In;

				vTo = 0.5f;

				setStartEnd(0.3f, 0.8f);
			}};
		}}, new ShiftPanel() {{
			x1 = 40f;
			y1 = -40f;

			x2 = 50f;
			y2 = -50f;
			moveCurve = Interp.pow2Out;

			rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			heightTo = 0.75f;
			widthTo = 0.75f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> Interp.pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0.1f, 0.6f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				x2 = x1 + 15f;
				y2 = y1 - 15f;

				heightFrom = heightTo = 0.75f;
				widthFrom = widthTo = 0.75f;

				rotationTo = rotationFrom - 90f;
				rotCurve = Interp.pow3In;

				vTo = 0.5f;

				setStartEnd(0.4f, 0.9f);
			}};
		}}, new ShiftPanel() {{
			x1 = 20f;
			y1 = -20f;

			x2 = 40f;
			y2 = -40f;
			moveCurve = Interp.pow2Out;

			rotationFrom = rotationTo = 45f;

			heightCurve = Interp.pow2Out;
			widthCurve = Interp.pow2Out;
			//sclEnd = 0.5f;
			vFrom = 0.5f;

			vCurve = Interp.pow2Out;
			//widthCurve = a -> Interp.pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
			setStartEnd(0, 0.5f);
			sclEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				x2 = x1 + 15f;
				y2 = y1 - 15f;

				heightFrom = heightTo = 1f;
				widthFrom = widthTo = 1f;

				rotationTo = rotationFrom - 90f;
				rotCurve = Interp.pow3In;

				vTo = 0.5f;

				setStartEnd(0.5f, 1f);
			}};
		}}, new ShiftPanel() {{
			idx = 1;
			renderFinish = false;
			//renderOut = false;

			x1 = -15f;

			x2 = -25f;
			y2 = 40f;
			vFrom = 0.5f;

			//heightCurve = a -> Interp.pow2Out.apply(slope.apply(a * a));
			//widthCurve = Interp.pow4Out;
			vCurve = a -> Interp.pow2Out.apply(Interp.slope.apply(a * a));

			//widthFrom = heightFrom = 0.6f;
			widthFrom = 1f;
			//heightFrom = heightTo = -0.6f;
			heightFrom = -0.6f;
			heightTo = 0.6f;
			widthTo = 0.6f;

			heightCurve = Interp.pow3;

			setStartEnd(0.2f, 0.6f);

			out = new ShiftPanel(this) {{
				x1 = -25f;
				y1 = 60f;

				x2 = -25f;
				y2 = 95f;

				widthFrom = heightFrom = 0f;
				widthTo = heightTo = 0.75f;
				//widthCurve = heightCurve = Interp.pow4Out;
				widthCurve = a -> Interp.pow2Out.apply(Interp.slope.apply(a));
				heightCurve = a -> Interp.pow2Out.apply(Mathf.curve(a, 0f, 0.5f));
				vFrom = 0f;
				vTo = 0f;

				//vFrom = 0.5f;
				//vCurve = a -> Interp.pow2Out.apply(Interp.slope.apply(a * a));

				setStartEnd(0.15f, 0.65f);
			}};
		}}, new ShiftPanel() {{
			idx = 1;

			x1 = -25f;

			x2 = -25f;
			y2 = 60f;

			moveCurve = Interp.pow2Out;
			heightCurve = Interp.pow2Out;

			widthFrom = 0.9f;

			vFrom = 0.5f;
			vCurve = Interp.pow2Out;

			setStartEnd(0.5f, 1f);

			sclEnd -= 0.25f;
			moveEnd -= 0.25f;

			out = new ShiftPanel(this) {{
				x2 = -25f;
				y2 = 95f;
				moveCurve = Interp.pow2Out;

				//heightTo = 0f;
				vTo = 0.5f;
				vCurve = Interp.pow2Out;

				widthCurve = heightCurve = Interp.pow4In;

				setStartEnd(0f, 0.5f);
			}};
		}});
	}
}
