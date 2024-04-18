package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;

public class ExpectingConstraintIDException extends IMPLogicsException {

    public ExpectingConstraintIDException() {
        super("Expecting constraint ID");
    }
}
