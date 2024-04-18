package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;

/**
 * Thrown to indicate that a {@code Literal} can not be negated.
 */
public class NoNegatableLiteralException extends IMPLogicsException {

    /**
     * Constructs an {@code NoNegatableLiteralException} with one argument indicating the non negatable literal.
     * <p>
     * The literal is included in this exception's detail message. The exact presentation format of the detail message
     * is unspecified.
     *
     * @param literal   non negatable literal.
     */
    public NoNegatableLiteralException(Literal literal) {
        super("Currently, we do not know how to negate the literal " + literal);
    }
}
