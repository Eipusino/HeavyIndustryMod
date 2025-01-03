package dynamilize.classmaker.code.annotation;

import dynamilize.classmaker.*;
import dynamilize.classmaker.code.*;

import java.lang.annotation.*;
import java.util.*;

public interface AnnotationType<T extends Annotation> {
    static <A extends Annotation> AnnotationType<A> asAnnotationType(Class<A> annoType) {
        Classc<A> clazz = ClassInfo.asType(annoType);
        return clazz.asAnnotation(null);
    }

    Classc<T> typeClass();

    Map<String, Object> defaultValues();

    Annotationc<T> annotateTo(AnnotatedElement element, Map<String, Object> attributes);
}
