package heavyindustry.util;

import arc.util.*;

public final class Stringf {
	/** Don't let anyone instantiate this class. */
	private Stringf() {}

	public static boolean canParseLong(String s) {
		return parseLong(s) != null;
	}

	@Nullable
	public static Long parseLong(String s) {
		return parseLong(s, 10);
	}

	@Nullable
	public static Long parseLong(String s, int radix) {
		return parseLong(s, radix, 0, s.length());
	}

	@Nullable
	public static Long parseLong(String s, int radix, int start, int end) {
		boolean negative = false;
		int i = start, len = end - start;
		long limit = -9223372036854775807l;
		if (len <= 0) {
			return null;
		} else {
			char firstChar = s.charAt(i);
			if (firstChar < '0') {
				if (firstChar == '-') {
					negative = true;
					limit = -9223372036854775808l;
				} else if (firstChar != '+') {
					return null;
				}

				if (len == 1) return null;

				++i;
			}

			long result;
			int digit;
			for (result = 0L; i < end; result -= digit) {
				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					return null;
				}

				result *= radix;
				if (result < limit + (long) digit) {
					return null;
				}
			}

			return negative ? result : -result;
		}
	}
}
