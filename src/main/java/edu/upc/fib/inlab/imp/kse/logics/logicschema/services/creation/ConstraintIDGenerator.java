package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintSpec;

/**
 * Interface responsible for obtaining a constraint ID for a given LogicConstraintSpec
 *
 * @param <T> kind of LogicConstraintSpec the interface works with
 */
public interface ConstraintIDGenerator<T extends LogicConstraintSpec> {

    ConstraintID newConstraintID(T lcs);
}
