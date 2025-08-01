package heavyindustry.game;

import arc.graphics.Color;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;

public final class HTeam {
	public static final Team slaanesh, nurgle, khorne, tzeentch, none;

	static {
		slaanesh = Team.get(6);
		nurgle = Team.get(7);
		khorne = Team.get(8);
		tzeentch = Team.get(9);
		none = Team.get(255);
	}

	/** Don't let anyone instantiate this class. */
	private HTeam() {}

	public static void load() {
		team(slaanesh.id, "slaanesh", Color.purple);
		team(nurgle.id, "nurgle", Color.green);
		team(khorne.id, "khorne", Color.red);
		team(tzeentch.id, "tzeentch", Color.blue);
		team(none.id, "none", Color.clear);
	}

	@Deprecated
	public static void team(Building build, Team team) {
		build.team(team);
	}

	public static void team(Unit unit, Team team) {
		if (unit.team != team) {
			unit.remove();
			unit.team(team);
			if (unit.isPlayer()) unit.getPlayer().team(team);
			unit.add();
		}
	}

	public static Team team(int id, String name, Color color) {
		Team team = Team.get(id);
		team.name = name;
		team.color.set(color);

		return team;
	}
}
