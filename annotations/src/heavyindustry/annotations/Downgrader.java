package heavyindustry.annotations;

import arc.util.Reflect;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Source.Feature;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Collections;
import java.util.Set;

/**
 * Makes users able to use Java 9+ syntactic-sugars while still targeting Java 8.
 */
public class Downgrader extends AbstractProcessor {
	static {
		try {
			// Get the trusted private lookup.
			Lookup lookup = Reflect.get(Lookup.class, "IMPL_LOOKUP");
			// Get the minimum level setter, to force certain features to qualify as a Java 8 feature.
			MethodHandle set = lookup.findSetter(Feature.class, "minLevel", Source.class);

			// Downgrade most Java 8-compatible features.
			for (Feature feature : new Feature[]{
					Feature.EFFECTIVELY_FINAL_VARIABLES_IN_TRY_WITH_RESOURCES,
					Feature.PRIVATE_SAFE_VARARGS,
					Feature.DIAMOND_WITH_ANONYMOUS_CLASS_CREATION,
					Feature.LOCAL_VARIABLE_TYPE_INFERENCE,
					Feature.VAR_SYNTAX_IMPLICIT_LAMBDAS,
					Feature.SWITCH_MULTIPLE_CASE_LABELS,
					Feature.SWITCH_RULE,
					Feature.SWITCH_EXPRESSION,
					Feature.TEXT_BLOCKS,
					Feature.PATTERN_MATCHING_IN_INSTANCEOF,
					Feature.REIFIABLE_TYPES_INSTANCEOF
			})
				set.invokeExact(feature, Source.JDK8);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		return false;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_17;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton("*");
	}
}
