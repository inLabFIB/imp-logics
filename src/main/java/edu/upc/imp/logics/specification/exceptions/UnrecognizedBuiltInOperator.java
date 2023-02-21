package edu.upc.imp.logics.specification.exceptions;

public class UnrecognizedBuiltInOperator extends RuntimeException {
    public UnrecognizedBuiltInOperator(String symbol) {
        super("Unrecognized built-in operator: " + symbol);
    }
}
