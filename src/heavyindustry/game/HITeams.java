package heavyindustry.game;

import arc.graphics.*;
import mindustry.game.*;

public final class HITeams {
    /** Don't let anyone instantiate this class. */
    private HITeams() {}

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
