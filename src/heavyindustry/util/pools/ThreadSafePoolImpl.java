package heavyindustry.util.pools;

import arc.func.*;

public class ThreadSafePoolImpl<T> extends ThreadSafePool<T> {
    public final Prov<T> provider;

    public ThreadSafePoolImpl(Prov<T> provider) {
        this.provider = provider;
    }

    public ThreadSafePoolImpl(int initialCapacity, Prov<T> provider) {
        super(initialCapacity);
        this.provider = provider;
    }

    public ThreadSafePoolImpl(int initialCapacity, int max, Prov<T> provider) {
        super(initialCapacity, max);
        this.provider = provider;
    }

    @Override
    protected T newObject() {
        return provider.get();
    }
}
