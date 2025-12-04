package heavyindustry.mod;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.serialization.Jval;

public class ModInfo {
	public final String name;
	public final String version;

	//public final String main;

	public final Fi file;

	public ModInfo(Fi modFile) {
		if (modFile instanceof ZipFi)
			throw new IllegalArgumentException("given file is a zip file object, please use file object");
		Fi modMeta;
		modMeta = ModGetter.checkModFormat(modFile);
		Jval info = Jval.read(modMeta.reader());
		file = modFile;
		name = info.get("name").asString();
		version = info.get("version").asString();
		//main = info.getString("main");
	}
}
