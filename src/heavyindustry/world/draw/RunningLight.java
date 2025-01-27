package heavyindustry.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.draw.*;

public class RunningLight extends DrawBlock {
	public int size;
	public TextureRegion[] regions;
	public Color color = Pal.accent;
	public boolean contrary = true;

	public RunningLight(int siz) {
		size = siz;
		regions = new TextureRegion[siz];
	}

	@Override
	public void load(Block block) {
		for (int i = 0; i < size; i++) {
			int ci = size - 1 - i;
			regions[i] = !contrary ? Core.atlas.find(block.name + "-glow-" + i) : Core.atlas.find(block.name + "-glow-" + ci);
		}
	}

	@Override
	public void draw(Building build) {
		for (int i = 0; i < size; i++) {
			float sin = Mathf.absin(Time.time + i * (60f / size), 6, 1);
			float a = sin * build.warmup();
			Draw.color(color.cpy().a(a));
			if (build instanceof TurretBuild)
				Draw.rect(regions[i], build.x + ((TurretBuild) build).recoilOffset.x, build.y + ((TurretBuild) build).recoilOffset.y, build.drawrot());
			else Draw.rect(regions[i], build.x, build.y, build.drawrot());
		}
		Draw.reset();
	}
}
