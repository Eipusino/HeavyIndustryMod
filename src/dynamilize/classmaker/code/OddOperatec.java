package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface OddOperatec<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitOddOperate(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.ODDOPERATE;
    }

    Localc<T> operateNumber();

    Localc<T> resultTo();

    OddOperator opCode();

    enum OddOperator {
        NEGATIVE("-"),
        BITNOR("~");

        private static final Map<String, OddOperator> symbolMap = new HashMap<>();

        static {
            for (OddOperator opc: values()) {
                symbolMap.put(opc.symbol, opc);
            }
        }

        private final String symbol;

        OddOperator(String sym) {
            this.symbol = sym;
        }

        public static OddOperator as(String symbol) {
            return symbolMap.computeIfAbsent(symbol, e -> {throw new IllegalArgumentException("unknown operator symbol: " + e);});
        }
    }
}
