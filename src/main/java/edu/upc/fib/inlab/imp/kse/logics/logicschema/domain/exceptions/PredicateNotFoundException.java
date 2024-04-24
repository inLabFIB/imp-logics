package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

/**
 * Thrown to indicate that a {@code Predicate} is not contained in a logic schema.
 */
public class PredicateNotFoundException extends IMPLogicsException {

    /**
     * Constructs an {@code PredicateNotFoundException} with one argument indicating the non-contained predicate name.
     * <p>
     * The predicate name is included in this exception's detail message. The exact presentation format of the detail
     * message is unspecified.
     *
     * @param predicateName the non-contained predicate name.
     */
    public PredicateNotFoundException(String predicateName) {
        super("Predicate " + predicateName + " does not exists");
    }
}
