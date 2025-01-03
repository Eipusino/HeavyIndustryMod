package dynamilize.runtimeannos;

import java.lang.annotation.*;

/**
 * Exclude tag, used for {@link dynamilize.DynamicClass#visitClass(Class, dynamilize.JavaHandleHelper)} (Class) to exclude members declared in the template class when accessing the template.
 * When a method/field has this annotation, the template access will directly ignore this method/field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Exclude {}
