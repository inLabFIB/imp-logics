package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

import java.util.List;

/**
 * Interface for generating several constraintIDs from a single constraintID.
 * This is though to be used in processes that transform one LogicConstraint to several LogicConstraints.
 */
public interface MultipleConstraintIDGenerator {
    /**
     * Generates a list of constraintIDs from a single constraintID.
     *
     * @param originalID          the original constraintID
     * @param numberOfRequiredIDs the number of constraintIDs to be generated
     * @return a list of constraintIDs
     */
    List<ConstraintID> generateNewConstraintsIDs(ConstraintID originalID, int numberOfRequiredIDs);

}
