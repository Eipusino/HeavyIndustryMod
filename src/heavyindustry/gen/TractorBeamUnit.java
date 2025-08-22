package heavyindustry.gen;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.type.unit.TractorBeamUnitType;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.UnitType;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;

import static mindustry.Vars.state;

public class TractorBeamUnit extends BasePayloadUnit {
	public Payload beamHeld;
	public Vec2 mouse = new Vec2(), payPos = new Vec2(), unitPos = new Vec2();
	public int mouseTileX, mouseTileY;
	public boolean movingIn = false;
	public float beamRange = 8f * 8f;

	private TractorBeamUnitType tractorBeamType;

	@Override
	public void setType(UnitType type) {
		tractorBeamType = checkType(type);

		super.setType(type);
	}

	@Override
	public TractorBeamUnitType checkType(UnitType value) {
		return (TractorBeamUnitType) value;
	}

	public void moveIn() {
		if (beamHeld != null && canPickupPayload(beamHeld)) {
			movingIn = false;
			addPayload(beamHeld);
			beamHeld = null;
			payPos.set(unitPos);
		}
	}

	public void moveOut() {
		if (beamHeld == null && payloads.size != 0) {
			Payload load = payloads.peek();

			if (!tryDropPayload(beamHeld)) {
				beamHeld = load;
				payPos.set(unitPos);

				payloads.pop();
			}
		}
	}

	public void updateHeld() {
		if (beamHeld == null || Vars.state.isPaused()) return;
		mouse.set(Mathf.clamp(aimX, 0, Vars.world.height() * 8f), Mathf.clamp(aimY, 0f, Vars.world.width() * 8f));

		if (mouse.dst(unitPos) > beamRange) {
			mouse.set(mouse.sub(unitPos).setLength(beamRange).add(unitPos));
		}
		mouseTileX = World.toTile(mouse.x);
		mouseTileY = World.toTile(mouse.y);

		if (beamHeld instanceof BuildPayload) {
			payPos.approach(movingIn ? unitPos : mouse, Mathf.clamp(payPos.dst(movingIn ? unitPos : mouse) * 0.1f, movingIn ? 2f : 0.1f, movingIn ? 32f : 16f) * Time.delta);
		} else {
			payPos.set(unitPos);
		}

		beamHeld.update(this, null);
		beamHeld.set(payPos.x, payPos.y, beamHeld instanceof BuildPayload ? ((BuildPayload) beamHeld).build.rotation : rotation);
		if (beamHeld instanceof BuildPayload && ((BuildPayload) beamHeld).build.health <= 0) { //todo FUCK
			beamHeld = null;
		}

		if (movingIn && payPos.dst(unitPos) < 3f) moveIn();
	}

	@Override
	public void updateLastPosition() {
		super.updateLastPosition();
	}

	@Override
	public void pickup(Unit unit) {
		if (beamHeld == null) {
			unit.remove();
			beamHeld = new UnitPayload(unit);
			Fx.unitPickup.at(unit);
			if (Vars.net.client()) {
				Vars.netClient.clearRemovedEntity(unit.id);
			}
			Events.fire(new EventType.PickupEvent(this, unit));
		}
	}

	@Override
	public void pickup(Building build) {
		if (beamHeld == null) {
			payPos.set(build.x, build.y);
			build.pickedUp();
			build.tile.remove();
			build.afterPickedUp();
			beamHeld = new BuildPayload(build);
			Fx.unitPickup.at(build);
			Events.fire(new EventType.PickupEvent(this, build));
		}
	}

	@Override
	public boolean dropLastPayload() {
		if (beamHeld == null) return false;

		if (tryDropPayload(beamHeld)) {
			beamHeld = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean tryDropPayload(Payload payload) {
		if (beamHeld == null || movingIn || payPos.dst(mouse) > 8f) return false;

		Tile on = Vars.world.tileWorld(mouseTileX * 8f, mouseTileY * 8f);
		//clear removed state of unit so it can be synced
		if (Vars.net.client() && payload instanceof UnitPayload) {
			Vars.netClient.clearRemovedEntity(((UnitPayload) payload).unit.id);
		}

		//drop off payload on an acceptor if possible todo pickup sends to inventory immediately
		if (on.build != null && on.build.acceptPayload(on.build, payload)) {
			Fx.unitDrop.at(on.build);
			on.build.handlePayload(on.build, payload);
			return true;
		}

		if (payload instanceof BuildPayload bp) {
			return dropBlock(bp);
		} else if (payload instanceof UnitPayload up) {
			return dropUnit(up);
		}
		return false;
	}

	/** @return whether the tile has been successfully placed. */
	@Override
	public boolean dropBlock(BuildPayload payload) {
		Building payBuild = payload.build;
		Tile on = Vars.world.tile(mouseTileX, mouseTileY);
		if (on != null && Build.validPlace(payBuild.block, payBuild.team, mouseTileX, mouseTileY, payBuild.rotation, false)) {
			payload.place(on, payBuild.rotation);
			Events.fire(new EventType.PayloadDropEvent(this, payBuild));

			if (getControllerName() != null) {
				payload.build.lastAccessed = getControllerName();
			}

			Fx.unitDrop.at(payBuild);
			Fx.placeBlock.at(on.drawx(), on.drawy(), on.block().size);
			return true;
		}

		return false;
	}

	@Override
	public int classId() {
		return Entitys.getId(TractorBeamUnit.class);
	}

	@Override
	public void update() {
		super.update();

		unitPos.set(x, y);
		updateHeld();
	}

	@Override
	public void draw() {
		super.draw();

		if (beamHeld != null) {
			float focusLen = type.buildBeamOffset + Mathf.absin(Time.time, 3f, 0.6f);
			float px = x + Angles.trnsx(rotation, focusLen);
			float py = y + Angles.trnsy(rotation, focusLen);

			Draw.z(35f);
			beamHeld.draw();

			if (beamHeld instanceof BuildPayload) {
				Building b = ((BuildPayload) beamHeld).build;
				float size = b.block.size;

				Draw.z(29f);
				Lines.stroke(1.0f, Pal.techBlue);

				//place preview
				if (Build.validPlace(b.block, b.team, mouseTileX, mouseTileY, b.rotation, false)) {
					Draw.alpha(1f - (payPos.dst(mouse) * 0.1f));
					Drawf.dashRectBasic(
							((mouseTileX - (size / 2f)) + (size % 2 == 0 ? 0.5f : 0f)) * 8f,
							((mouseTileY - (size / 2f)) + (size % 2 == 0 ? 0.5f : 0f)) * 8f,
							size * 8f, size * 8f
					);
					Draw.alpha(1f);
				}

				Draw.z(122f);
				Drawf.buildBeam(px, py, beamHeld.x(), beamHeld.y(), size * 4f);
				Fill.square(px, py, 1.8F + Mathf.absin(Time.time, 2.2f, 1.1f), rotation + 45f);
				Draw.reset();
				Draw.z(115f);
			}
		}
	}

	@Override
	public void add() {
		if (added) return;
		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();

		team.data().updateCount(type, 1);
		if (type.useUnitCap && count() > cap() && !spawnedByCore && !dead && !state.rules.editor) {
			Call.unitCapDeath(this);
			team.data().updateCount(type, -1);
		}

		beamRange = tractorBeamType.tractorBeamRange;
	}

	@Override
	public void write(Writes write) {
		TypeIO.writePayload(write, beamHeld);

		super.write(write);
	}

	@Override
	public void read(Reads read) {
		beamHeld = TypeIO.readPayload(read);

		super.read(read);
	}

	@Override
	public void writeSync(Writes write) {
		TypeIO.writePayload(write, beamHeld);

		super.writeSync(write);
	}

	@Override
	public void readSync(Reads read) {
		beamHeld = TypeIO.readPayload(read);

		super.readSync(read);
	}
}
