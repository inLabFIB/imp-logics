package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;

public class NoNegatableLiteralException extends IMPLogicsException {

    public NoNegatableLiteralException(Literal literal) {
        super("Currently, we do not know how to negate the literal " + literal);
    }
}
