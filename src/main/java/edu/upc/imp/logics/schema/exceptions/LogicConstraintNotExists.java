package edu.upc.imp.logics.schema.exceptions;

import edu.upc.imp.logics.schema.ConstraintID;

public class LogicConstraintNotExists extends RuntimeException {
    public LogicConstraintNotExists(ConstraintID constraintID) {
        super("LogicConstraint " + constraintID + " does not exist.");
    }
}
