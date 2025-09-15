package heavyindustry.gen;

import mindustry.Vars;
import mindustry.gen.Groups;

public class GuiYUnit extends NucleoidUnit {
	@Override
	public int classId() {
		return Entitys.getId(GuiYUnit.class);
	}

	@Override
	public void add() {
		// Only for use by the default team, and the quantity is limited to one.
		if (!added && team == Vars.state.rules.defaultTeam && count() < 1) {
			index__all = Groups.all.addIndex(this);
			index__unit = Groups.unit.addIndex(this);
			index__sync = Groups.sync.addIndex(this);
			index__draw = Groups.draw.addIndex(this);

			added = true;

			updateLastPosition();

			team.data().updateCount(type, 1);
		}
	}
}
