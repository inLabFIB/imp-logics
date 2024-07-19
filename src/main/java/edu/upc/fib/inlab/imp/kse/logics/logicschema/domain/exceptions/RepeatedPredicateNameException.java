package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

/**
 * Thrown to indicate that a {@code Predicate} name is repeated inside a {@code LogicSchema}.
 */
public class RepeatedPredicateNameException extends IMPLogicsException {

    /**
     * Constructs an {@code RepeatedPredicateNameException} with one argument indicating the repeated {@code Predicate}
     * name.
     * <p>
     * The {@code Predicate} name is included in this exception's detail message. The exact presentation format of the
     * detail message is unspecified.
     *
     * @param name repeated {@code Predicate} name.
     */
    public RepeatedPredicateNameException(String name) {
        super("Repeated predicate " + name + " in the schema");
    }
}
