package heavyindustry.gen;

import arc.Events;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.entities.Units;
import mindustry.game.EventType.PayloadDropEvent;
import mindustry.game.EventType.PickupEvent;
import mindustry.gen.Building;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.io.TypeIO;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.power.PowerGraph;

public class PayloadLegsUnit extends ExtraLegsUnit implements Payloadc {
	protected Seq<Payload> payloads = new Seq<>();

	protected transient @Nullable PowerGraph payloadPower;

	protected PayloadLegsUnit() {}

	@Override
	public int classId() {
		return EntityRegister.getId(PayloadLegsUnit.class);
	}

	@Override
	public void update() {
		super.update();
		if (payloadPower != null) {
			payloadPower.clear();
		}

		//update power graph first, resolve everything
		for (Payload pay : payloads) {
			if (pay instanceof BuildPayload pb && pb.build.power != null) {
				if (payloadPower == null) payloadPower = new PowerGraph(false);

				//pb.build.team = team;
				pb.build.power.graph = null;
				payloadPower.add(pb.build);
			}
		}

		if (payloadPower != null) {
			payloadPower.update();
		}

		for (Payload pay : payloads) {
			pay.set(x, y, rotation);
			pay.update(this, null);
		}
	}

	@Override
	public void destroy() {
		if (Vars.state.rules.unitPayloadsExplode) {
			for (Payload pay : payloads) {
				pay.destroyed();
			}
		}
		super.destroy();
	}

	@Override
	public void payloads(Seq<Payload> seq) {
		payloads = seq;
	}

	@Override
	public float payloadUsed() {
		return payloads.sumf(p -> p.size() * p.size());
	}

	@Override
	public boolean canPickup(Unit unit) {
		return type.pickupUnits && payloadUsed() + unit.hitSize * unit.hitSize <= type.payloadCapacity + 0.001f && unit.team == team() && unit.isAI();
	}

	@Override
	public boolean canPickup(Building build) {
		return payloadUsed() + build.block.size * build.block.size * Vars.tilesize * Vars.tilesize <= type.payloadCapacity + 0.001f && build.canPickup() && build.team == team;
	}

	@Override
	public boolean canPickupPayload(Payload pay) {
		return payloadUsed() + pay.size() * pay.size() <= type.payloadCapacity + 0.001f && (type.pickupUnits || !(pay instanceof UnitPayload));
	}

	@Override
	public boolean hasPayload() {
		return payloads.any();
	}

	@Override
	public void addPayload(Payload load) {
		payloads.add(load);
	}

	@Override
	public void pickup(Unit unit) {
		if (unit.isAdded()) unit.team.data().updateCount(unit.type, 1);

		unit.remove();
		addPayload(new UnitPayload(unit));
		Fx.unitPickup.at(unit);
		if (Vars.net.client()) {
			Vars.netClient.clearRemovedEntity(unit.id);
		}
		Events.fire(new PickupEvent(this, unit));
	}

	@Override
	public void pickup(Building tile) {
		tile.pickedUp();
		tile.tile.remove();
		tile.afterPickedUp();
		addPayload(new BuildPayload(tile));
		Fx.unitPickup.at(tile);
		Events.fire(new PickupEvent(this, tile));
	}

	@Override
	public boolean dropLastPayload() {
		if (payloads.isEmpty()) return false;

		Payload load = payloads.peek();

		if (tryDropPayload(load)) {
			payloads.pop();
			return true;
		}
		return false;
	}

	@Override
	public boolean tryDropPayload(Payload payload) {
		Tile on = tileOn();

		//clear removed state of unit so it can be synced
		if (Vars.net.client() && payload instanceof UnitPayload u) {
			Vars.netClient.clearRemovedEntity(u.unit.id);
		}

		//drop off payload on acceptor if possible
		if (on != null && on.build != null && on.build.team == team && on.build.acceptPayload(on.build, payload)) {
			Fx.unitDrop.at(on.build);
			on.build.handlePayload(on.build, payload);
			return true;
		}

		if (payload instanceof BuildPayload b) {
			return dropBlock(b);
		} else if (payload instanceof UnitPayload p) {
			return dropUnit(p);
		}
		return false;
	}

	@Override
	public boolean dropUnit(UnitPayload payload) {
		Unit u = payload.unit;

		//add random offset to prevent unit stacking
		Tmp.v1.rnd(Mathf.random(2f));

		//can't drop ground units
		//allow stacking for small units for now - otherwise, unit transfer would get annoying
		if (!u.canPass(World.toTile(x + Tmp.v1.x), World.toTile(y + Tmp.v1.y)) || Units.count(x, y, u.physicSize(), o -> o.isGrounded() && o.hitSize > 14f) > 1) {
			return false;
		}

		Fx.unitDrop.at(this);

		//clients do not drop payloads
		if (Vars.net.client()) return true;

		u.set(x + Tmp.v1.x, y + Tmp.v1.y);
		u.rotation(rotation);
		//reset the ID to a new value to make sure it's synced
		u.id = EntityGroup.nextId();
		//decrement count to prevent double increment
		if (!u.isAdded()) u.team.data().updateCount(u.type, -1);
		u.add();
		u.unloaded();
		Events.fire(new PayloadDropEvent(this, u));

		return true;
	}

	/** @return whether the tile has been successfully placed. */
	@Override
	public boolean dropBlock(BuildPayload payload) {
		Building tile = payload.build;
		int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
		Tile on = Vars.world.tile(tx, ty);
		if (on != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false)) {
			payload.place(on, tile.rotation);
			Events.fire(new PayloadDropEvent(this, tile));

			if (getControllerName() != null) {
				payload.build.lastAccessed = getControllerName();
			}

			Fx.unitDrop.at(tile);
			on.block().placeEffect.at(on.drawx(), on.drawy(), on.block().size);
			return true;
		}

		return false;
	}

	@Override
	public Seq<Payload> payloads() {
		return payloads;
	}

	@Override
	public void contentInfo(Table table, float itemSize, float width) {
		table.clear();
		table.top().left();

		float pad = 0;
		float items = payloads.size;
		if (itemSize * items + pad * items > width) {
			pad = (width - (itemSize) * items) / items;
		}

		for (Payload p : payloads) {
			table.image(p.icon()).size(itemSize).padRight(pad);
		}
	}

	@Override
	public void read(Reads read) {
		super.read(read);
		int r = read.i();
		payloads.clear();
		for (int i = 0; i < r; i++) {
			Payload p = TypeIO.readPayload(read);
			if (p != null) payloads.add(p);
		}
	}

	@Override
	public void write(Writes write) {
		super.write(write);
		write.i(payloads.size);
		for (int i = 0; i < payloads.size; i++) {
			TypeIO.writePayload(write, payloads.get(i));
		}
	}

	@Override
	public void readSync(Reads read) {
		super.readSync(read);
		int r = read.i();
		payloads.clear();
		for (int i = 0; i < r; i++) {
			Payload p = TypeIO.readPayload(read);
			if (p != null) payloads.add(p);
		}
	}

	@Override
	public void writeSync(Writes write) {
		super.writeSync(write);
		write.i(payloads.size);
		for (int i = 0; i < payloads.size; i++) {
			TypeIO.writePayload(write, payloads.get(i));
		}
	}

	public static PayloadLegsUnit create() {
		return new PayloadLegsUnit();
	}
}
