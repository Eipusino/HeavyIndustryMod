package heavyindustry.mod;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.util.*;
import mindustry.*;
import rhino.*;

import static heavyindustry.util.Utils.*;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions.
 *
 * @since 1.0.6
 */
public final class ModJS {
    public static final Seq<Runnable> runs = new Seq<>();
    public static final Seq<String> names = new Seq<>();

    /** Don't let anyone instantiate this class. */
    private ModJS() {}

    public static void load() {
        try {
            run(runs, names);
            runs.clear();
            names.clear();
        } catch (Exception e) {
            Vars.ui.showException(e);
            Log.err(e);
        }
    }

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
     *              another custom scope.
     */
    public static void importDefaults(ImporterTopLevel scope) {
        for (String pack : packages) {
            importPackage(scope, pack);
        }
    }

    /**
     * Imports a single package to the given scope.
     * @param scope See {@link #importDefaults(ImporterTopLevel)}.
     * @param name  The package's fully qualified name.
     */
    public static void importPackage(ImporterTopLevel scope, String name) {
        if (scope == null) return;
        var p = new NativeJavaPackage(name, Vars.mods.mainLoader());
        p.setParentScope(scope);

        scope.importPackage(p);
    }

    public static void importPackage(ImporterTopLevel scope, Package pack) {
        importPackage(scope, pack.getName());
    }

    public static void importClass(ImporterTopLevel scope, String canonical) {
        importClass(scope, Reflectf.findClass(canonical));
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
        Class<?> type = Reflectf.box(returnType);
        return args -> {
            Object res = func.call(context, scope, scope, args);
            if (type == void.class || type == Void.class) return null;

            if (res instanceof Wrapper w) res = w.unwrap();
            if (!type.isAssignableFrom(res.getClass()))
                throw new IllegalStateException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
            return (T) type.cast(res);
        };
    }

    private static void run(Seq<Runnable> run, Seq<String> name) {
        if (name.any()) {
            for (int i = 0; i < name.size && i < run.size; i++) {
                Vars.content.setCurrentMod(Vars.mods.locateMod(name.get(i)));

                try {
                    require(name.get(i));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Log.err(e);
                }

                run.get(i).run();
            }

            Vars.content.setCurrentMod(null);

            try {
                require(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.err(e);
            }
        }
    }

    private static void require(String modName) throws NoSuchFieldException, IllegalAccessException {
        var obj = Vars.mods.getScripts();
        var field = obj.getClass().getDeclaredField("currentMod");
        field.setAccessible(true);
        if (modName == null) {
            field.set(obj, null);
        } else {
            field.set(obj, Vars.mods.getMod(modName));
        }
    }
}
