package heavyindustry.world.blocks;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import heavyindustry.type.*;
import heavyindustry.type.formloaders.*;
import heavyindustry.type.formloaders.SpriteShapeLoader.ChunkProcessor.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.ConstructBlock.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

public class CustomShapeBlock extends Block {
	protected static final Field blockField = ((Prov<Field>) () -> {
		try {
			Field field = Tile.class.getDeclaredField("block");
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}).get();
	protected static final TileChangeEvent tileChangeEvent = new TileChangeEvent();

	protected final int[] emptySubBuildings = new int[0];

	public CustomShape customShape;
	public Vec2[] worldDrawOffsets = new Vec2[4];

	protected boolean checking = false;
	protected boolean removingSubs = false;

	public CustomShapeBlock(String name) {
		super(name);
		destructible = true;
		update = true;
	}

	@Override
	public void init() {
		super.init();
		if (customShape != null) updateClipSizeAndOffset();

	}

	@Override
	public void load() {
		super.load();

		if (customShape != null) updateClipSizeAndOffset();
	}

	protected void updateClipSizeAndOffset() {
		clipSize = Math.max(Math.max(customShape.width, customShape.height) * tilesize, clipSize);
		worldDrawOffsets[0] = new Vec2()
				.set(customShape.width, customShape.height)
				.scl(1 / 2f)
				.sub(customShape.anchorX() + 0.5f, customShape.anchorY() + 0.5f)
				.scl(tilesize);
		for (int i = 1; i < 4; i++) {
			worldDrawOffsets[i] = worldDrawOffsets[i - 1].cpy().rotate90(1);
		}
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		boolean validPlan = true;
		if (!checking) {
			validPlan = checkNearByValid(tile, null, team, rotation);
		}
		return validPlan && super.canPlaceOn(tile, team, rotation);
	}

	protected boolean checkNearByValid(Tile tile, Building building, Team team, int rotation) {
		boolean[] validPlan = {true};
		eachNearByValid(tile, building, team, rotation, (dx, dy, bool) -> validPlan[0] &= bool);
		return validPlan[0];
	}

	protected void eachNearByValid(Tile tile, Building building, Team team, int rotation, Cons3<Integer, Integer, Boolean> cons3) {
		checking = true;
		customShape.eachRelativeCenter(false, true, false, (dx, dy) -> {
			Tmp.p1.set(dx, dy).rotate(rotation);
			int nearbyX = tile.x + Tmp.p1.x;
			int nearbyY = tile.y + Tmp.p1.y;
			if (Build.validPlace(this, team, nearbyX, nearbyY, rotation) || building != null && Vars.world.build(nearbyX, nearbyY) == building) {
				cons3.get(nearbyX, nearbyY, true);
			} else {
				cons3.get(nearbyX, nearbyY, false);
			}
		});
		checking = false;
	}

	@Override
	public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid, float alpha) {
		super.drawPlan(plan, list, valid, alpha);
		eachNearByValid(plan.tile(), null, Vars.player.team(), plan.rotation, (dx, dy, bool) -> {
			Drawf.selected(dx, dy, this, bool ? Pal.accent : Pal.remove);
		});
	}

	@Override
	public void placeBegan(Tile tile, Block previous) {
		customShape.eachRelativeCenter(false, true, false, (dx, dy) -> {
			ConstructBuild build = (ConstructBuild) tile.build;
			Tile nearby = tile.nearby(Tmp.p1.set(dx, dy).rotate(build.rotation));
			setTile(nearby, tile.block(), tile.build);
		});
		super.placeBegan(tile, previous);
	}

	/** this is a different method so subclasses can call it even after overriding the base */
	public void drawDefaultPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		TextureRegion reg = getPlanRegion(plan, list);
		Vec2 worldDrawOffset = worldDrawOffsets[plan.rotation];
		Draw.rect(reg, plan.drawx() + worldDrawOffset.x, plan.drawy() + worldDrawOffset.y, !rotate || !rotateDraw ? 0 : plan.rotation * 90);

		if (plan.worldContext && player != null && teamRegion != null && teamRegion.found()) {
			if (teamRegions[player.team().id] == teamRegion) Draw.color(player.team().color);
			Draw.rect(teamRegions[player.team().id], plan.drawx() + worldDrawOffset.x, plan.drawy() + worldDrawOffset.y);
			Draw.color();
		}

		drawPlanConfig(plan, list);
	}

	protected void setTile(Tile nearby, Block block, Building build) {
		Reflect.set(nearby, blockField, block);
		nearby.build = build;
		Events.fire(tileChangeEvent.set(nearby));
		tileChangeEvent.set(null);
	}

	public class CustomFormBuild extends Building {
		protected int[] subBuilding = emptySubBuildings;
		protected int prevRotation;

		@Override
		public void update() {
			updateRotation(super::update);
		}


		public void innerDraw() {
			Vec2 worldDrawOffset = worldDrawOffsets[rotation];
			if (variants != 0 && variantRegions != null) {
				Draw.rect(variantRegions[Mathf.randomSeed(pos(), 0, Math.max(0, variantRegions.length - 1))], x + worldDrawOffset.x, y + worldDrawOffset.y, drawrot());
			} else {
				Draw.rect(region, x + worldDrawOffset.x, y + worldDrawOffset.y, drawrot());
			}
			drawTeamTop();
		}

		@Override
		public void draw() {
			if (updateRotation(this::innerDraw)) {
				return;
			}
			Vec2 worldDrawOffset = worldDrawOffsets[rotation];
			Draw.draw(Layer.overlayUI, () -> {

				Draw.mixcol(Pal.breakInvalid, 0.4f + Mathf.absin(Time.globalTime, 6f, 0.28f));
				Draw.rect(region, x + worldDrawOffset.x, y + worldDrawOffset.y);
				Draw.mixcol();
				eachNearByValid(tile, this, Vars.player.team(), rotation, (dx, dy, bool) -> {
					if (!bool) Drawf.selected(dx, dy, block, Color.white);
				});
				Draw.mixcol();
			});

		}


		@Override
		public void remove() {
			boolean wasAdded = added;
			if (removingSubs) return;
			super.remove();
			if (wasAdded && !added) {
				removeSub();
			}
		}

		protected void removeSub() {
			if (removingSubs) return;
			removingSubs = true;
			for (int i : subBuilding) {
				Tile tile = Vars.world.tile(i);
				tile.remove();
				Events.fire(tileChangeEvent.set(tile));
				tileChangeEvent.set(null);
			}
			removingSubs = false;
			subBuilding = emptySubBuildings;
		}

		protected boolean updateRotation(Runnable callback) {
			if (prevRotation == rotation || checkNearByValid(tile, this, team, rotation)) {
				if (prevRotation != rotation) {
					removeSub();
					initSubBuilds();
				}
				prevRotation = rotation;
				callback.run();
				return true;
			}


			int tmpRotation = rotation;
			rotation = prevRotation;
			callback.run();
			rotation = tmpRotation;
			return false;
		}

		@Override
		public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
			Building init = super.init(tile, team, shouldAdd, rotation);
			prevRotation = rotation;
			if (!Vars.world.isGenerating()) {
				initSubBuilds();
			}
			return init;
		}

		protected void initSubBuilds() {
			Log.info("initSubBuilds @", tile);
			if (subBuilding != emptySubBuildings) {
				removeSub();
			}
			subBuilding = new int[customShape.otherBlocksAmount];
			int[] counter = {0};
			customShape.eachRelativeCenter(false, true, false, (dx, dy) -> {
				Tile nearby = tile.nearby(Tmp.p1.set(dx, dy).rotate(rotation));
				setTile(nearby, CustomShapeBlock.this, this);
				subBuilding[counter[0]] = nearby.pos();
				counter[0]++;
			});
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(prevRotation);
			write.i(subBuilding.length);
			for (int j : subBuilding) {
				write.i(j);
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			prevRotation = read.i();
			int size = read.i();
			if (subBuilding != emptySubBuildings) {
				removeSub();
			}
			subBuilding = new int[size];
			for (int i = 0; i < size; i++) {
				subBuilding[i] = read.i();
			}
			initSubBuilds();
		}
	}

	public void pixmap() {
		var atlasPixmap = Core.atlas.getPixmap(name + "-position");
		var pixmap = atlasPixmap.crop();
		var loader = new PixmapShapeLoader(Color.black, Color.white, Color.red);
		loader.load(pixmap);
		customShape = loader.toShape();
	}

	public void sprite() {
		var pixmap = Core.atlas.getPixmap(region).crop();
		var loader = new SpriteShapeLoader(32, new PercentProcessor(0.25f,
				pixmap.width / 64,
				pixmap.height / 64));
		loader.load(pixmap);
		customShape = loader.toShape();
	}

	public void string() {
		var loader = new StringShapeLoader('0', '1', '#');
		loader.load(
				"111100000",
				"111100000",
				"111100000",
				"111110000",
				"1111#1000",
				"011111111",
				"011111111",
				"001111111",
				"000011111"
		);
		customShape = loader.toShape();
	}
}
