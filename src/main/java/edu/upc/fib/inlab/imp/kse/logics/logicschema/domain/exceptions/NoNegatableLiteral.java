package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;

public class NoNegatableLiteral extends RuntimeException {
    public NoNegatableLiteral(Literal literal) {
        super("Currently, we do not know how to negate the literal " + literal);
    }
}
