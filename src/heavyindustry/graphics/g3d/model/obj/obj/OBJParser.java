package heavyindustry.graphics.g3d.model.obj.obj;

import arc.files.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.graphics.g3d.model.obj.*;

import java.io.*;

/** For models parsing use {@link ObjectModelFactory}. */
public class OBJParser {
	static Seq<Vec3> vs = new Seq<>(), vns = new Seq<>();
	static Seq<Vec2> vts = new Seq<>();
	static IntSeq facesIndexes = new IntSeq();
	static FloatSeq vertices = new FloatSeq();

	/** @param file file for parsing */
	public static Seq<OBJ> parse(Fi file) {
		Seq<OBJ> out = new Seq<>();
		OBJ currentObj = null;
		Fi mtlLib = null;


		int faces = 0;


		try (BufferedReader lines = file.reader(1 << 13)) {
			String line;
			while ((line = lines.readLine()) != null) {
				String[] strings = line.split(" ");
				String key = strings[0];
				String[] args = new String[strings.length - 1];
				System.arraycopy(strings, 1, args, 0, args.length);

				switch (key) {
					case "mtllib" -> mtlLib = file.parent().child(args[0]);
					case "o" -> {
						finishObject(out, currentObj);
						currentObj = new OBJ(file);
						currentObj.name = args[0];
						currentObj.mtlFile = mtlLib;
					}
					case "v" -> {
						float[] vert = getFloats(args);
						addOrGet(vs, Vec3::new).set(vert);
					}
					case "vt" -> {
						float[] vert = getFloats(args);
						addOrGet(vts, Vec2::new).set(vert[0], vert[1]);
					}
					case "vn" -> {
						float[] vert = getFloats(args);
						addOrGet(vns, Vec3::new).set(vert);
					}
					case "usemtl" -> currentObj.mtlName = args[0];
					case "f" -> {
						faces++;
						for (String str : args) {
							parseIntegers(facesIndexes, str, "/");
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		finishObject(out, currentObj);

		if (currentObj == null) {
			Log.warn("You fucking loser! Your .obj file corrupted!");
			reset();
		}

		return out;
	}

	private static <T> T addOrGet(Seq<T> vs, Prov<T> constructor) {
		if (vs.items.length == vs.size + 1) {
			vs.add(constructor.get());

		}
		for (int i = 0; i < vs.items.length; i++) {
			if (vs.items[i] == null) {
				vs.items[i] = constructor.get();
			}
		}
		vs.size++;
		return vs.peek();
	}

	private static void finishObject(Seq<OBJ> out, OBJ currentObj) {
		if (currentObj != null) {
			out.add(currentObj);
			constructObj(currentObj);
			// reset all sequences
			reset();
		}
	}

	private static void reset() {
		facesIndexes.clear();
		vs.size = 0;
		vts.size = 0;
		vns.size = 0;
		vertices.clear();
	}

	/** Parse floats from list of strings. */
	private static float[] getFloats(String[] args) {
		float[] floats = new float[args.length];
		for (int i = 0; i < floats.length; i++) {
			floats[i] = Float.parseFloat(args[i]);
		}
		return floats;
	}

	/** Constructs object's vertices. */
	static void constructObj(OBJ obj) {
		vertices.clear();
		for (int i = 0; i < facesIndexes.size; i += 3) {
			Vec3 v = vs.get(facesIndexes.get(i) - 1);
			Vec2 vt = vts.get(facesIndexes.get(i + 1) - 1);
			Vec3 vn = vns.get(facesIndexes.get(i + 2) - 1);

			vertices.addAll(
					v.x, v.y, v.z,
					vt.x, vt.y,
					vn.x, vn.y, vn.z
			);
		}
		obj.vertices = vertices.toArray();
	}


	/** Parse integers from list of strings. */
	public static IntSeq toIntegers(IntSeq out, String... strings) {
		for (String string : strings) {
			out.add(Integer.parseInt(string));
		}
		return out;
	}

	/** Parse integers from string. */
	public static IntSeq parseIntegers(IntSeq out, String v, String sep) {
		return toIntegers(out, v.split(sep));
	}
}
