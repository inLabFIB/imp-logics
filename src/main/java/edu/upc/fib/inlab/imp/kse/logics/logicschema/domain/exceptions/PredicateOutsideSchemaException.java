package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class PredicateOutsideSchemaException extends IMPLogicsException {

    public PredicateOutsideSchemaException(String name) {
        super("Predicate " + name + " exists, but is not from this schema");
    }
}
