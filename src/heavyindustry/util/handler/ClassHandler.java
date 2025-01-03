package heavyindustry.util.handler;

import dynamilize.*;
import dynamilize.classmaker.*;
import heavyindustry.util.classes.*;

public interface ClassHandler {
    ClassHandler newInstance(Class<?> modMain);

    AbstractClassGenerator getGenerator();

    DynamicMaker getDynamicMaker();

    AbstractFileClassLoader currLoader();

    void finishGenerate();
}
