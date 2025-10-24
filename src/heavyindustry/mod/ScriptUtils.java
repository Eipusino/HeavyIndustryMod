package heavyindustry.mod;

import arc.files.Fi;
import arc.func.Func;
import arc.util.Log;
import heavyindustry.HVars;
import heavyindustry.util.ReflectUtils;
import mindustry.Vars;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import rhino.NativeJavaClass;
import rhino.NativeJavaPackage;
import rhino.Scriptable;
import rhino.Wrapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class ScriptUtils {
	/// Don't let anyone instantiate this class.
	private ScriptUtils() {}

	/// Initializes the Mod JS.
	public static void init() {
		try {
			if (Vars.mods.getScripts().scope instanceof ImporterTopLevel imp) {
				importPackages(imp, HVars.packages);
			}
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	/**
	 * Imports packages defined by this mod into the script scope.
	 *
	 * @param scope {@code Vars.mods.getScripts().scope} for the base game's script scope (mod scripts folder and console), or
	 *              another custom scope.
	 */
	public static void importPackages(ImporterTopLevel scope, String... packages) {
		for (String pack : packages) {
			importPackage(scope, pack);
		}
	}

	/**
	 * Imports a single package to the given scope.
	 *
	 * @param scope See {@link #importPackages(ImporterTopLevel, String...)}.
	 * @param name  The package's fully qualified name.
	 */
	public static void importPackage(ImporterTopLevel scope, String name) {
		if (scope == null) return;
		NativeJavaPackage pac = new NativeJavaPackage(name, Vars.mods.mainLoader());
		pac.setParentScope(scope);

		scope.importPackage(pac);
	}

	public static void importPackage(ImporterTopLevel scope, Package pack) {
		importPackage(scope, pack.getName());
	}

	public static void importClasses(ImporterTopLevel scope, String... classes) {
		for (String name : classes) {
			importClass(scope, name);
		}
	}

	public static void importClasses(ImporterTopLevel scope, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			importClass(scope, clazz);
		}
	}

	public static void importClass(ImporterTopLevel scope, String canonical) {
		try {
			importClass(scope, Class.forName(canonical));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void importClass(ImporterTopLevel scope, Class<?> type) {
		NativeJavaClass nat = new NativeJavaClass(scope, type);
		nat.setParentScope(scope);

		scope.importClass(nat);
	}

	public static Function compileFunc(Scriptable scope, String sourceName, String source) {
		return compileFunc(scope, sourceName, source, 1);
	}

	public static Function compileFunc(Scriptable scope, String sourceName, String source, int lineNum) {
		return Vars.mods.getScripts().context.compileFunction(scope, source, sourceName, lineNum);
	}

	@SuppressWarnings("unchecked")
	public static <T> Func<Object[], T> requireType(Function func, Context context, Scriptable scope, Class<T> returnType) {
		Class<?> type = ReflectUtils.box(returnType);
		return args -> {
			Object res = func.call(context, scope, scope, args);
			if (type == Void.class) return null;

			if (res instanceof Wrapper w) res = w.unwrap();
			if (!type.isAssignableFrom(res.getClass()))
				throw new IllegalArgumentException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
			return (T) res;
		};
	}

	/// see {@code SimpleClass.__javaObject__.getSimpleName()}
	public static <T> Class<T> c(Class<T> c) {
		return c;
	}

	public static NativeJavaClass nativeClass(Class<?> c) {
		return new NativeJavaClass(Vars.mods.getScripts().scope, c);
	}

	public static NativeJavaClass getClass(String name) throws ClassNotFoundException {
		return new NativeJavaClass(Vars.mods.getScripts().scope, Class.forName(name, true, Vars.mods.mainLoader()));
	}

	public static NativeJavaClass loadClass(String name) throws ClassNotFoundException, IOException {
		return loadClass(HVars.internalTree.file, name);
	}

	public static NativeJavaClass loadClass(Fi file, String name) throws ClassNotFoundException, IOException {
		try (URLClassLoader urlLoader = new URLClassLoader(new URL[]{file.file().toURI().toURL()})) {
			return new NativeJavaClass(Vars.mods.getScripts().scope, urlLoader.loadClass(name));
		}
	}

	public static boolean bool(boolean bool) {
		return bool;
	}

	public static byte b(byte b) {
		return b;
	}

	public static short s(short s) {
		return s;
	}

	public static int i(int i) {
		return i;
	}

	public static long l(long i) {
		return i;
	}

	public static float f(float f) {
		return f;
	}

	public static double d(double d) {
		return d;
	}

	public static char c(char c) {
		return c;
	}

	public static boolean[] bool(boolean[] bool) {
		return bool;
	}

	public static byte[] b(byte[] b) {
		return b;
	}

	public static short[] s(short[] s) {
		return s;
	}

	public static int[] i(int[] i) {
		return i;
	}

	public static long[] l(long[] i) {
		return i;
	}

	public static float[] f(float[] f) {
		return f;
	}

	public static double[] d(double[] d) {
		return d;
	}

	public static char[] c(char[] c) {
		return c;
	}
}
