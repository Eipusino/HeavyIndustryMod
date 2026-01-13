package heavyindustry.util;

import arc.util.pooling.Pool.Poolable;

/**
 * For passing into closures.
 */
public final class Wrappers {
	private Wrappers() {}

	public static class NumberWrapper<T extends Number> implements Poolable {
		public T val;

		public NumberWrapper() {
			reset();
		}

		public NumberWrapper(T val) {
			this.val = val;
		}

		public NumberWrapper(NumberWrapper<T> val) {
			this.val = val.val;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void reset() {
			val = (T) (Integer.valueOf(0));
		}
	}

	public static class ObjectWrapper<T> implements Poolable {
		public T val;

		public ObjectWrapper() {
			reset();
		}

		public ObjectWrapper(T val) {
			this.val = val;
		}

		public ObjectWrapper(ObjectWrapper<T> val) {
			this.val = val.val;
		}

		@Override
		public void reset() {
			val = null;
		}
	}
}
