package heavyindustry.util.handler;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/** A set of practical tools for copying field properties of an object to another. */
public final class ObjectHandler {
    public static Consumer<Throwable> exceptionHandler = e -> {};

    private ObjectHandler() {}

    /**
     * Copy all attribute values of the source object completely to the target object,
     * which must be the type or subclass of the source object.
     *
     * @param source Source object of attribute
     * @param target Copy the attribute to the target object
     * @throws IllegalArgumentException If the target object is not assigned from the source class
     */
    public static <S, T extends S> void copyField(S source, T target) {
        copyFieldAsBlack(source, target);
    }

    /**
     * Copy the values of attributes of the source object that are not on the blacklist to the target object,
     * which must be the type or subclass of the source object.
     *
     * @param source    Source object of attribute
     * @param target    Copy the attribute to the target object
     * @param blackList Field blacklist
     */
    public static <S, T extends S> void copyFieldAsBlack(S source, T target, String... blackList) {
        Class<?> curr = source.getClass();
        Set<String> black = new HashSet<>(Arrays.asList(blackList));
        Set<String> fields = new HashSet<>();

        while (curr != Object.class) {
            for (Field field : curr.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                fields.add(field.getName());
            }

            curr = curr.getSuperclass();
        }

        copyFieldAsWhite(source, target, fields.stream().filter(e -> !black.contains(e)).toArray(String[]::new));
    }

    /**
     * Copy the specified attribute values of the source object to the target object,
     * hich must be the type or subclass of the source object.
     *
     * @param source    Source object of attribute
     * @param target    Copy the attribute to the target object
     * @param whiteList Field whitelist
     */
    public static <S, T extends S> void copyFieldAsWhite(S source, T target, String... whiteList) {
        for (String s : whiteList) {
            try {
                FieldHandler.setValueDefault(target, s, FieldHandler.getValueDefault(source, s));
            } catch (Throwable e) {
                exceptionHandler.accept(e);
            }
        }
    }
}
