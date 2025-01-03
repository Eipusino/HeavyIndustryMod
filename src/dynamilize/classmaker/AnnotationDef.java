package dynamilize.classmaker;

import dynamilize.*;
import dynamilize.classmaker.code.*;
import dynamilize.classmaker.code.annotation.AnnotatedElement;
import dynamilize.classmaker.code.annotation.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class AnnotationDef<A extends Annotation> implements Annotationc<A> {
    Classc<A> typeClass;

    final AnnotationType<A> annoType;
    Map<String, Object> pairs;

    A anno;

    AnnotatedElement element;

    public AnnotationDef(A anno) {
        typeClass = (Classc<A>) ClassInfo.asType(anno.annotationType());
        annoType = typeClass.asAnnotation(null);

        this.anno = anno;

        try {
            HashMap<String, Object> temp = new HashMap<>(annoType.defaultValues());
            for (Method method: anno.annotationType().getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) || method.getParameterCount() > 0) continue;
                temp.put(method.getName(), method.invoke(anno));
            }
            pairs = new HashMap<>(temp);
        } catch (Throwable e) {
            throw new IllegalHandleException(e);
        }
    }

    public AnnotationDef(AnnotationType<A> type, AnnotatedElement element, Map<String, Object> attributes) {
        annoType = type;
        typeClass = type.typeClass();

        this.element = element;

        HashMap<String, Object> temp = new HashMap<>(annoType.defaultValues());
        if (attributes != null) temp.putAll(attributes);
        pairs = new HashMap<>(temp);
    }

    @Override
    public AnnotationType<A> annotationType() {
        return annoType;
    }

    @Override
    public Map<String, Object> pairs() {
        return pairs;
    }

    @Override
    public A asAnnotation() {
        if (anno == null) {
            if (typeClass == null) {
                if (!annoType.typeClass().isExistedClass())
                    throw new IllegalHandleException("only get annotation object with existed type info");

                typeClass = annoType.typeClass();
            }

            anno = element.getAnnotation(typeClass.getTypeClass());
        }

        return anno;
    }

    @Override
    public <T> T getAttr(String attr) {
        return (T) pairs.computeIfAbsent(attr, e -> {
            throw new IllegalHandleException("no such attribute in annotation" + this);
        });
    }
}
