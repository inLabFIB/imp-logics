package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;

public class NotExpectingConstraintIDException extends IMPLogicsException {

    public NotExpectingConstraintIDException() {
        super("Not expecting constraint ID");
    }
}
