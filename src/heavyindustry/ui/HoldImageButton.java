package heavyindustry.ui;

import arc.Core;
import arc.func.Boolp;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.Element;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.util.Time;
import heavyindustry.HVars;

public class HoldImageButton extends ImageButton {
	public Boolp canHold = () -> true;

	protected Runnable held = () -> {};
	protected boolean heldAct;
	protected HoldImageButtonStyle style;
	protected boolean repeat = false;
	protected boolean hasReset;

	protected Drawable localDrawable = null;
	protected Color localColor = Color.white;

	public HoldImageButton() {
		this(Core.scene.getStyle(HoldImageButtonStyle.class));
	}

	public HoldImageButton(Drawable icon, HoldImageButtonStyle stylen) {
		this(stylen);
		HoldImageButtonStyle style = new HoldImageButtonStyle(stylen);
		style.imageUp = icon;
		setStyle(style);
	}

	public HoldImageButton(TextureRegion region) {
		this(Core.scene.getStyle(HoldImageButtonStyle.class));
		HoldImageButtonStyle style = new HoldImageButtonStyle(Core.scene.getStyle(HoldImageButtonStyle.class));
		style.imageUp = new TextureRegionDrawable(region);
		setStyle(style);
	}

	public HoldImageButton(TextureRegion region, HoldImageButtonStyle stylen) {
		this(stylen);
		HoldImageButtonStyle style = new HoldImageButtonStyle(stylen);
		style.imageUp = new TextureRegionDrawable(region);
		setStyle(style);
	}

	public HoldImageButton(HoldImageButtonStyle style) {
		super(style);
		setStyle(style);
		setSize(getPrefWidth(), getPrefHeight());
	}

	public HoldImageButton(Drawable imageUp) {
		this(new HoldImageButtonStyle(null, null, null, imageUp, null, null, null));
		HoldImageButtonStyle style = new HoldImageButtonStyle(Core.scene.getStyle(HoldImageButtonStyle.class));
		style.imageUp = imageUp;
		setStyle(style);
	}

	public HoldImageButton(Drawable imageUp, Drawable imageDown) {
		this(new HoldImageButtonStyle(null, null, null, imageUp, imageDown, null, null));
	}

	public HoldImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
		this(new HoldImageButtonStyle(null, null, null, imageUp, imageDown, imageChecked, null));
	}

	public HoldImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked, Drawable imageHeld) {
		this(new HoldImageButtonStyle(null, null, null, imageUp, imageDown, imageChecked, imageHeld));
	}

	public HoldImageButtonStyle getStyle() {
		return style;
	}

	public void setStyle(ButtonStyle style) {
		if (!(style instanceof HoldImageButtonStyle s)) {
			throw new IllegalArgumentException("style must be a HoldImageButtonStyle.");
		} else {
			super.setStyle(style);
			this.style = s;
			if (getImage() != null) {
				updateImage();
			}
		}
	}

	public Element held(Runnable r) {
		held = r;
		return this;
	}

	public Element canHold(Boolp canHold) {
		this.canHold = canHold;
		return this;
	}

	public ClickListener clicked(Cons<ClickListener> tweaker, final Cons<ClickListener> runner) {
		ClickListener click;
		addListener(click = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (runner != null && !isDisabled() && !heldAct) {
					runner.get(this);
				}
			}
		});
		tweaker.get(click);
		addReset(); //Make sure click happens before reset
		return click;
	}

	@Override
	protected void updateImage() {
		if (isDisabled() && style.imageDisabled != null) {
			localDrawable = style.imageDisabled;
		} else if (isHeld() && style.imageHeld != null) {
			localDrawable = style.imageHeld;
		} else if (isPressed() && style.imageDown != null) {
			localDrawable = style.imageDown;
		} else if (isChecked() && style.imageChecked != null) {
			localDrawable = style.imageCheckedOver != null && isOver() ? style.imageCheckedOver : style.imageChecked;
		} else if (isOver() && style.imageOver != null) {
			localDrawable = style.imageOver;
		} else if (style.imageUp != null) {
			localDrawable = style.imageUp;
		}

		localColor = getImage().color;
		if (isDisabled() && style.imageDisabledColor != null) {
			localColor = style.imageDisabledColor;
		} else if (isHeld() && style.imageHeldColor != null) {
			localColor = style.imageHeldColor;
		} else if (isPressed() && style.imageDownColor != null) {
			localColor = style.imageDownColor;
		} else if (isChecked() && style.imageCheckedColor != null) {
			localColor = style.imageCheckedColor;
		} else if (isOver() && style.imageOverColor != null) {
			localColor = style.imageOverColor;
		} else if (style.imageUpColor != null) {
			localColor = style.imageUpColor;
		}

		Image image = getImage();
		image.setDrawable(localDrawable);
		image.setColor(localColor);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (isPressed() && !isDisabled() && canHold.get()) {
			HVars.pressTimer += Time.delta;
			if (HVars.pressTimer > HVars.longPress && (repeat || !heldAct)) {
				heldAct = true;
				held.run();
			}
		}
	}

	public boolean isHeld() {
		return isPressed() && HVars.pressTimer > HVars.longPress;
	}

	public void addReset() {
		if (hasReset) return;

		released(() -> {
			heldAct = false;
			HVars.pressTimer = 0;
		});

		hasReset = true;
	}

	public boolean repeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public static class HoldImageButtonStyle extends ImageButtonStyle {
		public Drawable imageHeld;
		public Color imageHeldColor;

		public HoldImageButtonStyle(Drawable up, Drawable down, Drawable checked, Drawable imageUp, Drawable imageDown, Drawable imageChecked, Drawable imageHeld) {
			super(up, down, checked, imageUp, imageDown, imageChecked);
			this.imageHeld = imageHeld;
		}

		public HoldImageButtonStyle(HoldImageButtonStyle style) {
			super(style);
			imageHeld = style.imageHeld;
			imageHeldColor = style.imageHeldColor;
		}

		public HoldImageButtonStyle(ImageButtonStyle style) {
			super(style);
		}
	}
}
