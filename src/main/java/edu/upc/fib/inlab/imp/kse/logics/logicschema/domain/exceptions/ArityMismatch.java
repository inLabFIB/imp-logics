package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class ArityMismatch extends RuntimeException {

    public ArityMismatch(int expected, int provided) {
        super("Expected arity " + expected + " but provided " + provided);
    }
}
