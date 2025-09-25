package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType.Trigger;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.BlockRenderer;
import mindustry.graphics.Layer;
import mindustry.world.blocks.storage.CoreBlock;

public class CrashCore extends CoreBlock {
	public float crashHealth = -1f;

	public Sound explosionSound = Sounds.titanExplosion;
	public float explosionSoundPitchMin = 0.9f, explosionSoundPitchMax = 1.1f;
	public float explosionSoundVolume = 1f;

	public Effect landExplosion = HFx.missileExplosion;

	public TextureRegion squareShadow;

	static {
		Events.run(Trigger.newGame, () -> {
			if (Vars.player.bestCore() instanceof CrashCoreBuild c && c.block instanceof CrashCore b) {
				c.landed = false;

				// Since the thruster time is never set to 1, separately handle exploding when the animation is skipped.
				if (Core.settings.getBool("skipcoreanimation") || Vars.state.rules.pvp) {
					b.explode(c);
				}
			}
		});
	}

	public CrashCore(String name) {
		super(name);

		hasShadow = false;
	}

	@Override
	public void load() {
		super.load();
		squareShadow = Core.atlas.find("square-shadow");
	}

	@Override
	public void init() {
		super.init();

		if (crashHealth < 0) crashHealth = health / 10f;
	}

	@Override
	protected TextureRegion[] icons() {
		return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
	}

	public void explode(CrashCoreBuild core) {
		landExplosion.at(core);
		explosionSound.at(core.x, core.y, Mathf.random(explosionSoundPitchMin, explosionSoundPitchMax), explosionSoundVolume);
		core.health = crashHealth;
		core.landed = true;
		core.tile.getLinkedTiles(t -> {
			//0 ~ 360 -> -360/16 ~ 360/16
			float ang = core.angleTo(t.worldx(), t.worldy()) / 8f - 360f / 16f;
			for (int i = 0; i < 8; i++) {
				if (Mathf.chance(0.8f)) {
					Fx.coreLandDust.at(t.worldx(), t.worldy(), ang + Mathf.range(15f), Tmp.c1.set(t.floor().mapColor).mul(1.5f + Mathf.range(0.15f)));
				}
			}
		});
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CrashCoreBuild::new;
	}

	public class CrashCoreBuild extends CoreBuild {
		boolean landed = true;

		@Override
		public void draw() {
			if (!landed) return;

			Draw.z(Layer.block - 1);
			Draw.color(BlockRenderer.shadowColor);
			float rad = 1.6f;
			float size = block.size * Vars.tilesize;
			Draw.rect(squareShadow, x, y, size * rad * Draw.xscl, size * rad * Draw.yscl);
			Draw.color();
			Draw.z(Layer.block);

			super.draw();
		}

		@Override
		public void drawLight() {
			if (!landed) return;

			super.drawLight();
		}

		@Override
		public void updateTile() {
			if (thrusterTime == 1f) {
				explode(this);
			}

			super.updateTile();
		}

		@Override
		public void updateLaunch() {
			//Cancel landing particles
		}

		@Override
		public void drawLanding(float x, float y) {
			float fin = Vars.renderer.getLandTimeIn();
			fin = Mathf.curve(fin, 0.875f, 1f);
			float fout = 1f - fin;

			float scl = Scl.scl(4f) / Vars.renderer.getDisplayScale();
			float dst = 400f * fout;
			float ang = Mathf.randomSeed(id, 112.5f, 157.5f);
			Tmp.v1.trns(ang, dst);

			Draw.scl(scl);
			Draw.alpha(1f - fout);
			Draw.rect(region, x + Tmp.v1.x, y + Tmp.v1.y);

			if (teamRegions[team.id] == teamRegion) {
				Draw.color(team.color);
				Draw.alpha(1f - fout);
			}
			Draw.rect(teamRegions[team.id], x + Tmp.v1.x, y + Tmp.v1.y);

			Draw.color();
			Draw.scl();
			Draw.reset();
		}
	}
}
