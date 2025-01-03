package dynamilize.runtimeannos;

import java.lang.annotation.*;

/**
 * This pointer marker is used to mark the first parameter of the method when {@link dynamilize.DynamicClass#visitClass(Class,dynamilize.JavaHandleHelper)} accesses the method behavior template,
 * The marked parameter will be passed as a 'this' pointer to the object itself when the function is called. This parameter is not included in the function parameter table for matching.
 * Please refer to the {@linkplain dynamilize.DynamicClass#visitClass(Class, dynamilize.JavaHandleHelper) access method template section}
 * <p>Although not explicitly stated, parameters marked as this pointer should be read-only (with final modifier)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface This {}
