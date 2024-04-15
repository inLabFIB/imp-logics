package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateNotInLevelException extends IMPLogicsException {

    public PredicateNotInLevelException(String name) {
        super("Predicate " + name + " is not contained in level");
    }
}
