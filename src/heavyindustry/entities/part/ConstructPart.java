package heavyindustry.entities.part;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;

/**
 * Draws a region with a construct animation, is meant to be used only on units.
 *
 * @author Eipusino
 */
public class ConstructPart extends DrawPart {
	/** Appended to unit/weapon/block name and drawn. */
	public String suffix;
	/** HOverrides suffix if set. */
	public @Nullable String name;

	/** Construction progress. */
	public PartProgress progress = PartProgress.reload;

	/** When to consider that it finished constructing. */
	public float finishTresh = 0.95f;

	/** Position and rotation offset. */
	public float x, y, rot;

	/** Offset for construct and outline region. */
	public float layerOffset = 0f, outlineLayerOffset = 0f;

	public TextureRegion constructRegion, outlineRegion;

	public ConstructPart(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void draw(PartParams params) {
		float z = Draw.z(),
				dx = params.x + Angles.trnsx(params.rotation - 90f, x, y),
				dy = params.y + Angles.trnsy(params.rotation - 90f, x, y),
				dr = params.rotation + rot - 90,
				prog = progress.getClamp(params);
		Draw.z(z + outlineLayerOffset);
		Draw.rect(outlineRegion, dx, dy, dr);
		Draw.z(z + layerOffset);
		if (prog < finishTresh) {
			Draw.draw(Draw.z(), () -> Drawf.construct(dx, dy, constructRegion, dr, prog, 1f, Time.time));
		} else {
			Draw.rect(constructRegion, dx, dy, dr);
		}
		Draw.z(z);
	}

	@Override
	public void load(String name) {
		if (this.name == null) {
			this.name = name + suffix;
		}

		constructRegion = Core.atlas.find(this.name);
		outlineRegion = Core.atlas.find(this.name + "-outline");
	}
}
