package edu.upc.imp.logics.schema.exceptions;

public class PredicateNotInLevel extends RuntimeException {
    public PredicateNotInLevel(String name) {
        super("Predicate " + name + " is not contained in level");
    }
}
