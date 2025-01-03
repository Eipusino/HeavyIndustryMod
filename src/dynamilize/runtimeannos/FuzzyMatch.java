package dynamilize.runtimeannos;

import java.lang.annotation.*;

/**
 * The fuzz slicing method matching tool should only be used on the methods of {@linkplain AspectInterface AspectInterface} slicing interface,
 * Match the annotated section method with fuzzy search rules to other overloaded methods with the same name as this <strong>method</strong>
 *
 * <p>This annotation needs to be accompanied by specified parameter annotations for the method to describe the matching rules for a method name,
 * and parameter annotations can be found in:
 * <ul>
 *   <li><strong>{@link AnyType}</strong></li>
 *   <li><strong>{@link TypeAssignable}</strong></li>
 *   <li><strong>{@link Exact}(Default value)</strong></li>
 * </ul>
 *
 * For slice methods that do not carry this annotation,
 * exact matching will be used, and the matching tool is equivalent to using default parameters for this annotation.
 * All parameters do not carry matching annotations, that is:
 * <pre>{@code
 * void method(String arg1, Object arg2);
 *
 * //Equivalent to:
 *
 * @FuzzyMatch
 * void method(String arg1, Object arg2);
 * }</pre>
 *
 * If different fuzzy matchers are declared multiple times in the extended structure of the slicing interface, the actual matching will be based on the first one found.
 * <p>
 * <strong>be careful:</strong>No matter how you set the matching parameters, all methods in the slicing interface can never be excluded,
 * Because under normal circumstances, the methods defined in the slicing interface will be implemented by dynamic objects at any time,
 * this fuzzy matcher cannot be used for self filtering and can only be used to search for other superclass methods that match to add delegates.
 * Obviously, the additional slice methods matched through fuzzy search cannot be accessed through the corresponding interface types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FuzzyMatch {
    /** Does the scope of fuzzy search include base classes for dynamic delegation (including a list of dynamically implemented interfaces) */
    boolean inBaseClass() default true;
    /** Does the scope of fuzzy search include superclass extensions of base classes through dynamic delegation (including all implemented interfaces) */
    boolean inSuperClass() default true;
    /** Does it only match abstract methods (interfaces from dynamic implementations or delegate base classes are abstract classes) */
    boolean abstractOnly() default false;

    /**
     * Does it match all methods with the same name within the scope?
     * If this property is true, the parameter table of the method will be ignored and all overloaded methods with the same name will be directly matched.
     */
    boolean anySameName() default false;

    /**
     * Match the position of this parameter with any type of parameter.
     * <br>Attribute: {@link AnyType#value()}
     */
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.PARAMETER)
    @interface AnyType {
        /** A regular expression used to match parameter names, ignoring parameter names by default and only comparing parameter types. */
        String value() default "^.*$";
    }

    /**
     * Match the position of the parameter with the assignable type, that is, the matched parameter type should be the same as the matching parameter or its subclass.
     * <br>Attribute: {@link TypeAssignable#value()}
     */
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.PARAMETER)
    @interface TypeAssignable {
        /** A regular expression used to match parameter names, ignoring parameter names by default and only comparing parameter types. */
        String value() default "^.*$";
    }

    /** The parameter type that matches the position of the parameter must be exactly the same as this parameter. */
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.PARAMETER)
    @interface Exact {
        Exact INSTANCE = new Exact() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Exact.class;
            }
        };
    }
}
