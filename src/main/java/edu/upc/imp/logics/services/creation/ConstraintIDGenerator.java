package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;

public interface ConstraintIDGenerator<T extends LogicConstraintSpec> {

    ConstraintID newConstraintID(T lcs);
}
