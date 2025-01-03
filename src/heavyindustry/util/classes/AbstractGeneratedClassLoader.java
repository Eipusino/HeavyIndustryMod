package heavyindustry.util.classes;

import java.io.*;

public abstract class AbstractGeneratedClassLoader extends AbstractFileClassLoader {
    public AbstractGeneratedClassLoader(File file, ClassLoader parent){
        super(file, parent);
    }
}
