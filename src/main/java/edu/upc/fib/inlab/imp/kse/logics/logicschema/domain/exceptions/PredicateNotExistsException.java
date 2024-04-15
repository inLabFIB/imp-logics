package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateNotExistsException extends IMPLogicsException {

    public PredicateNotExistsException(String predicateName) {
        super("Predicate " + predicateName + " does not exists");
    }
}
