package dynamilize;

/**
 * Variable calculator,
 * a functional interface used to process the current value of a variable and provide callbacks.
 */
public interface Calculator<T> {
    T calculate(T input);

    interface BoolCalculator {
        boolean calculate(boolean input);
    }

    interface ByteCalculator {
        byte calculate(byte input);
    }

    interface ShortCalculator {
        short calculate(short input);
    }

    interface IntCalculator {
        int calculate(int input);
    }

    interface LongCalculator {
        long calculate(long input);
    }

    interface FloatCalculator {
        float calculate(float input);
    }

    interface DoubleCalculator {
        double calculate(double input);
    }
}
