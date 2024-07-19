package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;

public class WrongNumberOfTermsInBuiltInLiteralException extends IMPLogicsException {

    public WrongNumberOfTermsInBuiltInLiteralException(int expected, int provided) {
        super("Expected " + expected + " terms but provided " + provided);
    }
}
