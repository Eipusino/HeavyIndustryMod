package heavyindustry.ui.defaults;

import arc.func.*;

class Lazy<T> {
    private static final Object UNINITIALIZED_VALUE = new Object();
    private final Object lock = new Object();
    private Prov<T> initializer;
    private Object _value = UNINITIALIZED_VALUE;

    Lazy(Prov<T> initializer) {
        this.initializer = initializer;
    }

    public void set(T value) {
        _value = value;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        Object _v1 = _value;
        if (_v1 != UNINITIALIZED_VALUE) {
            //noinspection unchecked
            return (T) _v1;
        }
        synchronized (lock) {
            Object _v2 = _value;
            if (_v2 != UNINITIALIZED_VALUE) {
                //noinspection unchecked
                return (T) _v2;
            }
            T typedValue = initializer.get();
            _value = typedValue;
            initializer = null;
            return typedValue;
        }
    }
}
