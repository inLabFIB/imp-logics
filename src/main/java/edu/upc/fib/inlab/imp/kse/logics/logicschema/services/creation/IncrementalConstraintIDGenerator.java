package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithoutIDSpec;

/**
 * Incremental strategy for obtaining the IDs of newly created logic constraints. It will return identifiers starting
 * from a given start (1 by default). E.g.: 1, 2, 3, ...
 */
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
        return newConstraintID();
    }

    public ConstraintID newConstraintID() {
        return new ConstraintID(Integer.toString(nextID++));
    }

}
