package edu.upc.fib.inlab.imp.kse.logics.services.creation.exceptions;

@Deprecated
public class UnrecognizedBuiltInOperator extends RuntimeException {
    public UnrecognizedBuiltInOperator(String symbol) {
        super("Unrecognized built-in operator: " + symbol);
    }
}
