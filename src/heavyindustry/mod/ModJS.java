package heavyindustry.mod;

import arc.func.Func;
import arc.util.Log;
import heavyindustry.HVars;
import heavyindustry.util.Reflects;
import mindustry.Vars;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import rhino.NativeJavaClass;
import rhino.NativeJavaPackage;
import rhino.Scriptable;
import rhino.Wrapper;

import java.net.URL;
import java.net.URLClassLoader;

import static heavyindustry.HVars.packages;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class ModJS {
	/** Don't let anyone instantiate this class. */
	private ModJS() {}

	/** Initializes the Mod JS. Main-thread only! */
	public static void init() {
		try {//declare heavy-industry java script APIs
			importDefaults((ImporterTopLevel) Vars.mods.getScripts().scope);
		} catch (Exception e) {
			Log.err("js initial error, stack trace", e);
		}
	}

	/**
	 * Imports packages defined by this mod into the script scope.
	 * @param scope {@code Vars.mods.getScripts().scope} for the base game's script scope (mod scripts folder and console), or
	 *			  another custom scope.
	 */
	public static void importDefaults(ImporterTopLevel scope) {
		for (String pack : packages) importPackage(scope, pack);
	}

	/**
	 * Imports a single package to the given scope.
	 * @param scope See {@link #importDefaults(ImporterTopLevel)}.
	 * @param name  The package's fully qualified name.
	 */
	public static void importPackage(ImporterTopLevel scope, String name) {
		if (scope == null) return;
		var pac = new NativeJavaPackage(name, Vars.mods.mainLoader());
		pac.setParentScope(scope);

		scope.importPackage(pac);
	}

	public static void importPackage(ImporterTopLevel scope, Package pack) {
		importPackage(scope, pack.getName());
	}

	public static void importClass(ImporterTopLevel scope, String canonical) {
		importClass(scope, Reflects.mainClass(canonical));
	}

	public static void importClass(ImporterTopLevel scope, Class<?> type) {
		var nat = new NativeJavaClass(scope, type);
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
		Class<?> type = Reflects.box(returnType);
		return args -> {
			Object res = func.call(context, scope, scope, args);
			if (type == void.class || type == Void.class) return null;

			if (res instanceof Wrapper w) res = w.unwrap();
			if (!type.isAssignableFrom(res.getClass()))
				throw new IllegalStateException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
			return (T) res;
		};
	}

	public static NativeJavaClass getClass(String name) {
		try {
			return new NativeJavaClass(Vars.mods.getScripts().scope, Class.forName(name, true, Vars.mods.mainLoader()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static NativeJavaClass loadClass(String name) {
		try (URLClassLoader urlLoader = new URLClassLoader(new URL[]{HVars.internalTree.file.file().toURI().toURL()})) {
			return new NativeJavaClass(Vars.mods.getScripts().scope, urlLoader.loadClass(name));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
