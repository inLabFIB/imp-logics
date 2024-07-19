package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@code Implementation of binary arithmetic comparison operators such as <, or <=.}
 */
public enum ComparisonOperator {

    LESS_THAN("<"),
    LESS_OR_EQUALS("<="),
    EQUALS("="),
    GREATER_THAN(">"),
    GREATER_OR_EQUALS(">="),
    NOT_EQUALS("<>");

    private static final Map<String, ComparisonOperator> LOOKUP = new HashMap<>();
    private static final Map<ComparisonOperator, ComparisonOperator> SYMMETRIC =
            Map.of(
                    LESS_THAN, GREATER_THAN,
                    LESS_OR_EQUALS, GREATER_OR_EQUALS,
                    EQUALS, EQUALS,
                    GREATER_THAN, LESS_THAN,
                    GREATER_OR_EQUALS, LESS_OR_EQUALS,
                    NOT_EQUALS, NOT_EQUALS
            );

    private static final Map<ComparisonOperator, ComparisonOperator> NEGATED =
            Map.of(
                    LESS_THAN, GREATER_OR_EQUALS,
                    LESS_OR_EQUALS, GREATER_THAN,
                    EQUALS, NOT_EQUALS,
                    GREATER_THAN, LESS_OR_EQUALS,
                    GREATER_OR_EQUALS, LESS_THAN,
                    NOT_EQUALS, EQUALS
            );

    static {
        for (ComparisonOperator op : ComparisonOperator.values()) {
            LOOKUP.put(op.getSymbol(), op);
        }
    }

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public static Optional<ComparisonOperator> fromSymbol(String symbol) {
        return Optional.ofNullable(LOOKUP.get(symbol));
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isSymmetric(ComparisonOperator rangeOperator) {
        return this.equals(rangeOperator.getSymmetric());
    }

    public ComparisonOperator getSymmetric() {
        return SYMMETRIC.get(this);
    }

    public ComparisonOperator getNegatedOperator() {
        return NEGATED.get(this);
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
