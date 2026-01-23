package endfield.world.blocks.storage;

import endfield.util.CollectionList;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.logic.Ranged;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.List;

public class MultiTurretCoreBlock extends CoreBlock {
	public List<Block> turretBuilder = new CollectionList<>(Block.class);
	public List<Item> itemBuilder = new CollectionList<>(Item.class);

	public float[] positions = {0, 0};

	public float maxRange;

	public MultiTurretCoreBlock(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = MultiTurretCoreBuild::new;
	}

	public class MultiTurretCoreBuild extends CoreBuild implements Ranged {
		public BuildPayload[] payloads;

		@Override
		public Building create(Block block, Team team) {
			payloads = new BuildPayload[turretBuilder.size()];

			for (int i = 0; i < turretBuilder.size(); i++) {
				payloads[i] = new BuildPayload(turretBuilder.get(i), team);
			}

			return super.create(block, team);
		}

		@Override
		public void updateTile() {
			super.updateTile();

			for (int i = 0; i < payloads.length; i++) {
				BuildPayload payload = payloads[i];

				Building build = payload.build;

				payload.update(null, this);

				for (int j = 0; j < itemBuilder.size(); j++) {
					Item item = itemBuilder.get(j);

					if (build.acceptItem(build, item) && items.get(item) >= 1) {
						build.handleItem(build, item);
						items.remove(item, 1);
					}
				}

				// To avoid terrible exception.
				if (i * 2 + 1 < positions.length) {
					payload.set(positions[i * 2], positions[i * 2 + 1], build.payloadRotation);
				}
			}
		}

		@Override
		public void draw() {
			super.draw();

			for (BuildPayload payload : payloads) {
				payload.draw();
			}
		}

		@Override
		public float range() {
			return maxRange;
		}
	}
}
