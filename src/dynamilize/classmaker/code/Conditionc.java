package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface Conditionc extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitCondition(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.CONDITION;
    }

    CondCode condCode();

    Localc<?> condition();

    Label ifJump();

    enum CondCode {
        EQUAL("=="),
        UNEQUAL("!="),
        MORE(">"),
        LESS("<"),
        MOREOREQUAL(">="),
        LESSOREQUAL("<=");

        private static final Map<String, CondCode> symbolMap = new HashMap<>();

        static {
            for (CondCode opc: values()) {
                symbolMap.put(opc.symbol, opc);
            }
        }

        private final String symbol;

        CondCode(String sym) {
            this.symbol = sym;
        }

        public static CondCode as(String symbol) {
            return symbolMap.computeIfAbsent(symbol, e -> {
                throw new IllegalArgumentException("unknown operator symbol: " + e);
            });
        }
    }
}
