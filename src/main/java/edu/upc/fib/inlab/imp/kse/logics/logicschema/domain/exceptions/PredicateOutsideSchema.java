package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateOutsideSchema extends RuntimeException {
    public PredicateOutsideSchema(String name) {
        super("Predicate " + name + " exists, but is not from this schema");
    }
}
