package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

public class RepeatedPredicateName extends RuntimeException {
    public RepeatedPredicateName(String name) {
        super("Repeated predicate " + name + " in the schema");
    }
}
