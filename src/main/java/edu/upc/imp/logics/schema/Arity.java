package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.exceptions.NegativeArity;

/**
 * Value object that represents an arity, for instance, of a Predicate.
 * It is a non-negative integer.
 */
public class Arity {
    /**
     * Invariants:
     * - arity >= 0
     */
    private final int arity;

    public Arity(int arity) {
        if(arity < 0) throw new NegativeArity(arity);
        this.arity = arity;

    }

    public int getNumber() {
        return arity;
    }

    public void checkMatches(int size) {
        if(arity != size) throw new ArityMismatch(arity, size);
    }
}
