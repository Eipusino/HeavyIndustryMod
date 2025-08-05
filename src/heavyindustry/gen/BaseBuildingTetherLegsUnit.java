package heavyindustry.gen;

import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.io.TypeIO;

public class BaseBuildingTetherLegsUnit extends BaseLegsUnit implements BuildingTetherc {
	public @Nullable Building building;

	@Override
	public int classId() {
		return Entitys.getId(BaseBuildingTetherLegsUnit.class);
	}

	@Override
	public void update() {
		super.update();

		if (building == null || !building.isValid() || building.team != team) {
			Call.unitDespawn(this);
		}
	}

	@Override
	public Building building() {
		return building;
	}

	@Override
	public void building(@Nullable Building value) {
		building = value;
	}

	@Override
	public void read(Reads read) {
		building = TypeIO.readBuilding(read);

		super.read(read);
	}

	@Override
	public void write(Writes write) {
		TypeIO.writeBuilding(write, building);

		super.write(write);
	}

	@Override
	public void readSync(Reads read) {
		building = TypeIO.readBuilding(read);

		super.readSync(read);
	}

	@Override
	public void writeSync(Writes write) {
		TypeIO.writeBuilding(write, building);

		super.writeSync(write);
	}
}
