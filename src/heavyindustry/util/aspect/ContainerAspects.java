package heavyindustry.util.aspect;

import arc.func.Boolf;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;

import java.lang.reflect.Method;

/**
 * The collection of corresponding slicing processors for containers in the Arc library, providing slicing types
 * for commonly used containers and factory methods for obtaining instances of these types.
 *
 * @since 1.0.8
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ContainerAspects {
	private ContainerAspects() {}

	/*----------------------------------
	 * arc.struct.Seq
	 *--------------------------------*/
	public static class SeqAspect<E> extends BaseContainerAspect<E, Seq<E>> {
		protected static final SeqContainerType contType = new SeqContainerType();

		public SeqAspect(Seq<E> source, Boolf<E> filter, Cons<Seq<E>> fieldSetter) {
			super(source, filter, fieldSetter);
		}

		@Override
		public SeqContainerType contType() {
			return contType;
		}
	}

	public static class SeqContainerType extends BaseContainerAspect.BaseContainerType<Seq> {
		public SeqContainerType() {
			super(Seq.class);
		}

		@Override
		public void onAdd(BaseContainerAspect<Object, Seq> aspect, Seq seq, Object[] args) {
			aspect.add(args[0]);
		}

		@Override
		public void onRemove(BaseContainerAspect<Object, Seq> aspect, Seq seq, Object[] args) {
			aspect.remove(args[0]);
		}

		@Override
		public Method[] getAddEntry() {
			try {
				return new Method[]{Seq.class.getMethod("add", Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Method[] getRemoveEntry() {
			try {
				return new Method[]{Seq.class.getMethod("remove", Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*----------------------------------
	 * arc.struct.ObjectSet
	 *--------------------------------*/
	public static class ObjectSetAspect<E> extends BaseContainerAspect<E, ObjectSet<E>> {
		protected static final ObjectSetContainerType contType = new ObjectSetContainerType();

		public ObjectSetAspect(ObjectSet<E> source, Boolf<E> filter, Cons<ObjectSet<E>> fieldSetter) {
			super(source, filter, fieldSetter);
		}

		@Override
		public ObjectSetContainerType contType() {
			return contType;
		}
	}

	public static class ObjectSetContainerType extends BaseContainerAspect.BaseContainerType<ObjectSet> {
		public ObjectSetContainerType() {
			super(ObjectSet.class);
		}

		@Override
		public void onAdd(BaseContainerAspect<Object, ObjectSet> aspect, ObjectSet set, Object[] args) {
			aspect.add(args[0]);
		}

		@Override
		public void onRemove(BaseContainerAspect<Object, ObjectSet> aspect, ObjectSet set, Object[] args) {
			aspect.remove(args[0]);
		}

		@Override
		public Method[] getAddEntry() {
			try {
				return new Method[]{ObjectSet.class.getMethod("add", Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Method[] getRemoveEntry() {
			try {
				return new Method[]{ObjectMap.class.getMethod("remove", Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*----------------------------------
	 * arc.struct.ObjectMap
	 *--------------------------------*/
	public static class ObjectMapAspect<K, E> extends BaseContainerAspect<E, ObjectMap<K, E>> {
		protected static final ObjectMapContainerType contType = new ObjectMapContainerType();

		public ObjectMapAspect(ObjectMap<K, E> source, Boolf<E> filter, Cons<ObjectMap<K, E>> fieldSetter) {
			super(source, filter, fieldSetter);
		}

		@Override
		public ObjectMapContainerType contType() {
			return contType;
		}
	}

	public static class ObjectMapContainerType extends BaseContainerAspect.BaseContainerType<ObjectMap> {
		public ObjectMapContainerType() {
			super(ObjectMap.class);
		}

		@Override
		public void onAdd(BaseContainerAspect<Object, ObjectMap> aspect, ObjectMap objectMap, Object[] args) {
			aspect.add(args[1]);
		}

		@Override
		public void onRemove(BaseContainerAspect<Object, ObjectMap> aspect, ObjectMap objectMap, Object[] args) {
			aspect.remove(objectMap.get(args[0]));
		}

		@Override
		public Method[] getAddEntry() {
			try {
				return new Method[]{ObjectMap.class.getMethod("put", Object.class, Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Method[] getRemoveEntry() {
			try {
				return new Method[]{ObjectMap.class.getMethod("remove", Object.class)};
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
