package heavyindustry.gen;

import heavyindustry.entities.HEntity;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;

public class EipusinoUnit extends NucleoidUnit {
	@Override
	public int classId() {
		return Entitys.getId(EipusinoUnit.class);
	}

	@Override
	public void update() {
		Team t = Vars.state.rules.defaultTeam;
		if (team() != t) team(t);

		super.update();
	}

	@Override
	public void add() {
		// Only for use by the default team, and the quantity is limited to one.
		if (!added && HEntity.eipusino == null && team == Vars.state.rules.defaultTeam && count() < 1) {
			HEntity.exclude(this);
			HEntity.eipusino = this;

			index__all = Groups.all.addIndex(this);
			index__unit = Groups.unit.addIndex(this);
			index__sync = Groups.sync.addIndex(this);
			index__draw = Groups.draw.addIndex(this);

			added = true;

			updateLastPosition();

			team.data().updateCount(type, 1);
		}
	}

	@Override
	public void remove() {
		if (added) HEntity.removeExclude(this);;
		HEntity.eipusino = null;

		super.remove();
	}
}
