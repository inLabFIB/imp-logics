package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

/**
 * Thrown to indicate that a {@code Predicate} is not from a {@code LogicSchema} but from another one.
 */
public class PredicateOutsideSchemaException extends IMPLogicsException {

    /**
     * Constructs an {@code PredicateOutsideSchemaException} with one argument indicating the predicate from another
     * schema.
     * <p>
     * The predicate is included in this exception's detail message. The exact presentation format of the detail message
     * is unspecified.
     *
     * @param predicate predicate from another {@code LogicSchema}.
     */
    public PredicateOutsideSchemaException(Predicate predicate) {
        super("Predicate " + predicate.getName() + " exists, but is not from this schema");
    }
}
