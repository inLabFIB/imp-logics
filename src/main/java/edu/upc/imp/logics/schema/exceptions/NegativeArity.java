package edu.upc.imp.logics.schema.exceptions;

public class NegativeArity extends RuntimeException {
    public NegativeArity(int arity) {
        super("Arity cannot be negative, but you provided " + arity);
    }
}
