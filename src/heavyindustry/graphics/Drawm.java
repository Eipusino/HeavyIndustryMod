package heavyindustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;

import static arc.graphics.g2d.Lines.*;

public final class Drawm {
	private static final Vec2 vec1 = new Vec2(), vec2 = new Vec2(), vec3 = new Vec2(), vec4 = new Vec2();

	/** Don't let anyone instantiate this class. */
	private Drawm() {}

	/** Draws an ellipse. */
	public static void ellipse(float x, float y, float rad, float wScl, float hScl, float rot) {
		float sides = Lines.circleVertices(rad);
		float space = 360 / sides;
		float r1 = rad - getStroke() / 2f, r2 = rad + getStroke() / 2f;

		for (int i = 0; i < sides; i++) {
			float a = space * i;
			vec1.trns(rot, r1 * wScl * Mathf.cosDeg(a), r1 * hScl * Mathf.sinDeg(a));
			vec2.trns(rot, r1 * wScl * Mathf.cosDeg(a + space), r1 * hScl * Mathf.sinDeg(a + space));
			vec3.trns(rot, r2 * wScl * Mathf.cosDeg(a + space), r2 * hScl * Mathf.sinDeg(a + space));
			vec4.trns(rot, r2 * wScl * Mathf.cosDeg(a), r2 * hScl * Mathf.sinDeg(a));
			Fill.quad(x + vec1.x, y + vec1.y, x + vec2.x, y + vec2.y, x + vec3.x, y + vec3.y, x + vec4.x, y + vec4.y);
		}
	}

	/** Generates all team regions for this block. Call #getTeamRegion(Blockf) afterward to get the region. */
	public static void generateTeamRegion(MultiPacker packer, Block b) {
		PixmapRegion teamr = Core.atlas.getPixmap(b.name + "-team");

		for (Team team : Team.all) {
			if (team.hasPalette) {
				Pixmap out = new Pixmap(teamr.width, teamr.height);
				for (int x = 0; x < teamr.width; x++) {
					for (int y = 0; y < teamr.height; y++) {
						int color = teamr.getRaw(x, y);
						int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
						out.setRaw(x, y, index == -1 ? teamr.getRaw(x, y) : team.palettei[index]);
					}
				}
				packer.add(PageType.main, b.name + "-team-" + team.name, out);
			}
		}

		//force reload of team region
		b.load();
	}

	/**
	 * ONly for blocks with 2 or more team regions.
	 * Generates all team regions for this region. Call #loadCustomTeamRegion(String) in load() afterward to get the region. Must be followed by a #generateTeamRegion.
	 */
	public static void customTeamRegion(MultiPacker packer, String name) {
		PixmapRegion teamr = Core.atlas.getPixmap(name + "-team");

		for (Team team : Team.all) {
			if (team.hasPalette) {
				Pixmap out = new Pixmap(teamr.width, teamr.height);
				for (int x = 0; x < teamr.width; x++) {
					for (int y = 0; y < teamr.height; y++) {
						int color = teamr.getRaw(x, y);
						int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
						out.setRaw(x, y, index == -1 ? teamr.getRaw(x, y) : team.palettei[index]);
					}
				}
				packer.add(PageType.main, name + "-team-" + team.name, out);
			}
		}
	}

	/** @return the sharded team texture region for this block */
	public static TextureRegion getTeamRegion(Block b) {
		return Core.atlas.find(b.name + "-team-sharded");
	}

	/** Loads the custom team regions. */
	public static TextureRegion[] loadCustomTeamRegion(String name) {
		TextureRegion[] ret = new TextureRegion[Team.all.length];
		TextureRegion def = Core.atlas.find(name + "-team");
		for (Team team : Team.all) {
			ret[team.id] = def.found() && team.hasPalette ? Core.atlas.find(name + "-team-" + team.name, def) : def;
		}
		return ret;
	}

	/** Draws a sprite that should be lightwise correct, using 4 sprites each colored with a different lighting angle. */
	public static void spinSprite(TextureRegion[] sprites, float x, float y, float r) {
		r = Mathf.mod(r, 360f);
		int now = (((int) (r + 45f)) / 90) % 4;

		Draw.rect(sprites[now], x, y, r);
		Draw.alpha(((r + 45f) % 90f) / 90f);
		Draw.rect(sprites[(now + 1) % 4], x, y, r);
		Draw.alpha(1f);
	}

	/** Draws a sprite that should be light-wise correct. Provided sprite must be symmetrical. */
	public static void spinSprite(TextureRegion region, float x, float y, float r) {
		r = Mathf.mod(r, 90f);
		Draw.rect(region, x, y, r);
		Draw.alpha(r / 90f);
		Draw.rect(region, x, y, r - 90f);
		Draw.alpha(1f);
	}

	/**
	 * Filps a sprite like a coin.
	 *
	 * @param region   Note that this region is flipped left-right, the y-axis being the axis.
	 * @param rotation Technically the yaw.
	 * @param roll	 Negative values tilt the sprite to the east (cw), positive to the west (ccw).
	 */
	public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll) {
		flipSprite(region, x, y, rotation, roll, Color.white, Pal.darkestGray);
	}

	public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll, Color lightColor, Color darkColor) {
		flipSprite(region, x, y, rotation, roll, region.width * Draw.scl * Draw.xscl, region.height * Draw.scl * Draw.yscl, lightColor, darkColor);
	}

	public static void flipSprite(TextureRegion region, float x, float y, float rotation, float roll, float w, float h, Color lightColor, Color darkColor) {
		roll = Mathf.wrapAngleAroundZero(Mathf.degreesToRadians * roll);

		prepareRollColor(roll, lightColor, darkColor, Mathf.clamp(Mathf.cosDeg(rotation - 45f) * 1.42f, -1f, 1f));
		Draw.rect(region, x, y, w * Mathf.cos(roll), h, rotation);
		Draw.mixcol();
	}

	public static void flipSpriteSimple(TextureRegion region, float x, float y, float rotation, float roll, float w, float h) {
		roll = Mathf.wrapAngleAroundZero(Mathf.degreesToRadians * roll);
		Draw.rect(region, x, y, w * Mathf.cos(roll), h, rotation);
	}

	private static void prepareRollColor(float roll, Color lightColor, Color darkColor, float a) {
		if (Mathf.zero(roll)) return;
		float f = Mathf.sin(roll) * 0.7f;
		if (roll > Mathf.pi / 2f || roll < -Mathf.pi / 2f) {
			f = -f;
		}
		f *= a;

		if (f > 0) {
			//dark+
			Draw.mixcol(darkColor, f);
		} else {
			//light+
			Draw.mixcol(lightColor, -f);
		}
	}

	/** Outlines a given textureRegion. Run in createIcons. */
	public static void outlineRegion(MultiPacker packer, TextureRegion tex, Color outlineColor, String name) {
		Pixmap out = Pixmaps.outline(Core.atlas.getPixmap(tex), outlineColor, 4);
		Drawf.checkBleed(out);
		packer.add(MultiPacker.PageType.main, name, out);
	}

	/** Outlines a list of regions. Run in createIcons. */
	public static void outlineRegion(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String name) {
		for (int i = 0; i < textures.length; i++) {
			outlineRegion(packer, textures[i], outlineColor, name + "-" + i);
		}
	}

	/** Lerps 2 TextureRegions. */
	public static TextureRegion blendSprites(TextureRegion a, TextureRegion b, float f, String name) {
		PixmapRegion r1 = Core.atlas.getPixmap(a);
		PixmapRegion r2 = Core.atlas.getPixmap(b);

		Pixmap out = new Pixmap(r1.width, r1.height);
		Color color1 = new Color();
		Color color2 = new Color();

		for (int x = 0; x < r1.width; x++) {
			for (int y = 0; y < r1.height; y++) {
				out.setRaw(x, y, color1.set(r1.getRaw(x, y)).lerp(color2.set(r2.getRaw(x, y)), f).rgba());
			}
		}

		Texture texture = new Texture(out);
		return Core.atlas.addRegion(name + "-blended-" + (int) (f * 100), new TextureRegion(texture));
	}

	public static void dashPoly(float partSize, Color color, float... cords) {
		Lines.stroke(3f, Pal.gray);
		DashLine.dashPolyWithLength(partSize, cords);
		Lines.stroke(1f, color);
		DashLine.dashPolyWithLength(partSize, cords);
		Draw.reset();
	}

	public static void dashPoly(Color color, float... cords) {
		dashPoly(10, color, cords);
	}

	public static void shadow(TextureRegion region, float x, float y, float rotation, float alpha) {
		Draw.color(Pal.shadow, alpha);
		Draw.rect(region, x, y, rotation);
		Draw.color();
	}

	public static void plus(float x, float y, float diameter, float angle) {
		plus(x, y, diameter / 3f, diameter, angle);
	}

	public static void plus(float x, float y, float stroke, float diameter, float angle) {
		for (int i = 0; i < 2; i++) {
			Fill.rect(x, y, stroke, diameter, angle + i * 90f);
		}
	}
}
