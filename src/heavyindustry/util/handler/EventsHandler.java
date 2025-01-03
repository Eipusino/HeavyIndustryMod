package heavyindustry.util.handler;

import arc.*;
import arc.func.*;
import arc.struct.*;
import heavyindustry.util.*;

import java.lang.reflect.*;

/** A toolset for performing search and delete operations on Events in the arc library. */
public final class EventsHandler {
    private static final ObjectMap<Object, Seq<Cons<?>>> events = FieldHandler.getValueDefault(Events.class, "events");

    private EventsHandler() {}

    public static Cons<?>[] getListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter) {
        return events.get(event, Empties.nilSeq()).retainAll(e -> {
            Class<?> c = e.getClass();
            if (c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
                    || c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName())) {
                return filter.get(e, e.getClass().getDeclaredFields());
            }
            return false;
        }).toArray(Cons.class);
    }

    public static void removeListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter) {
        events.get(event, Empties.nilSeq()).remove(e -> {
            Class<?> c = e.getClass();
            if (c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
                    || c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName())) {
                return filter.get(e, e.getClass().getDeclaredFields());
            }
            return false;
        });
    }
}
