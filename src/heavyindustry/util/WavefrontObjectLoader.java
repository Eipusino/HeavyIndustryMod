/*
package heavyindustry.util;

import arc.assets.AssetDescriptor;
import arc.assets.AssetLoaderParameters;
import arc.assets.AssetManager;
import arc.assets.loaders.AsynchronousAssetLoader;
import arc.assets.loaders.FileHandleResolver;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Nullable;

// @since 1.0.7
public class WavefrontObjectLoader extends AsynchronousAssetLoader<WavefrontObject, WavefrontObjectLoader.WavefrontObjectParameters> {
	private WavefrontObject object;

	public WavefrontObjectLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager assetManager, String fileName, Fi file, WavefrontObjectParameters parameter) {
		Fi material = file.parent().child(file.nameWithoutExtension() + ".mtl");
		if (!material.exists()) material = null;

		if (parameter != null && parameter.object != null) {
			(object = parameter.object).load(file, material);
		} else {
			object = new WavefrontObject();
			object.load(file, material);
		}
	}

	@Override
	public WavefrontObject loadSync(AssetManager assetManager, String fileName, Fi file, WavefrontObjectParameters parameter) {
		WavefrontObject object = this.object;
		this.object = null;
		return object;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, WavefrontObjectParameters parameter) {
		return null;
	}

	public static class WavefrontObjectParameters extends AssetLoaderParameters<WavefrontObject> {
		public @Nullable WavefrontObject object;

		public WavefrontObjectParameters(@Nullable WavefrontObject object) {
			this.object = object;
		}
	}
}
*/
