package endfield.core;

import arc.ApplicationListener;
import arc.files.Fi;

public interface IListener extends ApplicationListener {
	@Override
	default void init() {}

	@Override
	default void resize(int width, int height) {}

	@Override
	default void update() {}

	@Override
	default void pause() {}

	@Override
	default void resume() {}

	@Override
	default void dispose() {}

	@Override
	default void exit() {}

	@Override
	default void fileDropped(Fi file) {}
}
