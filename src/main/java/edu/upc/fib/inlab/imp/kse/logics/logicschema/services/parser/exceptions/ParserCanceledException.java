package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;

public class ParserCanceledException extends IMPLogicsException {

    public ParserCanceledException(String errorMessage) {
        super(errorMessage);
    }
}
