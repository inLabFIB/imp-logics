package edu.upc.fib.inlab.imp.kse.logics.schema.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

public class LogicConstraintNotExists extends RuntimeException {
    public LogicConstraintNotExists(ConstraintID constraintID) {
        super("LogicConstraint " + constraintID + " does not exist.");
    }
}
