package endfield.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Source-level annotations to generate entity classes from components. */
public final class Annotations {
	private Annotations() {}

	/* Fills a {@code new String[]{}}'s arg with the list of compiled classes with their qualified names. */
	/*@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ListClasses {}*/

	/** Fills a {@code new String[]{}}'s arg with the list of compiled packages. */
	@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ListPackages {}
}
