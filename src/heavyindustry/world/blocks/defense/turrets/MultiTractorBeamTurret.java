package heavyindustry.world.blocks.defense.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

import static arc.Core.*;

public class MultiTractorBeamTurret extends TractorBeamTurret {
	protected static final float OFFSET = 12f;

	public int maxAttract = 5;
	public StatusEffect status = StatusEffects.slow;
	public float statusDuration = 10f;

	public MultiTractorBeamTurret(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.shots, maxAttract);
		if (status != null && status != StatusEffects.none && !status.isHidden()) stats.add(Stat.abilities, table -> {
			table.row().table().padLeft(OFFSET * 2).getTable().table(t -> {
				t.align(Align.topLeft);
				t.table(info -> {
					info.left();
					info.add("[lightgray]" + bundle.get("content.status.name") + ": ").padRight(OFFSET);
					info.button(new TextureRegionDrawable(status.uiIcon), Styles.cleari, () -> {
						new ContentInfoDialog().show(status);
					}).scaling(Scaling.fit);
				}).fill().row();
				t.add("Duration: " + statusDuration / 60f + ".sec").growX().fillY();
			}).fill().padBottom(OFFSET).left().row();
		});
	}

	@Override
	public void load() {
		super.load();
		laser = atlas.find("parallax-laser");
		laserEnd = atlas.find("parallax-laser-end");
	}

	public class MultiTractorBeamBuild extends TractorBeamBuild {
		/**
		 * {@link ObjectMap} {@code targets} Uses a {@link Vec3} to contain:
		 * <li>.x -> {@code lastX}
		 * <li>.y -> {@code lastY}
		 * <li>.z -> {@code strength}
		 */
		public final ObjectMap<Unit, Vec3> targets = new ObjectMap<>(maxAttract);

		@Override
		public void updateTile() {
			super.updateTile();
			for (Unit unit : targets.keys()) {
				if (unit != null && Angles.within(rotation, angleTo(unit), shootCone) && within(unit, range + unit.hitSize / 2f) && unit.team() != team && unit.isValid() && unit.checkTarget(targetAir, targetGround)) {
					targets.get(unit).x = unit.x;
					targets.get(unit).y = unit.y;
					targets.get(unit).z = Mathf.lerpDelta(targets.get(unit).z, 1f, 0.1f);
					if (unit != target) {
						if (damage > 0) unit.damageContinuous(damage * efficiency());
						if (status != StatusEffects.none) unit.apply(status, statusDuration);
						unit.impulseNet(Tmp.v1.set(this).sub(unit).limit((force + (1f - unit.dst(this) / range) * scaledForce) * edelta() * timeScale));
					}
				} else {
					Vec3 v = targets.get(unit);
					if (v == null) continue;
					v.z = Mathf.lerpDelta(v.z, 0, 0.1f);
					if (Mathf.equal(targets.get(unit).z, 0, 0.001f)) targets.remove(unit);
				}
			}

			if (target != null && target.within(this, range + target.hitSize / 2f) && target.team() != team && target.checkTarget(targetAir, targetGround) && efficiency() > 0.02f) {
				Units.nearbyEnemies(team, Tmp.r1.setSize((range + target.hitSize / 2f) * 2).setCenter(x, y), unit -> {
					if (targets.size < maxAttract && !targets.keys().toSeq().contains(unit) && Angles.within(rotation, angleTo(unit), shootCone)) {
						targets.put(unit, new Vec3(unit.x, unit.y, 0f));
					}
				});
			}
		}

		@Override
		public void draw() {
			Draw.rect(baseRegion, x, y);
			Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
			Draw.rect(region, x, y, rotation - 90);
			Draw.z(Layer.bullet);
			//draw laser if applicable
			for (Unit unit : targets.keys()) {
				if (unit == null) continue;
				float ang = angleTo(targets.get(unit).x, targets.get(unit).y);
				Draw.mixcol();
				Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));
				Tmp.v1.trns(rotation, shootLength).add(x, y);
				Drawf.laser(laser, laserEnd, Tmp.v1.x, Tmp.v1.y, targets.get(unit).x, targets.get(unit).y, targets.get(unit).z * efficiency() * laserWidth);
			}
		}
	}
}
