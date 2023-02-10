package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.exceptions.NegativeArity;

import java.util.List;

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

    /**
     * Checks whether the number of given elements matches this arity, and throws an Exception if
     * this is not the case.
     *
     * @param elements non-null list
     */
    public void checkMatches(List elements) {
        if(arity != elements.size()) throw new ArityMismatch(arity, elements.size());
    }
}
