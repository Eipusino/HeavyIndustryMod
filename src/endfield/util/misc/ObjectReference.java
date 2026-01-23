package endfield.util.misc;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Func;
import arc.func.Prov;
import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/** @since 1.0.8 */
public class ObjectReference<T> implements Serializable, Poolable {
	private static final long serialVersionUID = -9054478421223311650l;

	public T element;

	public ObjectReference() {}

	public ObjectReference(T initialElement) {
		element = initialElement;
	}

	public T get() {
		if (element == null) {
			throw new NoSuchElementException("No value present");
		}
		return element;
	}

	public boolean isEmpty() {
		return element == null;
	}

	public boolean isPresent() {
		return element != null;
	}

	public void ifPresent(Cons<? super T> action) {
		if (element != null) {
			action.get(element);
		}
	}

	public void ifPresentOrElse(Cons<? super T> action, Runnable emptyAction) {
		if (element != null) {
			action.get(element);
		} else {
			emptyAction.run();
		}
	}

	public ObjectReference<T> filter(Boolf<? super T> predicate) {
		if (isPresent()) {
			return predicate.get(element) ? this : new ObjectReference<>();
		} else {
			return this;
		}
	}

	public <U> ObjectReference<U> map(Func<? super T, ? extends U> mapper) {
		if (isPresent()) {
			return new ObjectReference<>(mapper.get(element));
		} else {
			return new ObjectReference<>();
		}
	}

	@SuppressWarnings("unchecked")
	public <U> ObjectReference<U> flatMap(Func<? super T, ? extends ObjectReference<? extends U>> mapper) {
		if (isPresent()) {
			return (ObjectReference<U>) mapper.get(element);
		} else {
			return new ObjectReference<>();
		}
	}

	@SuppressWarnings("unchecked")
	public ObjectReference<T> or(Prov<? extends ObjectReference<? extends T>> supplier) {
		if (isPresent()) {
			return this;
		} else {
			return (ObjectReference<T>) supplier.get();
		}
	}

	public Stream<T> stream() {
		if (isPresent()) {
			return Stream.of(element);
		} else {
			return Stream.empty();
		}
	}

	public T orElse(T other) {
		return element != null ? element : other;
	}

	public T orElseGet(Prov<? extends T> supplier) {
		return element != null ? element : supplier.get();
	}

	public T orElseThrow() {
		if (element == null) {
			throw new NoSuchElementException("No element present");
		}
		return element;
	}

	public <X extends Throwable> T orElseThrow(Prov<? extends X> exceptionSupplier) throws X {
		if (element != null) {
			return element;
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = null;
	}
}
