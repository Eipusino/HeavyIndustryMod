package endfield.world.blocks.payload;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.ui.Bar;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.player;

public class PayloadBuffer extends PayloadBlock {
	public float payloadCapacity = 6f * 6f * 64f;
	public int displayMax = 8;

	public PayloadBuffer(String name) {
		super(name);
		rotate = true;
		acceptsPayload = true;
		outputsPayload = true;
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("payloadcapacity", (PayloadBufferBuild tile) -> new Bar("stat.payloadcapacity", Pal.items, () -> tile.payloadUsed / payloadCapacity));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PayloadBufferBuild::new;
	}

	public class PayloadBufferBuild extends PayloadBlockBuild<Payload> {
		public final Seq<Payload> inventory = new Seq<>(true, 8, Payload.class);

		public Building next;
		public float payloadUsed;

		@Override
		public void updateTile() {
			super.updateTile();
			if (next != null && current() != null && next.acceptPayload(this, current())) {
				if (payload == null && inventory.any()) {
					payVector.setZero();
					payload = inventory.pop();
					payloadUsed -= paySize();
				}
				moveOutPayload();

			} else if (moveInPayload() && payload != null && payloadCapacity - payloadUsed >= paySize()) {
				inventory.add(payload);
				payloadUsed += paySize();
				payload = null;
			}
		}

		@Override
		public void display(Table table) {
			super.display(table);
			if (team != player.team()) return;

			table.row();
			table.table(t -> {
				t.left().defaults().left();
				for (int i = 1; i < Math.min(displayMax + 2, inventory.size); i++) {
					if (i < displayMax + 1)
						t.image(inventory.get(inventory.size - i).content().uiIcon).size(iconMed).padRight(4).padTop(4);
					else t.label(() -> "[gray][...]");
				}
			});
		}

		public Payload current() {
			if (payload != null) return payload;
			else if (inventory.any()) return inventory.peek();
			return null;
		}

		public float paySize() {
			return payload.size() * payload.size();
		}

		@Override
		public void draw() {
			drawPayload();
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			next = front();
		}

		@Override
		public Payload takePayload() {
			if (payload == null && inventory.any()) {
				payload = inventory.pop();
				payloadUsed -= paySize();
			}
			return super.takePayload();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			TypeIO.writeBuilding(write, next);
			write.f(payloadUsed);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			next = TypeIO.readBuilding(read);
			payloadUsed = read.f();
		}
	}
}