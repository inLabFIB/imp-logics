package edu.upc.fib.inlab.imp.kse.logics.schema.utils;

import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Set of predicates of a level in some hierarchical database
 */
public class Level {
    private final Set<Predicate> predicates;
    /*
     * Invariants:
     * - predicate set cannot be null
     * - predicate set cannot contain nulls
     * - predicate must be unmodifiable
     *
     * A level can be empty. This is necessary for schemas which do not contain base predicates but contains
     * derived predicates (e.g. "P(x) :- TRUE()")
     */

    public Level(Set<Predicate> predicates) {
        if (Objects.isNull(predicates)) throw new IllegalArgumentException("Predicates cannot be null");
        if (predicates.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Predicates cannot contain null");

        this.predicates = Collections.unmodifiableSet(predicates);
    }

    /**
     * @return an unmodifiable set of predicates of this level
     */
    public Set<Predicate> getAllPredicates() {
        return predicates;
    }
}
