package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;

public class IncrementalConstraintIDGenerator implements ConstraintIDGenerator<LogicConstraintWithoutIDSpec> {
    private int nextID;

    public IncrementalConstraintIDGenerator() {
        this(1);
    }

    public IncrementalConstraintIDGenerator(int start) {
        nextID = start;
    }

    @Override
    public ConstraintID newConstraintID(LogicConstraintWithoutIDSpec lcs) {
        return new ConstraintID(Integer.toString(nextID++));
    }
}
