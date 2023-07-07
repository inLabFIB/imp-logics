package edu.upc.fib.inlab.imp.kse.logics.schema.exceptions;

public class PredicateIsNotDerived extends RuntimeException {
    public PredicateIsNotDerived(String derivedPredicateName) {
        super("Predicate " + derivedPredicateName + " is not derived.");
    }
}
