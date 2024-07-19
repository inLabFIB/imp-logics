package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

/**
 * Thrown to indicate that a {@code LogicConstraint} does not exist in a logic schema.
 */
public class LogicConstraintNotFoundException extends IMPLogicsException {

    /**
     * Constructs an {@code LogicConstraintNotFoundException} with one argument indicating the nonexistent
     * constraintID.
     * <p>
     * The constraintID is included in this exception's detail message. The exact presentation format of the detail
     * message is unspecified.
     *
     * @param constraintID the constraintID of the nonexistent logic constraint.
     */
    public LogicConstraintNotFoundException(ConstraintID constraintID) {
        super("LogicConstraint " + constraintID + " does not exist.");
    }
}
