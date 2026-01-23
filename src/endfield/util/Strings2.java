package endfield.util;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class Strings2 {
	static final String[] byteUnit = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"};

	static final String printableString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
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

	/**
	 * The {@code char} version of {@code String.repeat(int)}.
	 *
	 * @throws IllegalArgumentException if the {@code count} is negative.
	 */
	@Contract(pure = true)
	public static @NotNull String repeat(char key, int count) {
		if (count < 0) throw new IllegalArgumentException("count is negative: " + count);

		char[] data = new char[count];
		Arrays.fill(data, key);

		return String.valueOf(data);
	}

	/**
	 * {@code String.repeat(int)}.
	 *
	 * @throws IllegalArgumentException if the {@code count} is negative.
	 */
	@Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
	public static String repeat(@Nullable String key, int count) {
		if (key == null) return null;

		StringBuilder builder = new StringBuilder(key.length() * count);
		for (int i = 0; i < count; i++) {
			builder.append(key);
		}
		return builder.toString();
		//return key.repeat(count);
	}

	/** Determine whether the string is composed entirely of numbers. */
	@Contract(value = "null -> false", pure = true)
	public static boolean isNumeric4(@Nullable String key) {
		if (numeric4 == null) numeric4 = Pattern.compile("\\d+");

		return key != null && numeric4.matcher(key).matches();
	}

	/** Determine whether the string is composed of {@code Number} and {@code . }. */
	@Contract(value = "null -> false", pure = true)
	public static boolean isNumeric(@Nullable String key) {
		if (key == null) return false;

		if (numeric == null) numeric = Pattern.compile("[0-9]*");

		int index = key.indexOf('.');
		if (index > 0) {//Determine if there is a decimal point
			if (index == key.lastIndexOf('.') && key.split("\\.").length == 2) { //Determine if there is only one decimal point
				return numeric.matcher(key.replace(".", "")).matches();
			} else {
				return false;
			}
		} else {
			return numeric.matcher(key).matches();
		}
	}

	/** Randomly generate a string of length within the specified range. */
	@Contract(pure = true)
	public static @NotNull String generateRandomString(int min, int max) {
		if (min < 0 || max < min || max > 1000000) return Core.bundle.format("text.generate-random-string-2", min, max);

		int length = min + Mathf.random(max - min + 1);
		return generateRandomString(length);
	}

	/**
	 * Randomly generate a string of specified length.
	 *
	 * @throws NegativeArraySizeException If the {@code length} is negative.
	 */
	@Contract(pure = true)
	public static @NotNull String generateRandomString(int length) {
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
	public static @NotNull String toByteFixNonUnit(double number, int retain) {
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
	public static @NotNull String toByteFix(double number, int retain) {
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

		String str = Double.toString(number / base);
		int dotIndex = str.indexOf('.');

		String integerPart = str.substring(0, dotIndex);
		String fractionalPart = str.substring(dotIndex + 1);

		int realRetain = Math.min(retain, fractionalPart.length());

		StringBuilder builder = new StringBuilder();
		if (isNegative) {
			builder.append('-');
		}
		builder.append(integerPart);
		if (retain != 0) {
			builder.append('.');
			builder.append(fractionalPart, 0, realRetain);
			for (int i = 0; i < Math.max(0, retain - realRetain); i++) {
				builder.append('0');
			}
		}
		return builder.toString();

		/*int index = 0;
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

		return (isNegative ? "-" : "") + arr[0] + (retain == 0 ? "" : "." + arr[1].substring(0, realRetain) + end + byteUnit[index]);*/
	}

	public static @NotNull String toStoreSize(float num) {
		float v = num;
		int n = 0;

		while (v > 1024) {
			v /= 1024;
			n++;
		}

		return Strings.fixed(v, 2) + "[lightgray]" + byteUnit[n];
	}
}
