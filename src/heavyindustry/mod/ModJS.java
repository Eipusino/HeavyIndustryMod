package heavyindustry.mod;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.util.*;
import mindustry.*;
import rhino.*;

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

    public static void init() {
        try {
            //declare heavy-industry java script APIs
            importDefaults((ImporterTopLevel) Vars.mods.getScripts().scope);
        } catch (Exception e) {
            Log.err("js initial error, stack trace", e);
        }
    }

    public static void importDefaults(ImporterTopLevel scope) {
        for (String pack : Utils.packages) {
            importPackage(scope, pack);
        }
    }

    public static void importPackage(ImporterTopLevel scope, String packageName) {
        if (scope == null) return;
        NativeJavaPackage p = new NativeJavaPackage(packageName, Vars.mods.mainLoader());
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
