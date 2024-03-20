package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.Set;

/**
 * Specification of a set of conjunctive queries.
 */
public record ConjunctiveQuerySetSpec(Set<ConjunctiveQuerySpec> conjunctiveQuerySpecSet) implements LogicElementSpec {

        public ConjunctiveQuerySetSpec {
            if (conjunctiveQuerySpecSet == null) throw new IllegalArgumentException("Conjunctive query set cannot be null");
            if (conjunctiveQuerySpecSet.isEmpty()) throw new IllegalArgumentException("Conjunctive query set cannot be empty");
        }
}
