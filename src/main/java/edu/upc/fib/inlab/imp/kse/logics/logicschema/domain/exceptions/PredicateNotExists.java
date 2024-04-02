package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateNotExists extends RuntimeException {
    public PredicateNotExists(String predicateName) {
        super("Predicate " + predicateName + " does not exists");
    }
}
