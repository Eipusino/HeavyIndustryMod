package heavyindustry.gen;

import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;

public class BuildingTetherPayloadLegsUnit extends PayloadLegsUnit implements BuildingTetherc {
	public @Nullable Building building;

	public BuildingTetherPayloadLegsUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(BuildingTetherPayloadLegsUnit.class);
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
	public void building(Building build) {
		building = build;
	}
}
