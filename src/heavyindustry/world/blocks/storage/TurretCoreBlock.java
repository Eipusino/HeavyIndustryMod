package heavyindustry.world.blocks.storage;

import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
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

	public class TurretCoreBuild extends CoreBuild {
		public BuildPayload payload = new BuildPayload(turret, Team.derelict);

		@Override
		public void updateTile() {
			super.updateTile();
			if (payload.build.team != team) {
				payload.build.team = team;
			}
			payload.update(null, this);

			if (team.core().items.get(ammo) >= 1) {
				if (payload.build.acceptItem(this, ammo)) {
					team.core().items.remove(ammo, 1);
					payload.build.handleItem(this, ammo);
				}
			}
			payload.set(x, y, payload.build.payloadRotation);
		}

		@Override
		public void draw() {
			super.draw();
			payload.draw();
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
			if (turret instanceof BaseTurret t) {
				Drawf.dashCircle(x, y, t.range, Pal.accent);
			}
		}
	}
}
