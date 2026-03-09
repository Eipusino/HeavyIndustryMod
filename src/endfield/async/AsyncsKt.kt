package endfield.async

import arc.Core
import arc.func.Prov
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicReference

/**
 * @since 1.0.9
 * @see Asyncs
 */
object AsyncsKt {
	@JvmStatic
	fun <T> get(future: Future<T>): T {
		return future.get()
	}

	@JvmStatic
	fun postWait(runSync: Runnable) {
		val flag = Semaphore(0)
		Core.app.post {
			try {
				runSync.run()
			} finally {
				flag.release()
			}
		}

		flag.acquire()
	}

	@JvmStatic
	fun <T> postWait(runSync: Prov<T>): T {
		val flag = Semaphore(0)
		val out = AtomicReference<T>()
		Core.app.post {
			try {
				out.set(runSync.get())
			} finally {
				flag.release()
			}
		}

		flag.acquire()

		return out.get()
	}
}