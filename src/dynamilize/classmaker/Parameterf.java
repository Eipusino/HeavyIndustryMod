package dynamilize.classmaker;

import dynamilize.*;
import dynamilize.classmaker.code.*;
import dynamilize.classmaker.code.annotation.AnnotatedElement;

import java.lang.annotation.*;
import java.lang.reflect.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Parameterf<T> extends AnnotatedMember implements AnnotatedElement {
    final Classc<T> type;
    Methodc<?, ?> method;

    boolean initialized;

    public void setOwner(Methodc<?, ?> method) {
        this.method = method;
    }

    public Methodc<?, ?> owner() {
        return method;
    }

    public static Parameterf<?>[] as(Object... infos) {
        Parameterf<?>[] res = new Parameterf[infos.length / 3];
        for (int i = 0; i < infos.length; i += 3) {
            res[i / 3] = new Parameterf<>(
                    ((Number) infos[i]).intValue(),
                    infos[i + 1] instanceof Classc ? (Classc) infos[i + 1] : ClassInfo.asType((Class<?>) infos[i + 1]),
                    (String) infos[i + 2]
            );
        }

        return res;
    }

    public static Parameterf<?>[] trans(Classc<?>... types) {
        Parameterf<?>[] res = new Parameterf[types.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = new Parameterf<>(0, types[i], "$param$" + i);
        }

        return res;
    }

    public static Parameterf<?>[] asParameter(Parameter... params) {
        Parameterf<?>[] res = new Parameterf[params.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = new Parameterf<>(params[i].getModifiers(), ClassInfo.asType(params[i].getType()), params[i].getName());
        }

        return res;
    }

    public Parameterf(int modifiers, Classc<T> type, String name) {
        super(name);
        setModifiers(modifiers);
        this.type = type;
    }

    public Classc<T> getType() {
        return type;
    }

    @Override
    public void initAnnotations() {
        if (initialized) return;

        Class<?> clazz = method.owner().getTypeClass();
        if (clazz == null)
            throw new IllegalHandleException("only get annotation object in existed type info");

        try {
            Method met = clazz.getDeclaredMethod(name(), method.parameters().stream().map(e -> e.getType().getTypeClass()).toArray(Class[]::new));

            Parameter[] p = met.getParameters();
            for (int i = 0; i < p.length; i++) {
                for (Annotation annotation: p[i].getAnnotations()) {
                    method.parameters().get(i).addAnnotation(new AnnotationDef<>(annotation));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        initialized = true;
    }

    @Override
    public boolean isType(ElementType type) {
        return type == ElementType.PARAMETER;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annoClass) {
        Class<?> clazz = method.owner().getTypeClass();
        if (clazz == null)
            throw new IllegalHandleException("only get annotation object in existed type info");

        try {
            for (Parameter parameter: clazz.getDeclaredMethod(method.name(), method.parameters().stream().map(e -> e.getType().getTypeClass()).toArray(Class[]::new)).getParameters()) {
                if (parameter.getName().equals(name())) return parameter.getAnnotation(annoClass);
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalHandleException(e);
        }

        return null;
    }
}
