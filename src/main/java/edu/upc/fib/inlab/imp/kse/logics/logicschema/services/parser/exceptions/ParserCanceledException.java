package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions;

public class ParserCanceledException extends RuntimeException {
    public ParserCanceledException(String errorMessage) {
        super(errorMessage);
    }
}
