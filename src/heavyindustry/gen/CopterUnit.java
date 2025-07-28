package heavyindustry.gen;

import arc.math.Mathf;
import arc.util.Time;
import heavyindustry.type.unit.CopterUnitType;
import heavyindustry.type.unit.CopterUnitType.Rotor;
import heavyindustry.type.unit.CopterUnitType.RotorMount;
import mindustry.gen.Call;
import mindustry.gen.Groups;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class CopterUnit extends BaseUnit implements Copterc {
	protected transient RotorMount[] rotors = {};
	protected transient float rotorSpeedScl = 1f;

	public CopterUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(CopterUnit.class);
	}

	@Override
	public void add() {
		if (added) return;
		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();

		team.data().updateCount(type, 1);

		//check if over unit cap
		if (type.useUnitCap && count() > cap() && !spawnedByCore && !dead && !state.rules.editor) {
			Call.unitCapDeath(this);
			team.data().updateCount(type, -1);
		}

		if (type instanceof CopterUnitType cType) {
			rotors = new RotorMount[cType.rotors.size];

			for (int i = 0; i < rotors.length; i++) {
				Rotor rotor = cType.rotors.get(i);
				rotors[i] = new RotorMount(rotor);
				rotors[i].rotorRot = rotor.rotOffset;
				rotors[i].rotorShadeRot = rotor.rotOffset;
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (type instanceof CopterUnitType cType) {
			if (dead || health < 0f) {
				if (!net.client() || isLocal()) rotation += cType.fallRotateSpeed * Mathf.signs[id % 2] * Time.delta;

				rotorSpeedScl = Mathf.lerpDelta(rotorSpeedScl, 0f, cType.rotorDeathSlowdown);
			} else {
				rotorSpeedScl = Mathf.lerpDelta(rotorSpeedScl, 1f, cType.rotorDeathSlowdown);
			}

			for (RotorMount rotor : rotors) {
				rotor.rotorRot += rotor.rotor.speed * rotorSpeedScl * Time.delta;
				rotor.rotorRot %= 360f;

				rotor.rotorShadeRot += rotor.rotor.shadeSpeed * Time.delta;
				rotor.rotorShadeRot %= 360f;
			}
		}
	}

	@Override
	public RotorMount[] rotors() {
		return rotors;
	}

	@Override
	public float rotorSpeedScl() {
		return rotorSpeedScl;
	}

	@Override
	public void rotors(RotorMount[] value) {
		rotors = value;
	}

	@Override
	public void rotorSpeedScl(float value) {
		rotorSpeedScl = value;
	}
}
