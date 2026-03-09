package endfield.util;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.func.ConsT;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.g2d.TextureRegion;
import arc.util.Http;
import arc.util.Log;
import endfield.util.concurrent.AtomicFloat;
import endfield.util.holder.ObjectHolder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class URLDownloader {
	static final CollectionOrderedMap<String, String> urlReplacers = new CollectionOrderedMap<>(String.class, String.class);

	/** Don't let anyone instantiate this class. */
	private URLDownloader() {}

	public static void setMirror(String source, String to) {
		urlReplacers.put(source, to);
	}

	public static void removeMirror(String source) {
		urlReplacers.remove(source);
	}

	public static void clearMirrors() {
		urlReplacers.clear();
	}

	public static void retryDown(String url, ConsT<Http.HttpResponse, Exception> resultHandler, int maxRetry, Cons<Throwable> errHandler) {
		AtomicInteger counter = new AtomicInteger();
		AtomicReference<Runnable> get = new AtomicReference<>();

		for (ObjectHolder<String, String> entry : urlReplacers) {
			if (url.startsWith(entry.key)) {
				url = url.replaceFirst(entry.key, entry.value);
			}
		}

		String realUrl = url;
		get.set(() -> Http.get(realUrl, resultHandler, e -> {
			if (counter.getAndIncrement() <= maxRetry) get.get().run();
			else errHandler.get(e);
		}));
		get.get().run();
	}

	public static AtomicFloat downloadToStream(String url, OutputStream stream) {
		AtomicFloat progress = new AtomicFloat();
		retryDown(url, res -> {
			try (stream) {
				InputStream in = res.getResultAsStream();
				long total = res.getContentLength();

				int curr = 0;
				for (int b = in.read(); b != -1; b = in.read()) {
					curr++;
					stream.write(b);
					progress.set((float) curr / total);
				}
			}
		}, 5, Log::err);
		return progress;
	}

	public static AtomicFloat downloadToFile(String url, Fi file) {
		return downloadToStream(url, file.write());
	}

	public static TextureRegion downloadImage(String url, TextureRegion errDef) {
		TextureRegion result = new TextureRegion(errDef);

		retryDown(url, res -> {
			Pixmap pixmap = new Pixmap(res.getResult());
			Core.app.post(() -> {
				try {
					Texture texture = new Texture(pixmap);
					texture.setFilter(TextureFilter.linear);
					result.set(texture);
					pixmap.dispose();
				} catch (Exception e) {
					Log.err(e);
				}
			});
		}, 5, Log::err);

		return result;
	}
}
