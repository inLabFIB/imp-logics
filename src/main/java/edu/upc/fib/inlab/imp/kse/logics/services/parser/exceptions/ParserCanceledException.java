package edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions;

public class ParserCanceledException extends RuntimeException {
    public ParserCanceledException(String errorMessage) {
        super(errorMessage);
    }
}
