package endfield.util;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import dynamilize.IllegalHandleException;
import endfield.mod.ModGetter;
import endfield.mod.ModInfo;
import mindustry.Vars;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static endfield.Vars2.MOD_NAME;

public class JarList {
	public static final Fi jarFileCache = Core.settings.getDataDirectory().child("tmp").child("endfield").child("cache");

	static final Fi listFile = jarFileCache.child("mod-generated-jars.lis");

	static final HashMap<String, InfoEntry> list = new HashMap<>();

	static boolean selfUpdate, recached;

	public static void init() {
		jarFileCache.mkdirs();
		if (!listFile.exists()) return;
		BufferedReader reader = new BufferedReader(listFile.reader());
		String line;
		InfoEntry entry;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				entry = InfoEntry.infoOf(line);
				list.put(entry.name, entry);
			}
		} catch (Exception ignored) {
			Log.info("[ENDFIELD] failed to load mod generated jars cache, regenerate all generate cache");
			list.clear();
			writeToList();
		}
	}

	public static String getMd5(Fi file) {
		MessageDigest md;
		byte[] buffer = new byte[8192];
		try (InputStream input = new FileInputStream(file.file())) {
			md = MessageDigest.getInstance("MD5");
			int data;
			while ((data = input.read(buffer)) != -1) {
				md.update(buffer, 0, data);
			}
			return new BigInteger(1, md.digest()).toString(16);
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static void update(ModInfo mod) {
		InfoEntry entry = list.get(mod.name);
		if (entry == null)
			throw new RuntimeException("unknown mod " + mod.name);

		entry.md5 = getMd5(mod.file);
	}

	public static boolean matched(ModInfo mod) {
		String md5code = getMd5(mod.file);
		InfoEntry entry = list.get(mod.name);
		if (entry == null) return false;
		if (!mod.version.equals(entry.version)) return false;
		return md5code.equals(entry.getMd5());
	}

	public static Fi loadCacheFile(ModInfo mod) {
		if (!selfUpdate && mod.name.equals(MOD_NAME)) {
			if (matched(mod)) {
				for (Fi fi : Vars.modDirectory.list()) {
					try {
						ModGetter.getModFormat(fi);
						ModInfo info = new ModInfo(fi);

						if (!info.name.equals(MOD_NAME) && !matched(info)) {
							selfUpdate = true;
							Log.info("[ENDFIELD] exist mod updated, universe core class cache updating");

							break;
						}
					} catch (IllegalHandleException ignored) {}
				}
			} else {
				selfUpdate = true;
				Log.info("[ENDFIELD] endfield updated, all cache will be regenerated");
				list.clear();
				writeToList();
			}
		}

		InfoEntry entry = list.get(mod.name);
		if (entry == null) {
			if (mod.name.equals(MOD_NAME)) recached = true;

			Log.info("[ENDFIELD] new mod: " + mod.name + " installed, to generate class cache");
			entry = new InfoEntry();
			entry.name = mod.name;
			entry.version = mod.version;
			entry.file = jarFileCache.child("generated-" + mod.name + ".jar");
			entry.file.delete();
			entry.md5 = getMd5(mod.file);
			list.put(mod.name, entry);
			writeToList();
		} else if ((selfUpdate && mod.name.equals(MOD_NAME) && !recached) || !matched(mod)) {
			if (mod.name.equals(MOD_NAME)) recached = true;

			Log.info("[ENDFIELD] source mod: " + mod.name + " is updated, regenerate class cache");
			entry.version = mod.version;
			entry.file.delete();
			entry.md5 = getMd5(mod.file);
			writeToList();
		} else {
			Log.info("[ENDFIELD] loading mod: " + mod.name + " class cache");
		}

		return entry.file;
	}

	public static void deleteCacheFile(ModInfo mod) {
		InfoEntry entry = list.get(mod.name);
		if (entry == null) return;
		entry.file.delete();
		list.remove(mod.name);
		writeToList();
	}

	static void writeToList() {
		try (BufferedWriter writer = new BufferedWriter(listFile.writer(false))) {
			for (InfoEntry entry : list.values()) {
				writer.write(entry.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class InfoEntry {
		String name;
		String version;
		String md5;
		Fi file;

		public static InfoEntry infoOf(String str) {
			InfoEntry result = new InfoEntry();
			StringBuilder keyBuffer = new StringBuilder();
			StringBuilder valueBuffer = new StringBuilder();

			int character;
			try (BufferedReader reader = new BufferedReader(new StringReader(str))) {
				String c;
				String key;
				String value;
				boolean inValue = false;
				while ((character = reader.read()) != -1) {
					c = Character.toString((char) character);
					if (c.equals(";")) {
						if (!inValue) throw new IllegalArgumentException("unexpected \";\"");
						key = keyBuffer.toString();
						value = valueBuffer.toString();

						switch (key) {
							case "name" -> result.name = value;
							case "version" -> result.version = value;
							case "MD5" -> result.md5 = value;
							case "file" -> result.file = new Fi(jarFileCache.path()).child(value);
							default -> throw new IllegalArgumentException("unknown key: " + key);
						}

						inValue = false;
						keyBuffer = new StringBuilder();
						valueBuffer = new StringBuilder();
					} else if (c.equals("=")) {
						inValue = true;
					} else {
						if (inValue) {
							valueBuffer.append(c);
						} else keyBuffer.append(c);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return result;
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		public String getMd5() {
			return md5;
		}

		public Fi getFile() {
			return file;
		}

		public String toString() {
			return "name=" + name + ";version=" + version + ";MD5=" + getMd5() + ";file=" + file.name() + ";";
		}
	}
}
