package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.ConstraintID;

public class IncrementalConstraintIDGenerator implements ConstraintIDGenerator {
    private int nextID;

    public IncrementalConstraintIDGenerator(int start) {
        nextID = start;
    }

    @Override
    public ConstraintID newConstraintID() {
        return new ConstraintID(Integer.toString(nextID++));
    }
}
