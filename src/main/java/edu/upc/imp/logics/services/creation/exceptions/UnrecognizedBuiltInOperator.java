package edu.upc.imp.logics.services.creation.exceptions;

public class UnrecognizedBuiltInOperator extends RuntimeException {
    public UnrecognizedBuiltInOperator(String symbol) {
        super("Unrecognized built-in operator: " + symbol);
    }
}
