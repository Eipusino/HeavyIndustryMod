package heavyindustry.util;

import kotlin.collections.*;

import java.util.*;

public class Collect {
	@SafeVarargs
	public static <T> List<T> listOf(T... elements) {
		return Arrays.asList(elements);
	}

	@SafeVarargs
	public static <T> List<T> mutableListOf(T... elements) {
		return CollectionsKt.mutableListOf(elements);
	}

	@SafeVarargs
	public static <T> List<T> arrayListOf(T... elements) {
		return CollectionsKt.arrayListOf(elements);
	}
}
