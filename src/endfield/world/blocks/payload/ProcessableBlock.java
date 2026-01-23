package endfield.world.blocks.payload;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class ProcessableBlock extends Block {
	public Block resultBlock;
	public Color processColor = Pal.slagOrange;
	public float processAlpha = 0.5f;

	public ProcessableBlock(String name) {
		super(name);
		solid = true;
		destructible = true;
		canOverdrive = false;
		drawDisabled = false;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ProcessableBuild::new;
	}

	public class ProcessableBuild extends Building {
		public float progress;

		@Override
		public void draw() {
			super.draw();
			Draw.alpha(progress);
			Draw.rect(resultBlock.region, x, y);

			Draw.color(processColor);
			Draw.alpha(Mathf.slope(progress) * processAlpha);
			Draw.blend(Blending.additive);
			Fill.rect(x, y, Vars.tilesize * size, Vars.tilesize * size);
			Draw.blend();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(progress);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.f();
		}
	}
}
