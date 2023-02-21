package edu.upc.imp.logics.specification.exceptions;

public class WrongNumberOfTermsInBuiltInLiteral extends RuntimeException {
    public WrongNumberOfTermsInBuiltInLiteral(int expected, int provided) {
        super("Expected " + expected + " terms but provided " + provided);
    }
}
