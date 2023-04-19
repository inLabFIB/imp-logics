package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public static Optional<ComparisonOperator> fromSymbol(String symbol) {
        return Optional.ofNullable(LOOKUP.get(symbol));
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

    public ComparisonOperator getNegatedOperator() {
        return switch (this) {
            case LESS_THAN -> GREATER_OR_EQUALS;
            case LESS_OR_EQUALS -> GREATER_THAN;
            case EQUALS -> NOT_EQUALS;
            case GREATER_THAN -> LESS_OR_EQUALS;
            case GREATER_OR_EQUALS -> LESS_THAN;
            case NOT_EQUALS -> EQUALS;
        };
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
