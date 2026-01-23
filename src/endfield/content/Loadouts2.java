package endfield.content;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import endfield.Vars2;
import mindustry.Vars;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class Loadouts2 {
	public static Schematic basicCripple;

	private Loadouts2() {}

	@Internal
	public static void load() {
		if (Vars.headless || Vars2.isPlugin) return;

		try {
			basicCripple = readBase64("bXNjaAF4nC3MQQqAIBRF0ZdEg2oHTdqAq2jcCqKB2YcE+4paINHeC+pOLxwIiBIlq53QDi5QPwTjvSXU2nEiTqPyENeNZqWo35eMYwCVVQvZCDHNBbqN1Jml4fWIKWSpX0jqHwIKfD2LRx4M");
			Vars.schematics.getLoadouts().get(Blocks2.coreShatter, () -> new Seq<>(Schematic.class)).add(basicCripple);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static Schematic readBase64(String schematic) throws IOException {
		return Schematics.read(new ByteArrayInputStream(Base64Coder.decode(schematic.trim())));
	}
}
