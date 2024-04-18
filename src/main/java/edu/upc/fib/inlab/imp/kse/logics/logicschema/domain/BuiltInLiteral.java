package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

/**
 * Implementation of a logic built-in literal. That is, a literal whose interpretation is fixed, rather than checked
 * over a database.
 * <p>
 * E.g. "{@code 3 < 4}"
 */
public abstract class BuiltInLiteral extends Literal {

    /**
     * Returns the built-in literal operation name.
     *
     * @return  the operation name.
     */
    public abstract String getOperationName();
}
