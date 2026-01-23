package endfield.util

object StringsKt {
	@JvmStatic
	fun repeat(seq: CharSequence, n: Int): String {
		require(n >= 0) { "Count 'n' must be non-negative, but was $n." }

		return when (n) {
			0 -> ""
			1 -> seq.toString()
			else -> {
				when (seq.length) {
					0 -> ""
					1 -> seq.get(0).let { char -> java.lang.String.valueOf(CharArray(n) { char }) }
					else -> {
						val builder = StringBuilder(n * seq.length)
						for (i in 1..n) {
							builder.append(seq)
						}
						builder.toString()
					}
				}
			}
		}
	}
}