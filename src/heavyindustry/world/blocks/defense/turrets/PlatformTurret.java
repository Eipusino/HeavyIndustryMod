package heavyindustry.world.blocks.defense.turrets;

import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

public class PlatformTurret extends Turret {
    public BulletType shootType = Bullets.placeholder;

    public PlatformTurret(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));
    }

    public class PlatformTurretBuild extends TurretBuild {
        @Override
        public BulletType useAmmo() {
            return shootType;
        }

        @Override
        public boolean hasAmmo() {
            return true;
        }

        @Override
        public BulletType peekAmmo() {
            return shootType;
        }
    }
}
