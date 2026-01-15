package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;

public class Pair<L, R> implements Poolable {
	private static final Pair<Object, Object> empty = new Pair<>(null, null);

	public L left;
	public R right;

	public Pair(L l, R r) {
		left = l;
		right = r;
	}

	@SuppressWarnings("unchecked")
	public static <L, R> Pair<L, R> empty() {
		return (Pair<L, R>) empty;
	}

	public static <L, R> Pair<L, R> createLeft(L left) {
		return left == null ? empty() : new Pair<>(left, null);
	}

	public static <L, R> Pair<L, R> createRight(R right) {
		return right == null ? empty() : new Pair<>(null, right);
	}

	public static <L, R> Pair<L, R> create(L left, R right) {
		return right == null && left == null ? empty() : new Pair<>(left, right);
	}

	@Override
	public String toString() {
		return "(" + left + ", " + right + ")";
	}

	@Override
	public void reset() {
		left = null;
		right = null;
	}
}
