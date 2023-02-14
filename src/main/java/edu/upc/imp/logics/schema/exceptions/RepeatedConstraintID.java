package edu.upc.imp.logics.schema.exceptions;

import edu.upc.imp.logics.schema.ConstraintID;

public class RepeatedConstraintID extends RuntimeException {
    public RepeatedConstraintID(ConstraintID id) {
        super("Repeated constraintID " + id + " in the schema");
    }
}
