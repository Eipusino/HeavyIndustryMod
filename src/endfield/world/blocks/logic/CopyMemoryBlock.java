package endfield.world.blocks.logic;

import mindustry.world.blocks.logic.MemoryBlock;

public class CopyMemoryBlock extends MemoryBlock {
	public CopyMemoryBlock(String name) {
		super(name);

		config(double[].class, (CopyMemoryBuild tile, double[] ds) -> {
			System.arraycopy(ds, 0, tile.memory, 0, ds.length);
		});
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CopyMemoryBuild::new;
	}

	public class CopyMemoryBuild extends MemoryBuild {
		public double[] buffer = new double[memoryCapacity];

		public void updateMemory() {
			System.arraycopy(memory, 0, buffer, 0, memory.length);
		}

		@Override
		public Object config() {
			updateMemory();

			return buffer;
		}
	}
}
