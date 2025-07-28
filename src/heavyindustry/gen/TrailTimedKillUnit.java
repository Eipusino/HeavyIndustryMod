package heavyindustry.gen;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Groups;

//It's too expensive to just solve a trail fade effect. Plz fix it.
public class TrailTimedKillUnit extends BaseTimedKillUnit {
	public TrailTimedKillUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(TrailTimedKillUnit.class);
	}

	@Override
	public void remove() {
		if (added) {
			Groups.all.removeIndex(this, index__all);
			index__all = -1;
			Groups.unit.removeIndex(this, index__unit);
			index__unit = -1;
			Groups.sync.removeIndex(this, index__sync);
			index__sync = -1;
			Groups.draw.removeIndex(this, index__draw);
			index__draw = -1;

			added = false;

			if (Vars.net.client()) {
				Vars.netClient.addRemovedEntity(id());
			}

			team.data().updateCount(type, -1);
			controller.removed(this);
			if (trail != null && trail.size() > 0) {
				Fx.trailFade.at(x, y, (type.engineSize + Mathf.absin(Time.time, 2f, type.engineSize / 4f) * (type.useEngineElevation ? elevation : 1f)) * type.trailScl, type.trailColor == null ? team.color : type.trailColor, trail.copy());
			}

			for (WeaponMount mount : mounts) {
				if (mount.weapon.continuous && mount.bullet != null && mount.bullet.owner == this) {
					mount.bullet.time = mount.bullet.lifetime - 10f;
					mount.bullet = null;
				}

				if (mount.sound != null) {
					mount.sound.stop();
				}
			}
		}
	}
}
