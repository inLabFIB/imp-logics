package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class RepeatedPredicateNameException extends IMPLogicsException {

    public RepeatedPredicateNameException(String name) {
        super("Repeated predicate " + name + " in the schema");
    }
}
