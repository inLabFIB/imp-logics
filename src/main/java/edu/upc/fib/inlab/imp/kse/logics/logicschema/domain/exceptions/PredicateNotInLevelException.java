package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

/**
 * Thrown to indicate that a {@code Predicate} is not found in a {@code LevelHierarchy}.
 */
public class PredicateNotInLevelException extends IMPLogicsException {

    /**
     * Constructs an {@code PredicateNotInLevelException} with one argument indicating the predicate not found in any
     * level.
     * <p>
     * The predicate is included in this exception's detail message. The exact presentation format of the detail message
     * is unspecified.
     *
     * @param predicate predicate not found in any level.
     */
    public PredicateNotInLevelException(Predicate predicate) {
        super("Predicate " + predicate + " is not contained in any level.");
    }
}
