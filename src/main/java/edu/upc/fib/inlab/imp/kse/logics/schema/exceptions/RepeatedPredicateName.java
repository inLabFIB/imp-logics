package edu.upc.fib.inlab.imp.kse.logics.schema.exceptions;

public class RepeatedPredicateName extends RuntimeException {
    public RepeatedPredicateName(String name) {
        super("Repeated predicate " + name + " in the schema");
    }
}
