package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;

public class UseSpecIDGenerator implements ConstraintIDGenerator<LogicConstraintWithIDSpec> {

    @Override
    public ConstraintID newConstraintID(LogicConstraintWithIDSpec lcs) {
        return new ConstraintID(lcs.getId());
    }
}
