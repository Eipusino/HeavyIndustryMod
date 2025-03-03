package heavyindustry.graphics.g3d.model.obj.mtl;

import arc.files.Fi;
import arc.struct.Seq;
import heavyindustry.graphics.g3d.model.obj.ObjectModelFactory;

import java.io.BufferedReader;
import java.io.IOException;

/** For models parsing use {@link ObjectModelFactory}. */
public class MTLParser {

	/** @param file file for parsing. */
	public static Seq<MTL> parse(Fi file) {
		Seq<MTL> out = new Seq<>();
		MTL currentMTL = null;

		try (BufferedReader lines = file.reader(1 << 13)) {
			String line;
			while ((line = lines.readLine()) != null) {
				int spaceIndex = line.indexOf(' ');
				if (line.matches("[#\\s].+") || spaceIndex == -1)
					continue;
				if (line.startsWith("newmtl ")) {
					currentMTL = new MTL(file, line.substring("newmtl ".length()));
					out.add(currentMTL);
				} else {
					String k = line.substring(0, spaceIndex);
					String v = line.substring(spaceIndex + 1);
					currentMTL.set(k, v);
				}
			}
			return out;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
