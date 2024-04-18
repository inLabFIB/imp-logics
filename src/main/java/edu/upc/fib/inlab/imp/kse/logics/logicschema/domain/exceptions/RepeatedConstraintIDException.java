package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;

/**
 * Thrown to indicate that a {@code ConstraintID} is repeated inside a {@code LogicSchema}.
 */
public class RepeatedConstraintIDException extends IMPLogicsException {

    /**
     * Constructs an {@code RepeatedConstraintIDException} with one argument indicating the repeated
     * {@code ConstraintID}.
     * <p>
     * The {@code ConstraintID} is included in this exception's detail message. The exact presentation format of the
     * detail message is unspecified.
     *
     * @param id    repeated {@code ConstraintID}.
     */
    public RepeatedConstraintIDException(ConstraintID id) {
        super("Repeated constraintID " + id + " in the schema");
    }
}
