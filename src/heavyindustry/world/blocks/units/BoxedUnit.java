package heavyindustry.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import heavyindustry.graphics.Drawn;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.logic.LAccess;
import mindustry.type.UnitType;
import mindustry.world.Block;

public class BoxedUnit extends Block {
	public UnitType type;
	public TextureRegion colorRegion;

	public BoxedUnit(String name) {
		this(name, UnitTypes.alpha);
	}

	public BoxedUnit(String name, UnitType ut) {
		super(name);
		type = ut;

		destructible = true;
		solid = true;
		configurable = true;
		drawDisabled = false;
		canOverdrive = false;
		rebuildable = false;

		config(Boolean.class, (BoxedUnitBuild tile, Boolean ignored) -> tile.spawn());
	}

	@Override
	public void load() {
		super.load();
		colorRegion = Core.atlas.find(name + "-strobe");
	}

	public class BoxedUnitBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Drawn.setStrobeColor();
			Draw.rect(colorRegion, x, y);
			Draw.color();
		}

		@Override
		public void control(LAccess type, double p1, double p2, double p3, double p4) {
			if (type == LAccess.enabled && !Mathf.zero(p1)) spawn();
		}

		@Override
		public boolean canControlSelect(Unit player) {
			return player.isPlayer();
		}

		@Override
		public void onControlSelect(Unit unit) {
			if (!unit.isPlayer()) return;
			Player player = unit.getPlayer();

			Unit u = spawn();
			u.spawnedByCore(true);
			u.apply(StatusEffects.disarmed, 10f); //Short period of disarm so that the ctrl + click from selecting doesn't make you shoot
			Call.unitControl(player, u);
		}

		@Override
		public void buildConfiguration(Table table) {
			table.button(Icon.upload, () -> configure(true));
		}

		protected Unit spawn() {
			Unit u = type.spawn(this, team);
			Fx.spawn.at(this);
			kill();
			u.rotation(90f);
			return u;
		}
	}
}
