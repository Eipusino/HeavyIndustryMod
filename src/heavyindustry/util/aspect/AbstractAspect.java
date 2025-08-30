package heavyindustry.util.aspect;

import arc.func.Cons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The base class of container slicing should have the ability to intercept the add and remove entries of the
 * source and target containers. Otherwise, the source container should not be assigned to the slicing,
 * which would lose the meaning of the slicing。
 * <p>Slice container is a special container member processing object based on entrance proxy. It adds and
 * deletes slice members through the add and remove entrances of the proxy source, and filters them to
 * avoid relying on traversal to filter targets. It is particularly effective for hash indexed containers and can
 * better handle a certain type of object (determined by the filter) in the source container uniformly. Usually,
 * this processing procedure is operated by the trigger entry, and this class implements {@link Iterable}. You can
 * also directly traverse the elements.
 * <p>The implementation of slicing should complete the container allocation in the constructor and complete
 * the entry proxy for the source.
 *
 * @since 1.0.8
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractAspect<T, S> implements Iterable<T> {
	protected final List<BaseTriggerEntry<?>> triggers = new ArrayList<>();
	protected final Set<T> children = new LinkedHashSet<>();

	private final Cache iterateCache = new Cache();

	protected Cons<BaseTriggerEntry<?>> apply, remove;
	protected S source;
	protected Cons<T> entry;
	protected Cons<T> exit;

	private boolean modified = true;// initialization

	protected AbstractAspect(S source) {
		this.source = source;
	}

	/** Obtain valid container instances for the application of this aspect. */
	public abstract S instance();

	/** Element filter for aspect processing. */
	public abstract boolean filter(T target);

	/**
	 * When the aspect is no longer used, call this method to release the aspect. The specific release
	 * content is implemented by the subclass. Rewriting should be able to release all references of the
	 * aspect to members and various side effects produced by the aspect.
	 * <p>Before calling this method, please confirm that this aspect has been uninstalled from <strong>all</strong> aspect
	 * managers, otherwise it may cause unpredictable exceptions.
	 */
	public void releaseAspect() {
		children.clear();
		triggers.clear();
		apply = null;
		remove = null;
		source = null;
	}

	/**
	 * Set the entry trigger for the aspect, and call the trigger when an element is added to the aspect.
	 *
	 * @param entry Entry trigger, passing in elements that enter the aspect
	 */
	public AbstractAspect<T, S> setEntryTrigger(Cons<T> entry) {
		this.entry = entry;
		return this;
	}

	/**
	 * Set the exit trigger for the aspect, and call the trigger when the element exits from the aspect.
	 *
	 * @param exit Export trigger, passing in the element of the exit aspect
	 */
	public AbstractAspect<T, S> setExitTrigger(Cons<T> exit) {
		this.exit = exit;
		return this;
	}

	/** Reset the aspect, which will clear the elements saved in the aspect */
	public void reset() {
		for (T child : children) {
			if (exit != null) exit.get(child);
		}
		children.clear();
		modified = true;
	}

	/**
	 * Set up a trigger entry. For trigger entry, see {@link BaseTriggerEntry}
	 *
	 * @param trigger Trigger entrance
	 */
	public AbstractAspect<T, S> setTrigger(BaseTriggerEntry<T> trigger) {
		triggers.add(trigger);
		trigger.aspect = this;
		apply(trigger);
		return this;
	}

	/**
	 * Remove a trigger entry. For trigger entries, see {@link BaseTriggerEntry}
	 *
	 * @param trigger Trigger entrance
	 */
	public void removeTrigger(BaseTriggerEntry<T> trigger) {
		triggers.remove(trigger);
		remove(trigger);
	}

	/**
	 * Perform trigger processing on all sub elements of the aspect.
	 *
	 * @param entry Target trigger for execution
	 */
	public void run(BaseTriggerEntry<T> entry) {
		for (T child : this) {
			entry.handle(child);
		}
	}

	/**
	 * Attempt to add an element to the aspect, and if the filter passes, the element will enter the aspect.
	 *
	 * @param added Trying to add elements
	 */
	public void add(T added) {
		if (filter(added)) {
			if (children.add(added) && entry != null) {
				entry.get(added);
				modified = true;
			}
		}
	}

	/**
	 * Remove an element from the aspect.
	 *
	 * @param removed Elements removed from the cut surface
	 */
	public void remove(T removed) {
		if (children.remove(removed) && exit != null) {
			exit.get(removed);
			modified = true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		// To avoid concurrent modifications,
		// the behavior of slicing may cause elements to be removed from the slicing,
		// and a buffer is needed to avoid this issue.
		if (modified) {
			if (iterateCache.arr == null || iterateCache.arr.length < children.size()) {
				iterateCache.arr = (T[]) new Object[children.size()];
			}

			int i = 0;
			for (T child : children) {
				iterateCache.arr[i++] = child;
			}

			modified = false;
		}

		return iterateCache.iterator();
	}

	public void apply(BaseTriggerEntry entry) {
		if (apply != null) apply.get(entry);
	}

	public void remove(BaseTriggerEntry entry) {
		if (remove != null) remove.get(entry);
	}

	class Cache implements Iterable<T> {
		T[] arr;
		int index;

		Iterator<T> itr = new Iterator<>() {
			@Override
			public boolean hasNext() {
				return index < arr.length;
			}

			@Override
			public T next() {
				return arr[index++];
			}
		};

		@Override
		public Iterator<T> iterator() {
			index = 0;
			return itr;
		}
	}
}
