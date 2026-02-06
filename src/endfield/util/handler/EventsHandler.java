package endfield.util.handler;

import arc.Events;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;

import java.lang.reflect.Field;

import static endfield.Vars2.classHelper;

/**
 * A toolset for performing search and delete operations on Events in the arc library.
 *
 * @since 1.0.9
 */
public final class EventsHandler {
	static final Seq<Cons<?>> consumers = new Seq<>(true, 0, Cons.class);
	static final ObjectMap<Object, Seq<Cons<?>>> events = FieldHandler.getObjectDefault(Events.class, "events");

	private EventsHandler() {}

	public static Cons<?>[] getListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter) {
		return events.get(event, consumers).retainAll(e -> {
			Class<?> c = e.getClass();
			return (c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
					|| c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName()))
					&& filter.get(e, classHelper.getFields(e.getClass()));
		}).toArray(Cons.class);
	}

	public static void removeListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter) {
		events.get(event, consumers).remove(e -> {
			Class<?> c = e.getClass();
			return (c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
					|| c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName()))
				&& filter.get(e, classHelper.getFields(e.getClass()));
		});
	}
}
