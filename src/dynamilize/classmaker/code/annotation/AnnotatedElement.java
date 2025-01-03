package dynamilize.classmaker.code.annotation;

import dynamilize.classmaker.code.*;

import java.lang.annotation.*;
import java.util.*;

public interface AnnotatedElement {
    List<Annotationc<?>> getAnnotations();

    void initAnnotations();

    boolean hasAnnotation(Classc<? extends Annotation> annoType);

    <T extends Annotation> Annotationc<T> getAnnotation(Classc<T> annoType);

    boolean isType(ElementType type);

    <A extends Annotation> A getAnnotation(Class<A> annoClass);

    void addAnnotation(Annotationc<?> annotation);
}
