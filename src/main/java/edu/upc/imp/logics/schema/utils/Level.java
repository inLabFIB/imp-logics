package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Predicate;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Set of predicates of a level in some hierarchichal database
 */
public class Level {
    private final Set<Predicate> predicates;
    /*
     * Invariants:
     * - predicate set cannot be null
     * - predicate set cannot contain nulls
     * - predicate set cannot be empty
     * - predicate must be unmodifiable
     */

    public Level(Set<Predicate> predicates) {
        if (Objects.isNull(predicates)) throw new IllegalArgumentException("Predicates cannot be null");
        if (predicates.isEmpty()) throw new IllegalArgumentException("Predicates cannot be empty");
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
