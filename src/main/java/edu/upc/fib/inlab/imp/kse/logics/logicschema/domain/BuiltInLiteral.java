package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

/**
 * Implementation of a logic built-in literal.
 * That is, a literal whose interpretation is fixed, rather than checked over a database.
 * E.g. "3 < 4" is a built-in literal since it can be checked without accessing a database
 */
public abstract class BuiltInLiteral extends Literal {
    public abstract String getOperationName();
}
