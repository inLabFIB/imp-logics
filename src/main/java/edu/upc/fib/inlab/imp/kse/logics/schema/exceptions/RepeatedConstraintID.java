package edu.upc.fib.inlab.imp.kse.logics.schema.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

public class RepeatedConstraintID extends RuntimeException {
    public RepeatedConstraintID(ConstraintID id) {
        super("Repeated constraintID " + id + " in the schema");
    }
}
