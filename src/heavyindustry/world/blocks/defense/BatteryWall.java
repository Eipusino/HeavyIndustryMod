package heavyindustry.world.blocks.defense;

import arc.audio.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

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
