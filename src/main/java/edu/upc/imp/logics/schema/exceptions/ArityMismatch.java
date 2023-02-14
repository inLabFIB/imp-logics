package edu.upc.imp.logics.schema.exceptions;

public class ArityMismatch extends RuntimeException {

    public ArityMismatch(int expected, int provided) {
        super("Expected arity " + expected + " but provided " + provided);
    }
}
