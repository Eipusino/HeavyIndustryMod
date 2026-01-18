package heavyindustry.io

import arc.util.io.Reads
import java.io.Closeable
import java.io.DataInput
import java.io.InputStream

/** A wrapper for DataInput with more concise method names and no IOExceptions. */
open class HReads(input: DataInput) : Reads(input) {
	/** @return -1 if EOF or unsupported, or the next byte. */
	override fun checkEOF(): Int {
		if (input is InputStream) {
			return (input as InputStream).read()
		}
		return -1
	}

	/** read long  */
	override fun l(): Long {
		return input.readLong()
	}

	/** read int */
	override fun i(): Int {
		return input.readInt()
	}

	/** read short */
	override fun s(): Short {
		return input.readShort()
	}

	/** read unsigned short */
	override fun us(): Int {
		return input.readUnsignedShort()
	}

	/** read byte */
	override fun b(): Byte {
		return input.readByte()
	}

	/** allocate & read byte array */
	override fun b(length: Int): ByteArray {
		val array = ByteArray(length)
		input.readFully(array)
		return array
	}

	/** read byte array */
	override fun b(array: ByteArray): ByteArray {
		input.readFully(array)
		return array
	}

	/** read byte array w/ offset */
	override fun b(array: ByteArray, offset: Int, length: Int): ByteArray {
		input.readFully(array, offset, length)
		return array
	}

	/** read unsigned byte */
	override fun ub(): Int {
		return input.readUnsignedByte()
	}

	/** read boolean */
	override fun bool(): Boolean {
		return input.readBoolean()
	}

	/** read float */
	override fun f(): Float {
		return input.readFloat()
	}

	/** read double */
	override fun d(): Double {
		return input.readDouble()
	}

	/** read string (UTF) */
	override fun str(): String {
		return input.readUTF()
	}

	/** skip bytes */
	override fun skip(amount: Int) {
		input.skipBytes(amount)
	}

	override fun close() {
		if (input is Closeable) {
			(input as Closeable).close()
		}
	}
}