package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

/**
 * Thrown to indicate that a {@code Predicate} is not derived.
 */
public class PredicateIsNotDerivedException extends IMPLogicsException {

    /**
     * Constructs an {@code PredicateIsNotDerivedException} with one argument indicating the base predicate.
     * <p>
     * The predicate is included in this exception's detail message. The exact presentation format of the detail message
     * is unspecified.
     *
     * @param predicate base predicate
     */
    public PredicateIsNotDerivedException(Predicate predicate) {
        super("Predicate " + predicate.getName() + " has no derivation rules");
    }
}
