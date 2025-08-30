package heavyindustry.util.aspect;

import arc.func.Boolf;
import arc.func.Cons;
import arc.struct.ObjectMap;
import dynamilize.DynamicClass;
import heavyindustry.HVars;

import java.lang.reflect.Method;

/**
 * The slicing base class of arc container can perform slicing management on arc container and its
 * subclasses. There is an internal abstract class {@link BaseContainerType} that controls
 * the proxy entry policy for the source container.
 * <p>The entry instance for container implementation should be returned in method {@link BaseContainerAspect#contType()}, which will
 * be used to construct the container's entry proxy instance.
 * </p><strong>Note that this aspect does not cover the fields of the source container. When constructing the
 * container aspect, you should set the source field as the entry proxy instance for this aspect in the
 * lambda passed in.</strong>
 *
 * @since 1.0.8
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseContainerAspect<T, C> extends AbstractAspect<T, C> {
	protected final C proxiedCont;
	protected final Boolf<T> filter;

	protected BaseContainerAspect(C source, Boolf<T> filter, Cons<C> fieldSetter) {
		super(source);
		this.filter = filter;
		proxiedCont = (C) contType().instance(source);
		contType().addAspect(proxiedCont, this);
		fieldSetter.get(proxiedCont);
	}

	public abstract BaseContainerType contType();

	@Override
	public C instance() {
		return proxiedCont;
	}

	@Override
	public boolean filter(T target) {
		return filter.get(target);
	}

	@Override
	public void releaseAspect() {
		super.releaseAspect();
		contType().removeAspect(proxiedCont);
	}

	/**
	 * Template for container types, used to create proxies for entry into target containers. Different
	 * container entries may have their own names and implementations, and the specific implementation
	 * of this type is simply to implement proxies for add and remove entries.
	 */
	public abstract static class BaseContainerType<D> {
		protected final ObjectMap<Object, BaseContainerAspect<?, D>> aspectMap = new ObjectMap<>();
		protected final Class<D> type;

		protected DynamicClass AspectType;

		protected BaseContainerType(Class<D> type) {
			this.type = type;
		}

		public DynamicClass getEntryProxy(Class<? extends D> type) {
			if (AspectType != null) return AspectType;

			AspectType = DynamicClass.get(type.getSimpleName() + "Aspect");
			for (Method method : getAddEntry()) {
				AspectType.setFunction(method.getName(), (self, supe, args) -> {
					BaseContainerAspect<Object, D> aspect = (BaseContainerAspect<Object, D>) aspectMap.get(self);
					if (aspect != null) onAdd(aspect, self.objSelf(), args.args());
					return supe.invokeFunc(method.getName(), args);
				}, method.getParameterTypes());
			}
			for (Method method : getRemoveEntry()) {
				AspectType.setFunction(method.getName(), (self, supe, args) -> {
					BaseContainerAspect<Object, D> aspect = (BaseContainerAspect<Object, D>) aspectMap.get(self);
					if (aspect != null) onRemove(aspect, self.objSelf(), args.args());
					return supe.invokeFunc(method.getName(), args);
				}, method.getParameterTypes());
			}

			return AspectType;
		}

		public D instance(D source) {
			return instance((Class<D>) source.getClass());
		}

		public D instance(Class<? extends D> type) {
			if (!this.type.isAssignableFrom(type))
				throw new IllegalArgumentException("can not create a disassignable class: " + type + " instance");
			return HVars.classHandler.getDynamicMaker().newInstance(type, getEntryProxy(type)).objSelf();
		}

		public void addAspect(D cont, BaseContainerAspect<?, D> aspect) {
			aspectMap.put(cont, aspect);
		}

		public void removeAspect(D cont) {
			aspectMap.remove(cont);
		}

		/**
		 * The proxy call implementation method of the add entry is executed when the add entry of the
		 * target source is called. The subclass implementation of this method should ensure that the
		 * added target can pass through the filter correctly and make the correct decision on whether to
		 * enter the aspect.
		 */
		public abstract void onAdd(BaseContainerAspect<Object, D> aspect, D cont, Object[] args);

		/**
		 * The proxy call implementation method for the remove entry is executed when the remove
		 * entry from the target source is called. Subclass implementation of this method should
		 * ensure that the removed target can be correctly removed from the aspect.
		 */
		public abstract void onRemove(BaseContainerAspect<Object, D> aspect, D cont, Object[] args);

		/**
		 * The subclass implementation should return the <strong>public or protected</strong> methods that the
		 * container only calls when adding an object, and these methods should be called and correctly
		 * passed to the added object when obtaining the add entry method object of the source
		 * container.
		 * <p>Note that the entry method may have a method iteration structure, and methods that have
		 * iterative calls should call the last level method to avoid marking two or more steps in the
		 * iteration as entries.
		 */
		public abstract Method[] getAddEntry();

		/**
		 * The subclass implementation should return the <strong>public or protected</strong> methods that the
		 * container only calls when deleting an object, and these methods should be called and correctly
		 * passed to the removed object when adding the object.
		 * <p>Note that the entry method may have a method iteration structure, and methods that have
		 * iterative calls should call the last level method to avoid marking two or more steps in the
		 * iteration as exits.
		 */
		public abstract Method[] getRemoveEntry();
	}
}
