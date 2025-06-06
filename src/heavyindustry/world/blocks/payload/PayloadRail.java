package heavyindustry.world.blocks.payload;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HShaders;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PayloadRail extends PayloadBlock {
	protected static final IntSet highlighted = new IntSet();
	protected static float zeroPrecision = 0.5f;

	public float maxPayloadSize = 3f;
	public float railSpeed = -1f;
	public float followSpeed = 0.1f;
	public float bufferDst = 1f;
	public float range = 220f;
	public float arrivedRadius = 4f;
	public float clawWarmupRate = 0.08f;
	public float warmupSpeed = 0.05f;

	protected TextureRegion railEndRegion, arrowRegion;
	protected TextureRegion[] railRegions, clawRegions;

	public PayloadRail(String name) {
		super(name);
		size = 3;
		configurable = true;
		outputsPayload = true;
		acceptsPayload = true;
		update = true;
		rotate = true;
		solid = true;
		canOverdrive = false;

		config(Point2.class, (PayloadRailBuild tile, Point2 point) -> {
			tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY());
		});
		config(Integer.class, (PayloadRailBuild tile, Integer link) -> {
			tile.items.each(RailPayload::removed);
			tile.items.clear();
			tile.link = link;
		});
	}

	/**
	 * Convert hitbox side length to corner dist.
	 */
	public static float payRadius(Payload p) {
		float a = p.size() / 2f;
		return Mathf.sqrt(2 * a * a);
	}

	@Override
	public void init() {
		super.init();

		if (railSpeed < 0) railSpeed = payloadSpeed;
		clipSize = Math.max(clipSize, (range + maxPayloadSize + 2) * 2f);
	}

	public boolean linkValid(Tile tile, Tile other) {
		if (tile == null || other == null || tile.build == null || other.build == null || tile.build.pos() == other.build.pos() || positionsValid(tile.build.tileX(), tile.build.tileY(), other.build.tileX(), other.build.tileY()) || !(other.build instanceof PayloadRailBuild b))
			return false;

		return tile.block() == other.block() && (b.incoming == -1 || b.incoming == tile.build.pos()) && b.link != tile.build.pos();
	}

	public boolean positionsValid(int x1, int y1, int x2, int y2) {
		return !(Mathf.dst(x1, y1, x2, y2) <= range / tilesize);
	}

	@Override
	public void drawOverlay(float x, float y, int rotation) {
		Lines.stroke(1f);
		Draw.color(Pal.accent);
		Drawf.circles(x, y, range);
		Draw.reset();
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, inRegion, outRegion, topRegion, railEndRegion};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
		Draw.rect(railEndRegion, plan.drawx(), plan.drawy());
	}

	@Override
	public void load() {
		super.load();
		railEndRegion = Core.atlas.find(name + "-rail-end");

		railRegions = new TextureRegion[3];
		clawRegions = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			railRegions[i] = Core.atlas.find(name + "-rail-" + i);
			clawRegions[i] = Core.atlas.find(name + "-claw-" + i);
		}

		arrowRegion = Core.atlas.find("bridge-arrow");
	}

	public class PayloadRailBuild extends PayloadBlockBuild<Payload> {
		public Seq<RailPayload> items = new Seq<>();
		public int link = -1;
		public int incoming = -1;
		public float clawInAlpha, clawOutAlpha;
		public float warmup;
		public Vec2 clawVec = new Vec2();

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			if (incoming == -1) {
				boolean fallback = true;
				for (int i = 0; i < 4; ++i) {
					if (blends(i)) {
						Draw.rect(inRegion, x, y, (float) (i * 90 - 180));
						fallback = false;
					}
				}

				if (fallback) {
					Draw.rect(inRegion, x, y, (float) (rotation * 90));
				}
			}

			if (checkLink()) {
				Draw.rect(outRegion, x, y, rotdeg());
			}

			Draw.z(Layer.blockOver + 0.1f);
			Draw.rect(topRegion, x, y);
			Draw.z(Layer.power + 0.1f);
			Draw.rect(railEndRegion, x, y);

			Draw.z(Layer.power + 0.2f);
			if (incoming != -1) {
				Building other = world.build(incoming);
				if (other instanceof PayloadRailBuild) {
					Drawn.spinSprite(clawRegions, x + clawVec.x, y + clawVec.y, other.angleTo(this), clawInAlpha);
				}
			}

			drawPayload();

			if (checkLink()) return;
			Building other = world.build(link);
			float opacity = Renderer.bridgeOpacity;
			if (!(other instanceof PayloadRailBuild) || Mathf.zero(opacity)) return;

			items.each(p -> {
				Draw.z(Layer.power - 1);
				p.draw();
			});

			Draw.z(Layer.power);
			float texW = railRegions[0].width / 4f;
			int count = Mathf.round(dst(other) / texW);
			float width = dst(other) / (count * texW);
			float ang = angleTo(other);
			float dx = (other.x - x) / count;
			float dy = (other.y - y) / count;
			for (int i = 0; i < count; i++) {
				float j = (i + 0.5f);
				Drawn.spinSprite(railRegions, x + dx * j, y + dy * j, texW * width, railRegions[0].height / 4f, ang, opacity);
			}

			Draw.z(Layer.effect);
			Draw.color(Pal.accent, opacity);
			Draw.scl(warmup * 1.1f);
			for (int i = 0; i < 4; i++) {
				Tmp.v1.set(x, y).lerp(other.x, other.y, 0.5f + (i - 1.5f) * 0.2f);
				Draw.rect(arrowRegion, Tmp.v1.x, Tmp.v1.y, ang);
			}
			Draw.color();
			Draw.scl();

			Draw.z(Layer.power + 0.2f);
			Drawn.spinSprite(clawRegions, x, y, ang, clawOutAlpha * opacity);
		}

		@Override
		public void drawPayload() {
			if (payload != null) {
				updatePayload();

				Draw.z((incoming != -1 && !checkLink()) ? Layer.power - 1 : Layer.blockOver);
				payload.draw();
			}
		}

		@Override
		public void drawSelect() {
			if (!checkLink()) {
				drawInput(world.tile(link));
			}

			if (incoming != -1) {
				drawInput(world.tile(incoming));
			}
		}

		private void drawInput(Tile other) {
			boolean linked = other.pos() == link;

			Tmp.v2.trns(tile.angleTo(other), 2f);
			float tx = tile.drawx(), ty = tile.drawy();
			float ox = other.drawx(), oy = other.drawy();
			float alpha = Math.abs((linked ? 100 : 0) - (Time.time * 2f) % 100f) / 100f;
			float x = Mathf.lerp(ox, tx, alpha);
			float y = Mathf.lerp(oy, ty, alpha);

			float rot = linked ? angleTo(other) : other.angleTo(tile);

			//draw "background"
			Draw.color(Pal.gray);
			Lines.stroke(2.5f);
			Lines.square(ox, oy, 2f, 45f);
			Lines.stroke(2.5f);
			Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

			//draw foreground colors
			Draw.color(linked ? Pal.place : Pal.accent);
			Lines.stroke(1f);
			Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

			Lines.square(ox, oy, 2f, 45f);
			Draw.mixcol(Draw.getColor(), 1f);
			Draw.color();
			Draw.rect(arrowRegion, x, y, rot);
			Draw.mixcol();
		}

		@Override
		public void drawConfigure() {
			Drawf.select(x, y, tile.block().size * tilesize / 2f + 2f, Pal.accent);

			highlighted.clear();
			for (int tx = (int) -range - 1; tx <= range + 1; tx++) {
				for (int ty = (int) -range - 1; ty <= range + 1; ty++) {
					Tile other = world.tile(tileX() + tx, tileY() + ty);
					if (linkValid(tile, other) && !highlighted.contains(other.pos())) {
						highlighted.add(other.pos());
					}
				}
			}

			highlighted.each(i -> {
				Tile other = world.tile(i);
				boolean linked = other.build.pos() == link;
				Drawf.select(other.build.x, other.build.y, tile.block().size * tilesize / 2f + 2f + (linked ? 0f : Mathf.absin(Time.time, 4f, 1f)), linked ? Pal.place : Pal.breakInvalid);
			});
		}

		@Override
		public void updateTile() {
			if (incoming != -1) {
				checkIncoming();
			}

			clawInAlpha = Mathf.approachDelta(clawInAlpha, 0, clawWarmupRate);

			if (link == -1) {
				moveOutPayload();
				return;
			}

			if (checkLink()) {
				items.each(RailPayload::removed);
				items.clear();
				moveOutPayload();
				return;
			}

			clawOutAlpha = Mathf.approachDelta(clawOutAlpha, 1, clawWarmupRate);
			if (moveInPayload(true)) {
				if (items.isEmpty() || dst(items.peek()) > items.peek().radius() + payRadius(payload) + bufferDst) {
					items.add(new RailPayload(payload, x, y));
					payload = null;
					clawOutAlpha = 0f;
				}
			}

			updateRail();
		}

		public void updateRail() {
			PayloadRailBuild other = (PayloadRailBuild) world.build(link);

			for (int i = 0; i < items.size; i++) {
				Position target = i == 0 ? other : items.get(i - 1);
				items.get(i).update(target);
			}

			if (items.any()) {
				RailPayload first = items.first();
				if (first.payArrived(other) && other.acceptPayload(this, first.payload)) {
					other.handlePayload(this, first.payload);
					items.remove(0);
				}
			}

			warmup = Mathf.approachDelta(warmup, items.any() ? 1 : 0, warmupSpeed);
		}

		@Override
		public void remove() {
			super.remove();
			items.each(RailPayload::removed);
			items.clear();
		}

		@Override
		public boolean moveInPayload(boolean rotate) {
			if (payload == null) return false;

			updatePayload();

			if (rotate) {
				PayloadRailBuild other = (PayloadRailBuild) world.build(link);
				float rotTarget = other != null ? angleTo(other) : block.rotate ? rotdeg() : 90f;
				payRotation = Angles.moveToward(payRotation, rotTarget, payloadRotateSpeed * delta());
			}
			payVector.approach(Vec2.ZERO, payloadSpeed * delta());

			return hasArrived();
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return super.acceptPayload(source, payload) && (incoming == -1 || source == world.build(incoming));
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			if (incoming != -1) {
				this.payload = payload;
				payVector.set(payload).sub(this);
				payRotation = payload.rotation();
				updatePayload();

				clawVec.set(payload).sub(this);
				clawInAlpha = 1f;
			} else {
				super.handlePayload(source, payload);
			}
		}

		@Override
		public boolean onConfigureBuildTapped(Building other) {
			if (linkValid(tile, other.tile)) {
				if (link == other.pos()) {
					configure(-1);
				} else {
					configure(other.pos());
				}
				return false;
			}
			return true;
		}

		/**
		 * @return true if link invalid
		 */
		public boolean checkLink() {
			if (link == -1) return true;
			Building other = world.build(link);
			if (!(other instanceof PayloadRailBuild build) || build.link == pos() || positionsValid(tileX(), tileY(), other.tileX(), other.tileY())) {
				return true;
			}
			if (build.incoming == -1) {
				build.incoming = pos();
			}
			return build.incoming != pos();
		}

		public void checkIncoming() {
			Building other = world.build(incoming);
			if (!(other instanceof PayloadRailBuild build) || build.link != pos() || positionsValid(tileX(), tileY(), other.tileX(), other.tileY())) {
				incoming = -1;
			}
		}

		@Override
		public Point2 config() {
			if (tile == null) return null;
			return Point2.unpack(link).sub(tile.x, tile.y);
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(link);
			write.i(incoming);
			write.i(items.size);

			for (RailPayload p : items) {
				p.write(write);
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			link = read.i();
			incoming = read.i();

			int amount = read.i();
			for (int i = 0; i < amount; i++) {
				RailPayload p = new RailPayload();
				p.read(read);
				items.add(p);
			}
		}
	}

	public class RailPayload implements Position {
		public Payload payload;
		public float x, y;
		public float dir;

		public RailPayload(Payload payload, float x, float y) {
			this.payload = payload;
			this.x = x;
			this.y = y;
		}

		public RailPayload() {
		}

		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		public void update(Position target) {
			if (target == null) return;

			Tmp.v1.set(target);
			Tmp.v2.set(this);
			if (target instanceof RailPayload r) {
				float dst = r.radius() + radius() + bufferDst;
				Tmp.v1.approach(Tmp.v2, dst);
			}

			Tmp.v2.approachDelta(Tmp.v1, railSpeed);
			x = Tmp.v2.x;
			y = Tmp.v2.y;

			dir = payload.angleTo(target);

			payload.set(Mathf.lerpDelta(payload.x(), x, followSpeed), Mathf.lerpDelta(payload.y(), y, followSpeed), Angles.moveToward(payload.rotation(), dir, payloadRotateSpeed * Time.delta));
		}

		public void draw() {
			float opacity = Renderer.bridgeOpacity;
			if (opacity < 0.999f) {
				FrameBuffer buffer = renderer.effectBuffer;
				Draw.draw(Draw.z(), () -> {
					buffer.begin(Color.clear);
					payload.draw();
					buffer.end();

					HShaders.alphaShader.alpha = opacity;
					buffer.blit(HShaders.alphaShader);
				});
			} else {
				payload.draw();
			}
			Draw.z(Layer.power + 0.2f);
			Drawn.spinSprite(clawRegions, payload.x(), payload.y(), dir, opacity);
		}

		public boolean railArrived(Position target) {
			return Mathf.within(target.getX(), target.getY(), x, y, zeroPrecision);
		}

		public boolean payArrived(Position target) {
			return Mathf.within(target.getX(), target.getY(), payload.x(), payload.y(), arrivedRadius);
		}

		public boolean clawArrived(Position target) {
			return Mathf.within(target.getX(), target.getY(), payload.x(), payload.y(), 0.5f);
		}

		public float radius() {
			return payRadius(payload);
		}

		public void removed() {
			payload.dump();
			payload = null;
		}

		public void write(Writes write) {
			write.f(x);
			write.f(y);
			Payload.write(payload, write);
			write.f(payload.x());
			write.f(payload.y());
			write.f(payload.rotation());
		}

		public void read(Reads read) {
			x = read.f();
			y = read.f();
			payload = Payload.read(read);
			payload.set(read.f(), read.f(), read.f());
		}
	}
}
