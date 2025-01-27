package heavyindustry.scene.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.util.*;
import heavyindustry.*;

public class HoldImageButton extends ImageButton {
	public Boolp canHold = () -> true;

	private Runnable held = () -> {};
	private boolean heldAct;
	private HoldImageButtonStyle style;
	private boolean repeat = false;
	private boolean hasReset;

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

	public void setStyle(Button.ButtonStyle style) {
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
		Drawable drawable = null;
		if (isDisabled() && style.imageDisabled != null) {
			drawable = style.imageDisabled;
		} else if (isHeld() && style.imageHeld != null) {
			drawable = style.imageHeld;
		} else if (isPressed() && style.imageDown != null) {
			drawable = style.imageDown;
		} else if (isChecked() && style.imageChecked != null) {
			drawable = style.imageCheckedOver != null && isOver() ? style.imageCheckedOver : style.imageChecked;
		} else if (isOver() && style.imageOver != null) {
			drawable = style.imageOver;
		} else if (style.imageUp != null) {
			drawable = style.imageUp;
		}

		Color color = getImage().color;
		if (isDisabled() && style.imageDisabledColor != null) {
			color = style.imageDisabledColor;
		} else if (isHeld() && style.imageHeldColor != null) {
			color = style.imageHeldColor;
		} else if (isPressed() && style.imageDownColor != null) {
			color = style.imageDownColor;
		} else if (isChecked() && style.imageCheckedColor != null) {
			color = style.imageCheckedColor;
		} else if (isOver() && style.imageOverColor != null) {
			color = style.imageOverColor;
		} else if (style.imageUpColor != null) {
			color = style.imageUpColor;
		}

		Image image = getImage();
		image.setDrawable(drawable);
		image.setColor(color);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (isPressed() && !isDisabled() && canHold.get()) {
			HIVars.pressTimer += Time.delta;
			if (HIVars.pressTimer > HIVars.longPress && (repeat || !heldAct)) {
				heldAct = true;
				held.run();
			}
		}
	}

	public boolean isHeld() {
		return isPressed() && HIVars.pressTimer > HIVars.longPress;
	}

	public void addReset() {
		if (hasReset) return;

		released(() -> {
			heldAct = false;
			HIVars.pressTimer = 0;
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
