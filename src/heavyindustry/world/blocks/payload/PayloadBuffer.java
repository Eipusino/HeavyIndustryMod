package heavyindustry.world.blocks.payload;

import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;

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

		addBar("payloadcapacity", (PayloadBufferBuild e) -> new Bar("stat.payloadcapacity", Pal.items, () -> e.payloadUsed / payloadCapacity));
	}

	public class PayloadBufferBuild extends PayloadBlockBuild<Payload> {
		public final Seq<Payload> inventory = new Seq<>(8);

		public Building next;
		public float payloadUsed;

		@Override
		public void updateTile() {
			super.updateTile();
			if (next != null && current() != null && next.acceptPayload(this, current())) {
				if (payload == null && !inventory.isEmpty()) {
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
			else if (!inventory.isEmpty()) return inventory.peek();
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
			if (payload == null && !inventory.isEmpty()) {
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