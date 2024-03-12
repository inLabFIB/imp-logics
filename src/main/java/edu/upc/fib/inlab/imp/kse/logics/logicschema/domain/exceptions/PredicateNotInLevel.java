package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateNotInLevel extends RuntimeException {
    public PredicateNotInLevel(String name) {
        super("Predicate " + name + " is not contained in level");
    }
}
