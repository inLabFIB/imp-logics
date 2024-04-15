package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

public class RepeatedConstraintIDException extends IMPLogicsException {

    public RepeatedConstraintIDException(ConstraintID id) {
        super("Repeated constraintID " + id + " in the schema");
    }
}
