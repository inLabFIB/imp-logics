package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;

/**
 * Strategy for obtaining the ID of a new logic constraint consisting in taking the id from its specification
 */
public class UseSpecIDGenerator implements ConstraintIDGenerator<LogicConstraintWithIDSpec> {

    @Override
    public ConstraintID newConstraintID(LogicConstraintWithIDSpec lcs) {
        return new ConstraintID(lcs.getId());
    }
}
