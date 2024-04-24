package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.Set;

/**
 * Specification of a set of conjunctive queries.
 */
public record QuerySetSpec(Set<QuerySpec> querySpecSet) implements LogicElementSpec {

    public QuerySetSpec {
        if (querySpecSet == null) throw new IllegalArgumentException("Conjunctive query set cannot be null");
        if (querySpecSet.isEmpty()) throw new IllegalArgumentException("Conjunctive query set cannot be empty");
    }
}
