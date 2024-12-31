package heavyindustry.entities.abilities;

import arc.math.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;

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
}
