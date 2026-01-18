package heavyindustry.io

import arc.files.Fi
import arc.util.Log
import heavyindustry.func.ProvT
import heavyindustry.func.RunT
import heavyindustry.util.InLazy
import mindustry.Vars
import java.io.Closeable
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often enough to
 * require me to write a class for it.
 */
object BaseIO {
	@JvmField
	val messageDigest = InLazy { MessageDigest.getInstance("MD5") }

	@JvmStatic
	fun ioRunUnchecked(run: RunT<IOException>) {
		run.run()
	}

	@JvmStatic
	fun <T> ioSuppUnchecked(prov: ProvT<T, IOException>): T {
		return prov.get()
	}

	@JvmStatic
	fun getMD5(file: Fi): String {
		val messageDigest = messageDigest.value
		val buffer = ByteArray(8192)
		var input: FileInputStream? = null

		try {
			input = FileInputStream(file.file())

			var data: Int
			while ((input.read(buffer).also { data = it }) != -1) {
				messageDigest.update(buffer, 0, data)
			}
			return BigInteger(1, messageDigest.digest()).toString(16)
		} finally {
			close(input)
		}
	}

	/** Close and printing errors. */
	@JvmStatic
	fun close(c: Closeable?) {
		if (c != null) {
			try {
				c.close()
			} catch (e: Throwable) {
				Log.err(e)

				Vars.ui.showException(e)
			}
		}
	}
}
