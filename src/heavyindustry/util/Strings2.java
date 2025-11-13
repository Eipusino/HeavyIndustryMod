package heavyindustry.util;

import arc.math.Mathf;
import arc.util.Strings;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class Strings2 {
	static final String[] byteUnit = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"};

	static final char[] printableChars = {
			' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			':', ';', '<', '=', '>', '?', '@',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'[', '\\', ']', '^', '_', '`',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'{', '|', '}', '~'
	};

	static Pattern numeric, numeric4;

	private Strings2() {}

	public static String repeat(char key, int count) {
		if (count <= 0) return "";

		char[] data = new char[count];
		Arrays.fill(data, key);

		return String.valueOf(data);
	}

	/** @see String#repeat(int) */
	public static String repeat(String key, int count) {
		if (key == null) return null;
		if (count <= 0 || key.isEmpty()) return "";
		if (count == 1) return key;

		StringBuilder builder = new StringBuilder(key.length() * count);
		for (int i = 0; i < count; i++) {
			builder.append(key);
		}
		return builder.toString();
	}

	/** Determine whether the string is composed entirely of numbers. */
	public static boolean isNumeric4(String key) {
		if (numeric4 == null) numeric4 = Pattern.compile("\\d+");

		return key != null && numeric4.matcher(key).matches();
	}

	/** Determine whether the string is composed of {@code Number} and {@code . }. */
	public static boolean isNumeric(String key) {
		if (key == null) return false;

		if (numeric == null) numeric = Pattern.compile("[0-9]*");

		int index = key.indexOf(".");
		if (index > 0) {//Determine if there is a decimal point
			if (index == key.lastIndexOf(".") && key.split("\\.").length == 2) { //Determine if there is only one decimal point
				return numeric.matcher(key.replace(".", "")).matches();
			} else {
				return false;
			}
		} else {
			return numeric.matcher(key).matches();
		}
	}

	/** Randomly generate a string of length within the specified range. */
	public static String generateRandomString(int min, int max) {
		if (min < 0 || max < min || max > 1000000) return "";

		int length = min + Mathf.random(max - min + 1);
		return generateRandomString(length);
	}

	/**
	 * Randomly generate a string of specified length.
	 *
	 * @throws NegativeArraySizeException If the length is negative.
	 */
	public static String generateRandomString(int length) {
		char[] chars = new char[length];
		int range = printableChars.length - 1;

		for (int i = 0; i < length; i++) {
			chars[i] = printableChars[Mathf.random(range)];
		}
		return String.valueOf(chars);
	}

	/**
	 * Convert numbers to computer storage capacity count representation without units.
	 *
	 * @param number The number to be converted
	 * @param retain Reserved decimal places
	 */
	public static String toByteFixNonUnit(double number, int retain) {
		boolean isNegative = false;
		if (number < 0) {
			number = -number;
			isNegative = true;
		}

		double base = 1d;
		for (int i = 0; i < byteUnit.length; i++) {
			if (base * 1024 > number) {
				break;
			}
			base *= 1024;
		}

		String[] arr = Double.toString(number / base).split("\\.");
		int realRetain = Math.min(retain, arr[1].length());

		String end = repeat('0', Math.max(0, retain - realRetain));

		return (isNegative ? "-" : "") + arr[0] + (retain == 0 ? "" : "." + arr[1].substring(0, realRetain) + end);
	}

	/**
	 * Convert numbers to computer storage capacity count representation.
	 *
	 * @param number The number to be converted
	 * @param retain Reserved decimal places
	 */
	public static String toByteFix(double number, int retain) {
		boolean isNegative = false;
		if (number < 0) {
			number = -number;
			isNegative = true;
		}

		int index = 0;
		double base = 1;
		for (int i = 0; i < byteUnit.length; i++) {
			if (base * 1024 > number) {
				break;
			}
			base *= 1024;
			index++;
		}

		String[] arr = Double.toString(number / base).split("\\.");
		int realRetain = Math.min(retain, arr[1].length());

		String end = repeat('0', Math.max(0, retain - realRetain));

		return (isNegative ? "-" : "") + arr[0] + (retain == 0 ? "" : "." + arr[1].substring(0, realRetain) + end + byteUnit[index]);
	}

	public static String toStoreSize(float num) {
		float v = num;
		int n = 0;

		while (v > 1024) {
			v /= 1024;
			n++;
		}

		return Strings.fixed(v, 2) + "[lightgray]" + byteUnit[n];
	}

	public static boolean canParseLong(String s, long def) {
		return parseLong(s, def) != def;
	}

	public static long parseLong(String s) {
		return parseLong(s, 10, 0);
	}

	public static long parseLong(String s, long def) {
		return parseLong(s, 10, def);
	}

	public static long parseLong(String s, int radix, long def) {
		return parseLong(s, radix, 0, s.length(), def);
	}

	public static long parseLong(String s, int radix, int start, int end, long def) {
		boolean negative = false;
		int i = start, len = end - start;
		long limit = Long.MIN_VALUE + 1;
		if (len <= 0 || s == null) {
			return def;
		} else {
			char firstChar = s.charAt(i);
			if (firstChar < '0') {
				if (firstChar == '-') {
					negative = true;
					limit = Long.MIN_VALUE;
				} else if (firstChar != '+') {
					return def;
				}

				if (len == 1) return def;

				++i;
			}

			long result;
			int digit;
			for (result = 0; i < end; result -= digit) {
				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					return def;
				}

				result *= radix;
				if (result < limit + digit) {
					return def;
				}
			}

			return negative ? result : -result;
		}
	}
}
