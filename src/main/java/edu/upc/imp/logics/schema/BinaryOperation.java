package edu.upc.imp.logics.schema;

/**
 * Implementation of binary arithmetic comparison operators such as <, <=.
 */
public enum BinaryOperation {
    LESS_THAN("<"),
    LESS_OR_EQUALS("<="),
    EQUALS("="),
    GREATER_THAN(">"),
    GREATER_OR_EQUALS(">="),
    NOT_EQUALS("<>");

    private final String symbol;

    BinaryOperation(String symbol) {
        this.symbol = symbol;
    }
}
