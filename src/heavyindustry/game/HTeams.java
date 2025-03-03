package heavyindustry.game;

import arc.graphics.Color;
import mindustry.game.Team;

public final class HTeams {
	/** Don't let anyone instantiate this class. */
	private HTeams() {}

	public static void load() {
		//TODO There are currently no teams to add.
	}

	public static Team newTeam(int id, String name, Color color) {
		Team team = Team.get(id);
		team.name = name;
		team.color.set(color);

		team.palette[0] = color;
		team.palette[1] = color.cpy().mul(0.75f);
		team.palette[2] = color.cpy().mul(0.5f);

		for (int i = 0; i < 3; i++) {
			team.palettei[i] = team.palette[i].rgba();
		}
		return team;
	}
}
