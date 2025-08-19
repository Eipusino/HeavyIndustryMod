package heavyindustry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fills a {@code Seq.<String>with()}'s arg with the list of compiled packages.
 */
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface ListPackages {
}
