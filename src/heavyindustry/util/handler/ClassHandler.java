package heavyindustry.util.handler;

import dynamilize.DynamicMaker;
import dynamilize.classmaker.AbstractClassGenerator;
import heavyindustry.util.AbstractFileClassLoader;

public interface ClassHandler {
	ClassHandler newInstance(Class<?> modMain);

	AbstractClassGenerator getGenerator();

	DynamicMaker getDynamicMaker();

	AbstractFileClassLoader currLoader();

	void finishGenerate();
}
