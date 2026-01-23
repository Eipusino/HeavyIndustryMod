package endfield.world.blocks.defense;

import arc.Core;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.audio.Sounds2;
import endfield.content.Fx2;
import endfield.graphics.Drawn;
import endfield.ui.Elements;
import endfield.util.CollectionList;
import endfield.world.Worlds;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.core.UI;
import mindustry.core.World;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static endfield.ui.Elements.LEN;

/**
 * Basic Commandable Attacker Block.
 *
 * @see BombLauncher
 * @see AirRaider
 * @since 1.0.4
 */
public abstract class CommandableAttackerBlock extends CommandableBlock {
	public float spread = 120f;
	public float prepareDelay = 60f;
	public int storage = 1;
	public ShootPattern shoot = new ShootPattern();

	public BulletType bullet = Bullets.placeholder;

	public CommandableAttackerBlock(String name) {
		super(name);

		replaceable = true;
		canOverdrive = false;
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Drawf.dashCircle(x * Vars.tilesize + offset, y * Vars.tilesize + offset, range, Pal.accent);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks);
		stats.add(Stat.damage, StatValues.ammo(ObjectMap.of(this, bullet)));
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("progress", (CommandableAttackerBlockBuild tile) -> new Bar(
				() -> Core.bundle.get("bar.progress"),
				() -> Pal.power,
				() -> (tile.reload % reloadTime) / reloadTime
		));
		addBar("storage", (CommandableAttackerBlockBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.capacity", UI.formatAmount(tile.ammo())),
				() -> Pal.ammo,
				() -> (float) tile.ammo() / storage
		));
	}

	public abstract class CommandableAttackerBlockBuild extends CommandableBuild {
		@Override
		public boolean isCharging() {
			return efficiency > 0 && reload < reloadTime * storage && !initiateConfigure;
		}

		@Override
		public boolean shouldCharge() {
			return reload < reloadTime * storage;
		}

		public int ammo() {
			return (int) (reload / reloadTime);
		}

		@Override
		public void control(LAccess type, Object p1, double p2, double p3, double p4) {
			super.control(type, p1, p2, p3, p4);
		}

		@Override
		public BlockStatus status() {
			return canCommand(targetVec) ? BlockStatus.active : isCharging() ? BlockStatus.noOutput : BlockStatus.noInput;
		}

		@Override
		public void updateTile() {
			super.updateTile();

			if (shouldChargeConfigure()) {
				configureChargeProgress += edelta() * warmup;
				if (configureChargeComplete()) {
					shoot(lastConfirmedTarget);
				}
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			target = read.i();
			reload = read.f();
			initiateConfigure = read.bool();
			configureChargeProgress = read.f();

			TypeIO.readVec2(read, lastConfirmedTarget);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(target);
			write.f(reload);
			write.bool(initiateConfigure);
			write.f(configureChargeProgress);

			TypeIO.writeVec2(write, lastConfirmedTarget);
		}

		@Override
		public void command(Vec2 pos) {
			lastConfirmedTarget.set(pos);
			targetVec.set(pos);
			target = Point2.pack(World.toTile(pos.x), World.toTile(pos.y));
			initiateConfigure = true;

			Fx2.attackWarningPos.at(lastConfirmedTarget.x, lastConfirmedTarget.y, configureChargeTime, team.color, tile);
		}

		/**
		 * Should Be Overridden.
		 */
		public void shoot(Vec2 target) {
			configureChargeProgress = 0;
			initiateConfigure = false;
			reload = Math.max(0, reload - reloadTime);

			consume();
		}

		@Override
		public void drawConfigure() {
			super.drawConfigure();

			Drawf.dashCircle(x, y, range, team.color);

			if (!drawsTmp.isEmpty()) drawsTmp.clear();

			CollectionList<CommandableBuild> commandableBuilds = Worlds.commandableBuilds;
			for (CommandableBuild build : commandableBuilds) {
				if (build != this && build != null && build.team == team && sameGroup(build.block) && build.canCommand(targetVec)) {
					drawsTmp.add(build);
					Drawn.posSquareLink(Pal.gray, 3, 4, false, build.x, build.y, targetVec.x, targetVec.y);
				}
			}

			for (CommandableBuild build : drawsTmp) {
				Drawn.posSquareLink(Pal.heal, 1, 2, false, build.x, build.y, targetVec.x, targetVec.y);
			}

			if (drawsTmp.any()) {
				Drawn.posSquareLink(Pal.accent, 1, 2, true, x, y, targetVec.x, targetVec.y);
				Drawn.drawConnected(targetVec.x, targetVec.y, 10f, Pal.accent);
			}

			if (canCommand(targetVec)) drawsTmp.add(this);
			if (drawsTmp.any())
				Drawn.overlayText(Core.bundle.format("text.participants", drawsTmp.size()), targetVec.x, targetVec.y, Vars.tilesize * 2f, Pal.accent, true);
		}

		@Override
		public void commandAll(Vec2 pos) {
			participantsTmp.clear();

			CollectionList<CommandableBuild> commandableBuilds = Worlds.commandableBuilds;
			for (int i = 0; i < commandableBuilds.size; i++) {
				CommandableBuild build = commandableBuilds.items[i];

				if (build.team == team && sameGroup(build.block) && build.canCommand(pos)) {
					build.command(pos);
					participantsTmp.add(build);
					build.lastAccessed = String.valueOf(Iconc.modeAttack);
				}
			}

			if (!Vars.headless && participantsTmp.any()) {
				if (team != Vars.player.team())
					Elements.showToast(Icon.warning, "[#ff7b69]Caution: []Attack " + (int) (pos.x / 8) + ", " + (int) (pos.y / 8), Sounds2.alert2);
				Fx2.attackWarningRange.at(pos.x, pos.y, 80, team.color);
			}
		}

		@Override
		public boolean canCommand(Vec2 target) {
			return ammo() > 0 && warmup > 0.25f && within(target, range()) && !isChargingConfigure();
		}

		@Override
		public void buildConfiguration(Table table) {
			Vars.control.input.selectedBlock();

			table.table(Tex.paneSolid, t -> {
				t.button(Icon.modeAttack, Styles.cleari, () -> {
					configure(targetVec);
				}).size(LEN).disabled(b -> targetVec.epsilonEquals(x, y, 0.1f));
				t.button("@mod.ui.select-target", Icon.move, Styles.cleart, LEN, () -> {
					Elements.selectPos(t, this::configure);
				}).size(LEN * 4, LEN).row();
			}).fill();
		}
	}
}
