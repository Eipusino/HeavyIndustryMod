package heavyindustry.util.concurrent;

import arc.util.pooling.Pool.Poolable;

import java.util.Objects;

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
	public int hashCode() {
		return Objects.hashCode(left) + 31 * Objects.hashCode(right);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Pair<?, ?> pair && Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
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
