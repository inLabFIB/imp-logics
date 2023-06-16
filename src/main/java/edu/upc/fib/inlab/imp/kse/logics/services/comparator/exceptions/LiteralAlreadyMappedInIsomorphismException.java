package edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;

public class LiteralAlreadyMappedInIsomorphismException extends RuntimeException {
    public LiteralAlreadyMappedInIsomorphismException(Literal firstLiteral, Literal secondLiteral) {
        super("Isomorphism already contains literal " + firstLiteral + " or " + secondLiteral + " (or both)");
    }
}
