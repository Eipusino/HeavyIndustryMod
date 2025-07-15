package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.blocks.environment.UndergroundOreBlock;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static heavyindustry.struct.Collectionsf.arrayOf;
import static mindustry.Vars.player;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.ui;
import static mindustry.Vars.world;

/**
 * A class for cores that have a functionality of scanning underground ores.
 * (We don't talk about Core: Colony here).
 */
public class DetectorCoreBlock extends CoreBlock {
	/** Ore detector radius, in world units. */
	public float radarRange = 25f * 8f;
	/** The active cone width of the radar, in degrees. */
	public float radarCone = 18f;
	/** Radar location speed, in degrees per tick. */
	public float speed = 0.3f;
	/** Effect color. */
	public Color effectColor = Color.valueOf("4b95ff");

	/** Player respawn cooldown. */
	public float spawnCooldown = 5f * 60f;

	public DetectorCoreBlock(String name) {
		super(name);
		configurable = true;
		clipSize = radarRange * 2f;
		//loopSound = HSounds.radar;
	}

	@Override
	protected TextureRegion[] icons() {
		return teamRegion.found() ? arrayOf(region, teamRegions[Team.sharded.id]) : arrayOf(region);
	}

	public class DetectorCoreBuild extends CoreBuild implements Ranged {
		public float timer = 0f, startTime;
		public boolean showOres = true, requested = false;
		public Seq<Tile> detectedOres;

		@Override
		public float range() {
			return radarRange;
		}

		@Override
		public void created() {
			super.created();

			startTime = Time.time;
			detectedOres = new Seq<>();
		}

		protected TextureRegionDrawable eyeIcon() {
			return showOres ? Icon.eyeSmall : Icon.eyeOffSmall;
		}

		@Override
		public void requestSpawn(Player player) {
			//spawn cooldown
			if (!requested) {
				boolean immediate = state.isEditor() || state.rules.infiniteResources;
				timer = immediate ? 0f : spawnCooldown;
				requested = true;
				Time.run(timer, () -> {
					if (player.dead()) {
						super.requestSpawn(player);
						Call.soundAt(Sounds.respawn, x, y, 1, 1);
					}
					requested = false;
				});
			}
		}

		@Override
		public void updateTile() {
			super.updateTile();

			if (timer > 0) timer -= Time.delta;
		}

		@Override
		public void buildConfiguration(Table table) {
			table.button(eyeIcon(), Styles.clearTogglei, () -> {
				showOres = !showOres;
				startTime = Time.time; //reset the timer to fix sound loop
				configure(showOres);
				deselect();
			}).size(40);
		}

		@Override
		public void draw() {
			super.draw();
			if (canConsume() && team == player.team()) {
				Draw.z(Layer.light);
				Draw.alpha(0.6f);
				Lines.stroke(2.5f, effectColor);

				if (showOres) {
					Draw.alpha(1f - (curTime() % 120f) / 120f);
					Lines.circle(x, y, (curTime() % 120f) / 120f * range());

					Draw.alpha(0.3f);
					Fill.arc(x, y, range(), radarCone / 360f, radarRot());
				}

				Draw.alpha(0.2f);
				Lines.circle(x, y, range());
				Lines.circle(x, y, range() * 0.95f);

				Draw.reset();
				if (showOres) locateOres(range());
			}

			if (timer > 0) {
				ui.showLabel(String.valueOf(Mathf.ceil(timer / 60f)), 1f / 60f, x, y + 16f);

				Draw.z(Layer.overlayUI);
				Draw.color(Pal.gray);
				Draw.rect("empty", x, y, 45f);

				Draw.color();

				float progress = 1 - timer / spawnCooldown;
				Draw.draw(Layer.blockOver, () -> Drawf.construct(this, unitType, 0f, progress, 1f, progress * 300f));

				Drawf.square(x, y, 6f);
			}
		}

		/*@Override
		public boolean shouldActiveSound() {
			return canConsume() && showOres;
		}*/

		public float radarRot() {
			return (curTime() * speed) % 360f;
		}

		public float curTime() {
			return Time.time - startTime;
		}

		public void locateOres(float radius) {
			Tile hoverTile = world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);

			tile.circle((int) (radius / tilesize), (ore) -> {
				if (ore != null && ore.overlay() != null && ore.overlay() instanceof UndergroundOreBlock) {
					var angle = Mathf.angle(ore.x - tile.x, ore.y - tile.y);
					var c1 = radarRot();
					var c2 = radarRot() + radarCone;
					if (c2 >= 360f && angle < 180f) {
						angle += 360;
					}

					if (angle >= c1 && angle <= c2 && !detectedOres.contains(ore)) {
						detectedOres.add(ore);
					}
				}
			});

			for (var ore : detectedOres) {
				if (ore.block() != Blocks.air || !(ore.overlay() instanceof UndergroundOreBlock u)) continue;

				u.shouldDrawBase = true;
				u.drawBase(ore);
				u.shouldDrawBase = false;

				//show an item icon above the cursor/finger
				if (ore == hoverTile && ore.block() != null) {
					Draw.z(Layer.max);
					Draw.alpha(1f);
					Draw.rect(u.drop.uiIcon, ore.x * 8, ore.y * 8 + 8);
				}
			}
		}

		@Override
		public void write(Writes write) {
			write.bool(showOres);
		}

		@Override
		public void read(Reads read, byte revision) {
			showOres = read.bool();
		}
	}
}
