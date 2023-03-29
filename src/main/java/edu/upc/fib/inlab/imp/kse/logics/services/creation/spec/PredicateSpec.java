package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import java.util.Objects;

/**
 * Specification of a predicate. Do note that a predicate specification is just a name, and one arity.
 * I.e., a predicate specification has no derivation rules associated.
 *
 * @param name
 * @param arity
 */
public record PredicateSpec(String name, int arity) {

    public PredicateSpec {
        if (Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        if (arity < 0) throw new IllegalArgumentException("Arity cannot be negative");
    }
}
