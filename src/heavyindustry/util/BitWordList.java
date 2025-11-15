package heavyindustry.util;

public class BitWordList {
	public final int initialWordsAmount;

	protected final byte wordLen;
	protected final short wordMask;
	protected long[] bits = {0};

	/**
	 * Creates a bit set whose initial size is large enough to explicitly represent bits with indices in the range 0 through
	 * nbits-1.
	 *
	 * @param nwords the initial size of the word set
	 */
	public BitWordList(int nwords, WordLength wordLen) {
		checkCapacity(nwords * wordLen.value >>> 6);
		initialWordsAmount = nwords;

		this.wordLen = wordLen.value;
		wordMask = (short) ~(~1 << (wordLen.value - 1));
	}

	/** Sets this bits to have the same bits as another. Both sets should have the same length. */
	public void set(BitWordList other) {
		int length = Math.min(bits.length, other.bits.length);
		System.arraycopy(other.bits, 0, bits, 0, length);
	}

	/**
	 * @param index the index of the bit
	 * @return whether the bit is set
	 * @throws ArrayIndexOutOfBoundsException if index < 0
	 */
	public byte get(int index) {
		final int wordPosition = index * wordLen >>> 6;
		if (wordPosition >= bits.length) {
			throw new IndexOutOfBoundsException();
		}
		index = (index * wordLen) & 63;
		return (byte) (bits[wordPosition] >>> index & wordMask);
	}

	/**
	 * @param index the index of the bit to set
	 * @throws ArrayIndexOutOfBoundsException if index < 0
	 */
	public void set(int index, byte number) {
		final int wordIndex = index * wordLen >>> 6;

		index = (index * wordLen) & 63;
		number &= wordMask;
		checkCapacity(wordIndex);
		bits[wordIndex] &= ~((long) wordMask << index);
		bits[wordIndex] |= (long) number << index;
	}

	protected void checkCapacity(int len) {
		if (len >= bits.length) {
			long[] newBits = new long[len + 1];
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			bits = newBits;
		}
	}

	/** Clears the entire bitset */
	public void clear() {
		long[] bits = this.bits;
		int length = bits.length;
		for (int i = 0; i < length; i++) {
			bits[i] = 0L;
		}
	}

	/** @return the number of bits currently stored, <b>not</b> the highset set bit! */
	public int size() {
		return bits.length << 6 / wordLen;
	}

	/**
	 * Returns the "logical size" of this bitset: the index of the highest set bit in the bitset plus one. Returns zero if the
	 * bitset contains no set bits.
	 *
	 * @return the logical size of this bitset
	 */
	public int nonZeroLength() {
		long[] bits = this.bits;
		for (int word = bits.length - 1; word >= 0; --word) {
			long bitsAtWord = bits[word];
			if (bitsAtWord != 0) {
				for (int bit = 63; bit >= 0; bit -= wordLen) {
					if ((bitsAtWord & ((long) wordMask << bit)) != 0L) {
						return ((word << 6) + bit + 1) / wordLen;
					}
				}
			}
		}
		return 0;
	}

	/** @return true if this bitset contains no bits that are set to true */
	public boolean isEmpty() {
		for (long bit : bits) {
			if (bit != 0L) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int word = nonZeroLength() >>> 6;
		int hash = 0;
		for (int i = 0; word >= i; i++) {
			hash = 127 * hash + Objects2.hashCodeLong(bits[i]);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof BitWordList other)) return false;

		long[] otherBits = other.bits;

		int commonWords = Math.min(bits.length, otherBits.length);
		for (int i = 0; commonWords > i; i++) {
			if (bits[i] != otherBits[i]) {
				return false;
			}
		}
		if (bits.length == otherBits.length)
			return true;


		return nonZeroLength() == other.nonZeroLength();
	}


	/**
	 * for one bit use {@link arc.struct.Bits}
	 * for eight bits use {@link arc.struct.ByteSeq}
	 * for sixteen bits use {@link arc.struct.ShortSeq}
	 * for thirty-two bits use {@link arc.struct.IntSeq}
	 * for sixty-four two bits use {@link arc.struct.LongSeq}
	 */
	public enum WordLength {
		two(2), four(4);

		public final byte value;

		public static final WordLength[] all = values();

		WordLength(int i) {
			value = (byte) i;
		}
	}
}
