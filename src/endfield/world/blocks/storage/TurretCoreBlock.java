package endfield.world.blocks.storage;

import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.storage.CoreBlock;

public class TurretCoreBlock extends CoreBlock {
	public Block turret = Blocks.duo;
	public Item ammo = Items.copper;

	public TurretCoreBlock(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		if (turret instanceof BaseTurret t) {
			Drawf.dashCircle(x * 8 + offset, y * 8 + offset, t.range, Pal.accent);
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = TurretCoreBuild::new;
	}

	public class TurretCoreBuild extends CoreBuild implements Ranged {
		public BuildPayload payload;

		@Override
		public Building create(Block block, Team team) {
			payload = new BuildPayload(turret, team);

			return super.create(block, team);
		}

		@Override
		public void updateTile() {
			super.updateTile();

			payload.update(null, this);

			Building build = payload.build;

			var core = team.core();

			if (core != null && core.items.get(ammo) >= 1) {
				if (build.acceptItem(this, ammo)) {
					core.items.remove(ammo, 1);
					build.handleItem(this, ammo);
				}
			}
			payload.set(x, y, build.payloadRotation);
		}

		@Override
		public void draw() {
			super.draw();

			payload.draw();
		}

		@Override
		public float range() {
			return turret instanceof BaseTurret t ? t.range : 0f;
		}
	}
}
