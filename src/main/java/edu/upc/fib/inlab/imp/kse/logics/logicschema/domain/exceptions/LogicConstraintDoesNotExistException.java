package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

public class LogicConstraintDoesNotExistException extends IMPLogicsException {

    public LogicConstraintDoesNotExistException(ConstraintID constraintID) {
        super("LogicConstraint " + constraintID + " does not exist.");
    }
}
