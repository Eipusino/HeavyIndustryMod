package heavyindustry.desktop;

import heavyindustry.util.*;

import java.lang.reflect.*;
import java.util.*;

import static heavyindustry.util.unsafe.UnsafeAccess.*;

/**
 * The anti modularization tool only provides one main method {@link Demodulator#makeModuleOpen(Module, Package, Module)} to force the software package of the required module to be open.
 * <p>This class of behavior may completely break the modular access protection and is inherently insecure. If it is not necessary, please try to avoid using this class as much as possible.
 * <p><strong>This class of behavior is only available on the Desktop platform and cannot be used on the Android platform.</strong>
 *
 * @since 1.0.6
 */
public final class Demodulator {
    private static final long fieldFilterOffset = 112l;

    private static final Field opensField;
    private static final Field exportField;

    private static final Method exportNative;

    static {
        ensureFieldOpen();

        opensField = Reflectf.findField(Module.class, "openPackages", false);
        exportField = Reflectf.findField(Module.class, "exportedPackages", false);

        makeModuleOpen(Module.class.getModule(), "java.lang", Demodulator.class.getModule());

        exportNative = Reflectf.findMethod(Module.class, "addExports0", true, Module.class, String.class, Module.class);
        Reflectf.invokeMethod(null, exportNative, Module.class.getModule(), "java.lang", Demodulator.class.getModule());
    }

    /** Don't let anyone instantiate this class. */
    private Demodulator() {}

    public static void makeModuleOpen(Module from, Class<?> clazz, Module to) {
        if (clazz.isArray()) {
            makeModuleOpen(from, clazz.getComponentType(), to);
        } else makeModuleOpen(from, clazz.getPackage(), to);
    }

    public static void makeModuleOpen(Module from, Package pac, Module to) {
        if (checkModuleOpen(from, pac, to)) return;

        makeModuleOpen(from, pac.getName(), to);
    }

    @SuppressWarnings("unchecked")
    public static void makeModuleOpen(Module from, String pac, Module to) {
        if (exportNative != null) Reflectf.invokeMethod(null, exportNative, from, pac, to);

        var opensMap = (Map<String, Set<Module>>) unsafe().getObjectVolatile(from, unsafe().objectFieldOffset(opensField));
        if (opensMap == null) {
            opensMap = new HashMap<>();
            unsafe().putObjectVolatile(from, unsafe().objectFieldOffset(opensField), opensMap);
        }

        var exportsMap = (Map<String, Set<Module>>) unsafe().getObjectVolatile(from, unsafe().objectFieldOffset(exportField));
        if (exportsMap == null) {
            exportsMap = new HashMap<>();
            unsafe().putObjectVolatile(from, unsafe().objectFieldOffset(exportField), exportsMap);
        }

        var opens = opensMap.computeIfAbsent(pac, e -> new HashSet<>());
        var exports = exportsMap.computeIfAbsent(pac, e -> new HashSet<>());

        try {
            opens.add(to);
        } catch (UnsupportedOperationException e) {
            var lis = new ArrayList<>(opens);
            lis.add(to);
            opensMap.put(pac, new HashSet<>(lis));
        }

        try {
            exports.add(to);
        } catch (UnsupportedOperationException e) {
            var lis = new ArrayList<>(exports);
            lis.add(to);
            exportsMap.put(pac, new HashSet<>(lis));
        }
    }

    public static boolean checkModuleOpen(Module from, Package pac, Module to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        if (pac == null) return true;

        return from.isOpen(pac.getName(), to);
    }

    @SuppressWarnings({"unchecked"})
    public static void ensureFieldOpen() {
        Class<?> clazz = Reflectf.forClass("jdk.internal.reflect.Reflection");
        Map<Class<?>, Set<String>> map = (Map<Class<?>, Set<String>>) unsafe().getObject(clazz, fieldFilterOffset);
        map.clear();
    }
}
