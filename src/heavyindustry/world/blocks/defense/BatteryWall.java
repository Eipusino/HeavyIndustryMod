package heavyindustry.world.blocks.defense;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.blocks.power.PowerDistributor;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Env;

public class BatteryWall extends PowerDistributor {
	//scale 1:1
	public float percentage = 1;
	public Effect effect = Fx.none;
	public Sound updateSound = Sounds.none;

	public BatteryWall(String name) {
		super(name);
		outputsPower = true;
		consumesPower = true;
		canOverdrive = false;
		flags = EnumSet.of(BlockFlag.battery);
		envEnabled |= Env.space;
		destructible = true;
		update = true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = BatteryWallBuild::new;
	}

	public class BatteryWallBuild extends Building {
		@Override
		public void updateTile() {
			super.updateTile();
			//1 = 100 ?
			if (getPower() >= maxHealth - health && health < maxHealth) {
				power.status -= getRemove();
				heal(maxHealth - health);
				effect.at(this);
				updateSound.at(this);
			}
		}

		@Override
		public BlockStatus status() {
			if (Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
			if (Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
			return BlockStatus.noOutput;
		}

		public float getPower() {
			return power.status * consPower.capacity * percentage;
		}

		public float getRemove() {
			return (maxHealth - health) / consPower.capacity * percentage;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
		}
	}
}
