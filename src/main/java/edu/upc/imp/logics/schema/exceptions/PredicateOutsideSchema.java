package edu.upc.imp.logics.schema.exceptions;

public class PredicateOutsideSchema extends RuntimeException {
    public PredicateOutsideSchema(String name) {
        super("Predicate " + name + " exists, but is not from this schema");
    }
}
