package heavyindustry.gen;

import arc.math.*;
import arc.util.*;
import heavyindustry.type.unit.*;
import heavyindustry.type.unit.OrnitopterUnitType.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

public class OrnitopterUnit extends Unitf implements Ornitopterc {
    public BladeMount[] blades;
    public float bladeMoveSpeedScl = 1f;
    public long drawSeed = 0;

    protected float driftAngle;
    protected boolean hasDriftAngle = false;

    protected OrnitopterUnit() {}

    @Override
    public int classId() {
        return EntityRegister.getId(OrnitopterUnit.class);
    }

    @Override
    public void afterRead() {
        super.afterRead();
        setBlades(type);
    }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        setBlades(type);
    }

    public void setBlades(UnitType type) {
        if (type instanceof OrnitopterUnitType oType) {
            blades = new BladeMount[oType.blades.size];
            for (int i = 0; i < blades.length; i++) {
                Blade bladeType = oType.blades.get(i);
                blades[i] = new BladeMount(bladeType);
            }
        }
    }

    public float driftAngle() {
        return driftAngle;
    }

    @Override
    public void update() {
        super.update();
        drawSeed++;
        if (type instanceof OrnitopterUnitType oType) {
            float rX = x + Angles.trnsx(rotation - 90, oType.fallSmokeX, oType.fallSmokeY);
            float rY = y + Angles.trnsy(rotation - 90, oType.fallSmokeX, oType.fallSmokeY);

            // When dying
            if (dead || health() <= 0) {
                if (Mathf.chanceDelta(oType.fallSmokeChance)) {
                    Fx.fallSmoke.at(rX, rY);
                    Fx.burning.at(rX, rY);
                }

                // Compute random drift angle if not already set
                if (!hasDriftAngle) {
                    float speed = Math.max(Math.abs(vel().x), Math.abs(vel().y));
                    float maxAngle = Math.min(180f, speed * oType.fallDriftScl); // Maximum drift angle based on speed
                    driftAngle = (Angles.angle(x, y, x + vel().x, y + vel().y) + Mathf.range(maxAngle)) % 360f;
                    hasDriftAngle = true;
                }

                // Drift in random direction
                float driftSpeed = Math.max(0f, vel().len() - type().drag) * oType.accel;
                float driftX = driftSpeed * Mathf.cosDeg(driftAngle);
                float driftY = driftSpeed * Mathf.sinDeg(driftAngle);
                move(driftX, driftY);

                rotation = Mathf.lerpDelta(rotation, driftAngle, 0.01f);

                bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 0f, oType.bladeDeathMoveSlowdown);
            } else {
                hasDriftAngle = false; // Reset the drift angle flag
                bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 1f, oType.bladeDeathMoveSlowdown);
            }

            for (BladeMount blade : blades) {
                blade.bladeRotation += ((blade.blade.bladeMaxMoveAngle * bladeMoveSpeedScl) + blade.blade.bladeMinMoveAngle) * Time.delta;
            }
            oType.fallSpeed = 0.01f;
        }
    }

    @Override
    public float bladeMoveSpeedScl() {
        return bladeMoveSpeedScl;
    }

    @Override
    public long drawSeed() {
        return drawSeed;
    }

    @Override
    public BladeMount[] blades() {
        return blades;
    }

    @Override
    public void bladeMoveSpeedScl(float value) {
        bladeMoveSpeedScl = value;
    }

    @Override
    public void blades(BladeMount[] value) {
        blades = value;
    }

    @Override
    public void drawSeed(long value) {
        drawSeed = value;
    }

    public static OrnitopterUnit create() {
        return new OrnitopterUnit();
    }
}
