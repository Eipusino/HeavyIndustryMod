package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import heavyindustry.graphics.Drawn;
import mindustry.world.blocks.production.Drill;

public class AugerDrill extends Drill {
	public TextureRegion[] bottomRegions, topRegions, oreRegions;
	public TextureRegion rotorRegion, rotorRotateRegion, mbaseRegion, wormDrive, gearRegion, rotateRegion, overlayRegion;

	public AugerDrill(String name) {
		super(name);

		rotate = true;
		drawArrow = false;
	}

	@Override
	public void load() {
		super.load();

		rotorRegion = Core.atlas.find(name + "-rotor");
		rotorRotateRegion = Core.atlas.find(name + "-rotor-rotate");
		mbaseRegion = Core.atlas.find(name + "-mbase");

		gearRegion = Core.atlas.find(name + "-gear");
		overlayRegion = Core.atlas.find(name + "-overlay");
		rotateRegion = Core.atlas.find(name + "-moving");
		wormDrive = Core.atlas.find(name + "-rotate");

		bottomRegions = new TextureRegion[2];
		topRegions = new TextureRegion[2];
		oreRegions = new TextureRegion[2];

		for (int i = 0; i < 2; i++) {
			bottomRegions[i] = Core.atlas.find(name + "-bottom" + (i + 1));
			topRegions[i] = Core.atlas.find(name + "-top" + (i + 1));
			oreRegions[i] = Core.atlas.find(name + "-ore" + (i + 1));
		}
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	public class ArguerDrillBuild extends DrillBuild {
		public float rot;

		@Override
		public void draw() {
			rot = timeDrilled * rotateSpeed % 1440f;

			float fixedRot = (rotdeg() + 90f) % 180f - 90f;

			int variant = rotation % 2;

			float deg = rotation == 0 || rotation == 3 ? rot : -rot;
			float shaftRot = rot * 2f;

			Point2 offset = Geometry.d4(rotation + 1);

			Draw.rect(bottomRegions[variant], x, y);

			//target region
			if (dominantItem != null && drawMineItem) {
				Draw.color(dominantItem.color);
				Draw.rect(oreRegions[variant], x, y);
				Draw.color();
			}

			//bottom rotor
			Draw.rect(rotorRegion, x, y, 360f - rot * 0.25f);

			Drawn.drawRotRect(rotorRotateRegion, x, y, 24f, 3.5f, 3.5f, 450f - rot * 0.25f, shaftRot, shaftRot + 180f);
			Drawn.drawRotRect(rotorRotateRegion, x, y, 24f, 3.5f, 3.5f, 450f - rot * 0.25f, shaftRot + 180f, shaftRot + 360f);

			//shaft
			Draw.rect(mbaseRegion, x, y, fixedRot);

			Drawn.drawRotRect(wormDrive, x, y, 24f, 3.5f, 3.5f, fixedRot, rot, rot + 180f);
			Drawn.drawRotRect(wormDrive, x, y, 24f, 3.5f, 3.5f, fixedRot, rot + 180f, rot + 360f);
			Drawn.drawRotRect(rotateRegion, x, y, 24f, 3.5f, 3.5f, fixedRot, rot, rot + 180f);

			Draw.rect(overlayRegion, x, y, fixedRot);

			//gears
			Draw.rect(gearRegion, x + offset.x * 4f, y + offset.y * 4f, -deg / 2);
			Draw.rect(gearRegion, x - offset.x * 4f, y - offset.y * 4f, deg / 2);

			Draw.rect(topRegions[variant], x, y);
			drawTeamTop();
		}
	}
}
