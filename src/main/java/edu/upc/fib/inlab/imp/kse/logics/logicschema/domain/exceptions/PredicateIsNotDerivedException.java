package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateIsNotDerivedException extends RuntimeException {

    public PredicateIsNotDerivedException(String derivedPredicateName) {
        super("Predicate " + derivedPredicateName + " is not derived.");
    }
}
