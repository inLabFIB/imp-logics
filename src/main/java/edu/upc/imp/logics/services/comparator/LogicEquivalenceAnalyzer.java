package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.LogicConstraint;

import java.util.List;
import java.util.Objects;

/**
 * <p> This class is responsible for detecting whether two list of literals (or derivation rules, or logic constraints) are
 * logically equivalent, or not. That is, whether they have the same evaluation for all possible databases. </p>
 *
 * <p> Since this problem is undecidible, this class relies on checking whether the two logic objects being compared (e.g. A, and B)
 * have a mutual homomorphism. That is, there is an homomorphism from A to B, and another from B to A. Such test
 * is sound, but incomplete. </p>
 *
 * <p> This class ignores the names of the derived predicate names. That is, two derived ordinary literals are considered
 * to be homomorphic if there is an homomorphism for their terms, and their derivation rules are homomorphic, but they
 * might have different predicate names.</p>
 *
 * <p> This class also ignores the schemas where the predicate belong to. That is, two base ordinary literals are considered
 * to be homomorphic if their predicate names coincide, and there is an homomorphism for their terms, but both literals belongs
 * to different schemas. In this manner, this analyser can be used to compare logic objects from different schemas to check
 * for instance, schema equivalence. </p>
 */
public class LogicEquivalenceAnalyzer {

    private final ExtendedHomomorphismFinder extendedHomomorphismFinder;

    public LogicEquivalenceAnalyzer() {
        extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
    }

    public boolean areEquivalent(List<Literal> first, List<Literal> second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First list of literals cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second list of literals cannot be null");
        return existHomomorphism(first, second) && existHomomorphism(second, first);
    }

    private boolean existHomomorphism(List<Literal> first, List<Literal> second) {
        return extendedHomomorphismFinder.findHomomorphism(first, second).isPresent();
    }

    public boolean areEquivalent(LogicConstraint first, LogicConstraint second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First logic constraint cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second logic constraint cannot be null");
        return existHomomorphism(first, second) && existHomomorphism(second, first);

    }

    private boolean existHomomorphism(LogicConstraint first, LogicConstraint second) {
        return extendedHomomorphismFinder.findHomomorphism(first, second).isPresent();
    }

    public boolean areEquivalent(DerivationRule first, DerivationRule second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First derivation rule cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second derivation rule cannot be null");
        return existHomomorphism(first, second) && existHomomorphism(second, first);
    }

    private boolean existHomomorphism(DerivationRule first, DerivationRule second) {
        return extendedHomomorphismFinder.findHomomorphism(first, second).isPresent();
    }

}