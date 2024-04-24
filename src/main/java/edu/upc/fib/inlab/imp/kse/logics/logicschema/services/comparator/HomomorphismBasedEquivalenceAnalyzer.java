package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p> This class is responsible for detecting whether two list of literals (or derivation rules, or logic constraints)
 * are logically equivalent, or not. That is, whether they have the same evaluation for all possible databases. </p> *
 * <p> Since this problem is undecidable, this class relies on checking whether the two logic objects being compared
 * (e.g. A, and B) have a mutual homomorphism. That is, there is an homomorphism from A to B, and another from B to A.
 * When the list of literals is composed only of positive base ordinary literals, the test is sound and complete.
 * However, if some literal list contains negated literals, or built-in literals, such test is sound, but incomplete.
 * </p>
 *
 * <p> This class, by default, ignores the names of the derived predicate names. That is, two derived ordinary literals
 * are considered to be homomorphic if there is an homomorphism for their terms, and their derivation rules are
 * homomorphic, but they might have different predicate names. Such behavior can be overridden by injecting a different
 * HomomorphismFinder</p>
 *
 * <p> This class also ignores the schemas where the predicate belong to. That is, two base ordinary literals are
 * considered to be homomorphic if their predicate names coincide, and there is an homomorphism for their terms, but
 * both literals belongs to different schemas. In this manner, this analyser can be used to compare logic objects from
 * different schemas to check for instance, schema equivalence. </p>
 */
public class HomomorphismBasedEquivalenceAnalyzer implements LogicEquivalenceAnalyzer {

    private final HomomorphismFinder homomorphismFinder;

    public HomomorphismBasedEquivalenceAnalyzer() {
        this(new ExtendedHomomorphismFinder());
    }

    public HomomorphismBasedEquivalenceAnalyzer(HomomorphismFinder homomorphismFinder) {
        this.homomorphismFinder = homomorphismFinder;
    }

    /**
     * @param first  list of literals, not null
     * @param second list of literals, not null
     * @return whether both list of literals are equivalent
     */
    @Override
    public Optional<Boolean> areEquivalent(List<Literal> first, List<Literal> second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First list of literals cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second list of literals cannot be null");

        if (existHomomorphism(first, second) && existHomomorphism(second, first)) {
            return Optional.of(true);
        } else {
            return isHomomorphismCheckComplete(first, second) ? Optional.of(false) : UNKNOWN;
        }
    }

    private boolean existHomomorphism(List<Literal> literalList1, List<Literal> literalList2) {
        return homomorphismFinder.findHomomorphism(literalList1, literalList2).isPresent();
    }

    private boolean isHomomorphismCheckComplete(List<Literal> first, List<Literal> second) {
        return isConjunctiveQuery(first) && isConjunctiveQuery(second);
    }

    private static boolean isConjunctiveQuery(List<Literal> literals) {
        Query query = QueryFactory.createQuery(Collections.emptyList(), literals);
        return query.isConjunctiveQuery();
    }

    /**
     * @param first  logic constraint is not null
     * @param second logic constraint is not null
     * @return whether both logic constraints are equivalent
     */
    @Override
    public Optional<Boolean> areEquivalent(LogicConstraint first, LogicConstraint second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First logic constraint cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second logic constraint cannot be null");
        if (existHomomorphism(first, second) && existHomomorphism(second, first)) {
            return Optional.of(true);
        } else {
            return isHomomorphismCheckComplete(first.getBody(), second.getBody()) ? Optional.of(false) : UNKNOWN;
        }
    }

    private boolean existHomomorphism(LogicConstraint lc1, LogicConstraint lc2) {
        return homomorphismFinder.findHomomorphism(lc1, lc2).isPresent();
    }

    /**
     * @param first  derivation rule is not null
     * @param second derivation rule is not null
     * @return whether both derivation rules are equivalent
     */
    @Override
    public Optional<Boolean> areEquivalent(DerivationRule first, DerivationRule second) {
        if (Objects.isNull(first)) throw new IllegalArgumentException("First derivation rule cannot be null");
        if (Objects.isNull(second)) throw new IllegalArgumentException("Second derivation rule cannot be null");
        if (existHomomorphism(first, second) && existHomomorphism(second, first)) {
            return Optional.of(true);
        } else {
            return isHomomorphismCheckComplete(first.getBody(), second.getBody()) ? Optional.of(false) : UNKNOWN;
        }
    }

    private boolean existHomomorphism(DerivationRule dr1, DerivationRule dr2) {
        return homomorphismFinder.findHomomorphism(dr1, dr2).isPresent();
    }

}