package endfield.mod;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.ArcRuntimeException;
import arc.util.serialization.Jval;
import endfield.util.Reflects;
import mindustry.mod.Mod;
import org.jetbrains.annotations.Nullable;

public class ModInfo {
	public final String name;
	public final String version;

	//public final float minGameVersion;

	public final Fi meta;
	public final Jval info;

	public final @Nullable Class<? extends Mod> main;

	public final Fi file;

	public ModInfo(Fi modFile) throws ArcRuntimeException {
		if (modFile instanceof ZipFi)
			throw new ArcRuntimeException("given file is a zip file object, please use file object");

		meta = ModGetter.getModFormat(modFile);
		info = Jval.read(meta.reader());
		file = modFile;
		name = info.get("name").asString();
		version = info.getString("version", "");

		main = Reflects.findClass(info.getString("main", null));
	}

	@Override
	public String toString() {
		return "ModInfo{" +
				"name=" + name +
				", version=" + version +
				'}';
	}
}
