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
package endfield.util.script;

import arc.files.Fi;
import arc.func.Func;
import arc.util.Log;
import dynamilize.FunctionType;
import endfield.Vars2;
import endfield.util.Reflects;
import mindustry.Vars;
import mindustry.mod.Scripts;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import rhino.JavaAdapter;
import rhino.NativeArray;
import rhino.NativeJavaClass;
import rhino.NativeJavaPackage;
import rhino.Scriptable;
import rhino.Wrapper;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;

import static arc.Core.files;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class Scripts2 {
	public static Scripts scripts;
	public static ImporterTopLevel scope;
	public static Context context;

	/** Don't let anyone instantiate this class. */
	private Scripts2() {}

	/** Initializes the Mod JS. */
	@Internal
	public static void init() {
		try {
			scripts = Vars.mods.getScripts();
			scope = (ImporterTopLevel) scripts.scope;
			context = scripts.context;

			importPackages(scope, Vars2.packages);

			/*context.evaluateString(scope, """
					function apply(map, object) {
						for (let key in object) {
							map.put(key, object[key]);
						}
					}

					function getClass(name) {
						return Packages.java.lang.Class.forName(name, true, Vars.mods.mainLoader());
					}

					function jbyte(value) { return Packages.java.lang.Byte.valueOf(value); }
					function jshort(value) { return Packages.java.lang.Short.valueOf(value); }
					function jint(value) { return Packages.java.lang.Integer.valueOf(value); }
					function jlong(value) { return Packages.java.lang.Long.valueOf(value); }
					function jfloat(value) { return Packages.java.lang.Float.valueOf(value); }
					function jdouble(value) { return Packages.java.lang.Double.valueOf(value); }
					""", "apply.js", 1);*/
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	@Contract(value = " -> new", pure = true)
	public static ImporterTopLevel newScope() {
		Context context = Vars.mods.getScripts().context;
		ImporterTopLevel scope = new ImporterTopLevel(context);
		context.evaluateString(scope, files.internal("scripts/global.js").readString(), "global.js", 1);
		return scope;
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
	@Contract(pure = true)
	public static <T> Func<Object[], T> requireType(Function func, Context context, Scriptable scope, Class<T> returnType) {
		Class<?> type = FunctionType.wrapper(returnType);
		return args -> {
			Object res = func.call(context, scope, scope, args);
			if (type == Void.class || res == null) return null;

			if (res instanceof Wrapper w) res = w.unwrap();
			if (!type.isInstance(res))
				throw new ClassCastException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
			return (T) res;
		};
	}

	public static NativeJavaClass loadClass(Fi file, String name) {
		try (URLClassLoader urlLoader = new URLClassLoader(new URL[]{file.file().toURI().toURL()})) {
			return new NativeJavaClass(Vars.mods.getScripts().scope, urlLoader.loadClass(name));
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*public static byte jbyte(byte value) {
		return value;
	}

	public static short jshort(short value) {
		return value;
	}

	public static int jint(int value) {
		return value;
	}

	public static long jlong(long value) {
		return value;
	}

	public static float jfloat(float value) {
		return value;
	}

	public static double jdouble(double value) {
		return value;
	}*/

	public static Object invokeForHandle(MethodHandle handle, Object[] arr) {
		return Reflects.invokeStatic(handle, convertArgs(arr, handle.type().parameterArray()));
	}

	@Contract(pure = true)
	public static Object[] convertArgs(NativeArray arr, Class<?>[] types) {
		return convertArgs(arr.toArray(), types);
	}

	@Contract(pure = true)
	public static Object[] convertArgs(Object[] arr, Class<?>[] types) {
		Iterator<Class<?>> iterator = Arrays.stream(types).iterator();
		return Arrays.stream(arr).map(a -> JavaAdapter.convertResult(a, iterator.next())).toArray();
	}
}
