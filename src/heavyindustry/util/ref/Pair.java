package heavyindustry.util.ref;

import java.util.Objects;

public class Pair<L, R> {
	static final Pair<Object, Object> empty = new Pair<>(null, null);

	public final L left;
	public final R right;

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
	public boolean equals(Object object) {
		return object instanceof Pair<?, ?> pair && Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public String toString() {
		return "(" + left + ", " + right + ")";
	}
}
