package endfield.entities.abilities;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Unit;

public class SuspiciousAbility extends Ability {
	@Override
	public void update(Unit unit) {
		super.update(unit);
		if (Mathf.random() < 0.001) {
			Team t = unit.team();
			for (TeamData team : Vars.state.teams.active) {
				if (team.team != t) {
					unit.team(team.team);
					return;
				}
			}
		}
	}

	@Override
	public String getBundle() {
		return "ability.suspicious";
	}
}
