package edu.upc.imp.logics.schema.exceptions;

import edu.upc.imp.logics.schema.ConstraintID;

public class RepeatedContraintID extends RuntimeException{
    public RepeatedContraintID(ConstraintID id) {
        super("Repeated constraintID "+id+" in the schema");
    }
}
