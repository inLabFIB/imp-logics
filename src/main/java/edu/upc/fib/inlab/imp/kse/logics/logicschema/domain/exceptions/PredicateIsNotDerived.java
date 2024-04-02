package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateIsNotDerived extends RuntimeException {
    public PredicateIsNotDerived(String derivedPredicateName) {
        super("Predicate " + derivedPredicateName + " is not derived.");
    }
}
