package heavyindustry.io

import arc.util.io.Writes
import java.io.Closeable
import java.io.DataOutput

open class HWrites(output: DataOutput) : Writes(output) {
	/** write long  */
	override fun l(i: Long) {
		output.writeLong(i)
	}

	/** write int  */
	override fun i(i: Int) {
		output.writeInt(i)
	}

	/** write byte  */
	override fun b(i: Int) {
		output.writeByte(i)
	}

	/** write bytes  */
	override fun b(array: ByteArray, offset: Int, length: Int) {
		output.write(array, offset, length)
	}

	/** write short  */
	override fun s(i: Int) {
		output.writeShort(i)
	}

	/** write float  */
	override fun f(f: Float) {
		output.writeFloat(f)
	}

	/** write double  */
	override fun d(d: Double) {
		output.writeDouble(d)
	}

	/** writes a string (UTF)  */
	override fun str(str: String) {
		output.writeUTF(str)
	}

	override fun close() {
		if (output is Closeable) {
			(output as Closeable).close()
		}
	}
}