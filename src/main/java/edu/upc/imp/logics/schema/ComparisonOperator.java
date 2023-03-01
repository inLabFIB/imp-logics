package edu.upc.imp.logics.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of binary arithmetic comparison operators such as <, or <=.
 */
public enum ComparisonOperator {

    LESS_THAN("<"),
    LESS_OR_EQUALS("<="),
    EQUALS("="),
    GREATER_THAN(">"),
    GREATER_OR_EQUALS(">="),
    NOT_EQUALS("<>");

    private static final Map<String, ComparisonOperator> LOOKUP = new HashMap<>();

    static {
        for (ComparisonOperator op : ComparisonOperator.values()) {
            LOOKUP.put(op.getSymbol(), op);
        }
    }

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static ComparisonOperator fromSymbol(String symbol) {
        return LOOKUP.get(symbol);
    }

    public boolean isSymmetric(ComparisonOperator rangeOperator) {
        return switch (this) {
            case LESS_THAN -> rangeOperator.equals(GREATER_THAN);
            case LESS_OR_EQUALS -> rangeOperator.equals(GREATER_OR_EQUALS);
            case EQUALS -> rangeOperator.equals(EQUALS);
            case GREATER_THAN -> rangeOperator.equals(LESS_THAN);
            case GREATER_OR_EQUALS -> rangeOperator.equals(LESS_OR_EQUALS);
            case NOT_EQUALS -> rangeOperator.equals(NOT_EQUALS);
        };
    }
}
