package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

public class RepeatedConstraintID extends RuntimeException {
    public RepeatedConstraintID(ConstraintID id) {
        super("Repeated constraintID " + id + " in the schema");
    }
}
