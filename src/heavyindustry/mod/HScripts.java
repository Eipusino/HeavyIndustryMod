/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package heavyindustry.mod;

import arc.files.Fi;
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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class HScripts {
	/** Don't let anyone instantiate this class. */
	private HScripts() {}

	/** Initializes the Mod JS. */
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
		Class<?> type = Reflects.box(returnType);
		return args -> {
			Object res = func.call(context, scope, scope, args);
			if (type == Void.class) return null;

			if (res instanceof Wrapper w) res = w.unwrap();
			if (!type.isAssignableFrom(res.getClass()))
				throw new IllegalArgumentException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
			return (T) res;
		};
	}

	// SimpleClass.__javaObject__.getSimpleName()
	public static <T> Class<T> c(Class<T> c) {
		return c;
	}

	public static NativeJavaClass loadClass(Fi file, String name) throws ClassNotFoundException, IOException {
		try (URLClassLoader urlLoader = new URLClassLoader(new URL[]{file.file().toURI().toURL()})) {
			return new NativeJavaClass(Vars.mods.getScripts().scope, urlLoader.loadClass(name));
		}
	}
}
