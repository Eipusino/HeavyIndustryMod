package heavyindustry.content;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import heavyindustry.HVars;
import mindustry.Vars;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class HLoadouts {
	public static Schematic basicCripple;

	private HLoadouts() {}

	@Internal
	public static void load() {
		if (Vars.headless || HVars.isPlugin) return;

		try {
			basicCripple = readBase64("bXNjaAF4nC3MQQqAIBRF0ZdEg2oHTdqAq2jcCqKB2YcE+4paINHeC+pOLxwIiBIlq53QDi5QPwTjvSXU2nEiTqPyENeNZqWo35eMYwCVVQvZCDHNBbqN1Jml4fWIKWSpX0jqHwIKfD2LRx4M");
			Vars.schematics.getLoadouts().get(HBlocks.coreShatter, () -> new Seq<>(Schematic.class)).add(basicCripple);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static Schematic readBase64(String schematic) throws IOException {
		return Schematics.read(new ByteArrayInputStream(Base64Coder.decode(schematic.trim())));
	}
}
