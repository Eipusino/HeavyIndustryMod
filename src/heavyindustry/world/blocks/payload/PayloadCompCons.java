package heavyindustry.world.blocks.payload;

import mindustry.Vars;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.Constructor;

/**
 * constructor that can construct cores because for some reason vanilla constructors just Cant
 *
 * @author Nullotte
 * @since 1.0.6
 */
public class PayloadCompCons extends Constructor {
	public PayloadCompCons(String name) {
		super(name);
	}

	@Override
	public boolean canProduce(Block b) {
		return b.isVisible() && b.size >= minBlockSize && b.size <= maxBlockSize && !Vars.state.rules.isBanned(b) && b.environmentBuildable() && (filter.isEmpty() || filter.contains(b));
	}

	public class PayloadCompConsBuild extends ConstructorBuild {}//there's nothing to change...
}
