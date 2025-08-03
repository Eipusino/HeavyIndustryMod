package heavyindustry.gen;

import arc.util.Nullable;
import heavyindustry.world.blocks.defense.TargetDummyBase;
import mindustry.gen.Building;
import mindustry.gen.Call;

public class TargetDummyUnit extends BaseUnit {
	public @Nullable Building building;

	public Building building() {
		return building;
	}

	public void building(Building build) {
		building = build;
	}

	@Override
	public void update(){
		super.update();
		if(building == null || (!building.isPayload() && !building.isValid())){
			Call.unitDespawn(this); //Don't despawn even if the building is on another team
		}
	}

	@Override
	public void rawDamage(float amount){
		((TargetDummyBase.TargetDummyBaseBuild)building).dummyHit(amount);
	}
}
