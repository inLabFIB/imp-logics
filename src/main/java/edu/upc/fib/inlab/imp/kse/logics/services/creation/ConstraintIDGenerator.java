package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintSpec;

/**
 * Interface responsible for obtaining a constraint ID for a given LogicConstraintSpec
 *
 * @param <T> kind of LogicConstraintSpec the interface works with
 */
public interface ConstraintIDGenerator<T extends LogicConstraintSpec> {

    ConstraintID newConstraintID(T lcs);
}
