package heavyindustry.type.weapons;

import arc.scene.ui.layout.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class HealConeWeapon extends BoostWeapon {
    public boolean targetBuildings = true;

    public HealConeWeapon() {
        this("");
    }

    public HealConeWeapon(String name) {
        super(name);
        shootSound = Sounds.pulse;
        cooldownTime = 180;
        shoot.firstShotDelay = 60;
        continuous = true;
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);
        t.row();
        int healTime = (int) (100 / bullet.healPercent);
        int reloadNeed = (int) (healTime / (bullet.lifetime / 60));
        int reloadRest = (int) (reloadNeed * reload / 60);
        t.add("[lightgray]" + Stat.repairTime.localized() + ": " + "[white]" + (1.5f * (healTime + reloadRest) / (mirror ? 2 : 1)) + " " + StatUnit.seconds.localized());
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        Unit out = Units.closest(unit.team, x, y, range, u -> u != unit && u.damaged());
        if (out != null || !targetBuildings) return out;
        return Units.findAllyTile(unit.team, x, y, range, Building::damaged);
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        return !(target.within(unit, range + unit.hitSize / 2) && target.team() == unit.team && (target instanceof Healthc && ((Healthc) target).damaged() && ((Healthc) target).isValid()));
    }
}
