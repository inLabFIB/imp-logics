package edu.upc.imp.logics.schema.exceptions;

public class PredicateNotExists extends RuntimeException {
    public PredicateNotExists(String predicateName) {
        super("Predicate " + predicateName + " does not exists");
    }
}
