package dynamilize.classmaker;

import dynamilize.*;
import dynamilize.classmaker.code.*;
import dynamilize.classmaker.code.annotation.*;

import java.lang.annotation.*;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class AnnotatedMember implements AnnotatedElement {
    private final String name;
    private int modifiers;

    private List<Annotationc<?>> annotationsList;

    public AnnotatedMember(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public int modifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public List<Annotationc<?>> getAnnotations() {
        return annotationsList == null ? new ArrayList<>() : annotationsList;
    }

    @Override
    public boolean hasAnnotation(Classc<? extends Annotation> annoType) {
        return getAnnotation(annoType) != null;
    }

    @Override
    public <A extends Annotation> Annotationc<A> getAnnotation(Classc<A> annoType) {
        if (annotationsList == null) return null;

        for (Annotationc<?> annotation: annotationsList) {
            if (annoType.equals(annotation.annotationType().typeClass())) {
                return (Annotationc<A>) annotation;
            }
        }

        return null;
    }

    @Override
    public void addAnnotation(Annotationc<?> annotation) {
        if (annotationsList == null) {
            annotationsList = new ArrayList<>();
        }

        checkType(annotation);

        for (Annotationc<?> anno: annotationsList) {
            if (anno.annotationType().equals(annotation.annotationType())) {
                if (!anno.annotationType().typeClass().hasAnnotation(ClassInfo.asType(Repeatable.class)))
                    throw new IllegalHandleException("cannot add multiple annotations with same name that without repeatable meta annotation");
            }
        }

        annotationsList.add(annotation);
    }

    protected void checkType(Annotationc<?> annotation) {
        Annotationc<Target> tarMark = annotation.annotationType().typeClass().getAnnotation(ClassInfo.asType(Target.class));
        if (tarMark == null) return;

        for (ElementType type: tarMark.asAnnotation().value()) {
            if (isType(type)) return;
        }

        throw new IllegalHandleException(annotation + " can not use on " + this);
    }
}
