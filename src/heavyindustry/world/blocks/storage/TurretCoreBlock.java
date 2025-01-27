package heavyindustry.world.blocks.storage;

import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.*;

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
