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

import arc.func.Func;
import arc.util.Log;
import dynamilize.FunctionType;
import endfield.Vars2;
import endfield.util.Reflects;
import mindustry.Vars;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import rhino.Context;
import rhino.Function;
import rhino.JavaAdapter;
import rhino.NativeArray;
import rhino.Scriptable;
import rhino.Wrapper;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class Scripts2 {
	/** Don't let anyone instantiate this class. */
	private Scripts2() {}

	/** Initializes the Mod JS. */
	@Internal
	public static void init() {
		try {
			Vars.mods.getScripts().context.evaluateReader(
					Vars.mods.getScripts().scope,
					Vars2.internalTree.child("other").child("endfield-global.js").reader(),
					"endfield-global.js",
					0
			);
		} catch (Throwable e) {
			Log.err(e);
		}
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
