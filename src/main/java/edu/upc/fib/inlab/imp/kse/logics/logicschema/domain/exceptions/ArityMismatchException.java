package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class ArityMismatchException extends IMPLogicsException {

    public ArityMismatchException(int expected, int provided) {
        super("Expected arity " + expected + " but provided " + provided);
    }
}
