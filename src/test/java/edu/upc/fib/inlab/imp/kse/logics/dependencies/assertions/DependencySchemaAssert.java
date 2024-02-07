package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.PredicateAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.HomomorphismBasedEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Optional;
import java.util.Set;

public class DependencySchemaAssert extends AbstractAssert<DependencySchemaAssert, DependencySchema> {

    private final HomomorphismBasedEquivalenceAnalyzer analyzer = new HomomorphismBasedEquivalenceAnalyzer();

    public DependencySchemaAssert(DependencySchema dependencySchema) {
        super(dependencySchema, DependencySchemaAssert.class);
    }

    public static DependencySchemaAssert assertThat(DependencySchema actual) {
        return new DependencySchemaAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public DependencySchemaAssert containsPredicate(String predicateName, int arity) {
        Assertions.assertThat(actual.getAllPredicates())
                .anySatisfy(predicate -> PredicateAssert.assertThat(predicate)
                        .hasName(predicateName)
                        .hasArity(arity));
        return this;
    }

    public DependencySchemaAssert containsExactlyThesePredicateNames(String... predicateNames) {
        Assertions.assertThat(actual.getAllPredicates())
                .map(Predicate::getName)
                .containsExactlyInAnyOrder(predicateNames);
        return this;
    }

    public DependencySchemaAssert hasDependencySize(int size) {
        Assertions.assertThat(actual.getDependencies()).hasSize(size);
        return this;
    }

    public DependencySchemaAssert isEmpty() {
        Assertions.assertThat(actual.isEmpty())
                .describedAs("Actual logic schema is not empty")
                .isTrue();
        return this;
    }

    private Optional<Boolean> logicConstraintIsContainedInList(LogicConstraint constraint, Set<LogicConstraint> constraintSet) {
        return logicConstraintIsContainedInList(constraint, constraintSet, analyzer);
    }

    private Optional<Boolean> logicConstraintIsContainedInList(LogicConstraint constraint, Set<LogicConstraint> constraintSet, LogicEquivalenceAnalyzer analyzer) {
        boolean unknownFound = false;
        for (LogicConstraint aConstraintFromSet : constraintSet) {
            Optional<Boolean> equivalenceResult = analyzer.areEquivalent(constraint, aConstraintFromSet);
            if (equivalenceResult.isPresent() && equivalenceResult.get()) {
                return Optional.of(true);
            } else if (equivalenceResult.isEmpty()) {
                unknownFound = true;
            }
        }
        if (unknownFound) return Optional.empty();
        return Optional.of(false);
    }

    /**
     * Asserts whether the actual logicSchema predicates (base or derived) are equivalent to the expectedSchema.
     * Do note that this comparison is NOT agnostic with the name of the derived predicates.
     * <p>
     * Since this check is not decidable, this method applies a sound (but not complete) strategy.
     * In particular, for each derivation rule of one schema, it tries to find a homomorphic derivation rule of the other
     * schema, and vice versa.
     *
     * @param expectedSchema not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public DependencySchemaAssert assertAllPredicatesAreEquivalent(LogicSchema expectedSchema) {
        for (Predicate actualPredicate : actual.getAllPredicates()) {
            try {
                Predicate expectedPredicate = expectedSchema.getPredicateByName(actualPredicate.getName());
                PredicateAssert.assertThat(actualPredicate).isLogicallyEquivalentTo(expectedPredicate);
            } catch (PredicateNotExists predicateNotExists) {
                Assertions.fail("Actual predicate " + actualPredicate.getName() + " is not expected");
            }
        }

        for (Predicate expectedPredicate : expectedSchema.getAllPredicates()) {
            try {
                actual.getPredicateByName(expectedPredicate.getName());
            } catch (PredicateNotExists predicateNotExists) {
                Assertions.fail("Missing expected predicate " + expectedPredicate.getName());
            }
        }

        return this;
    }

    /**
     * Checks whether the actual schema contains a derivation rule equivalent to expectedRule considering
     * that two derived ordinary literals are equivalent according to the given strategy.
     *
     * @param expectedRule            not null
     * @param derivedLiteralsStrategy not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public DependencySchemaAssert containsEquivalentDerivationRule(DerivationRule expectedRule, LogicSchemaAssert.DerivedLiteralStrategy derivedLiteralsStrategy) {
        Predicate actualPredicate = actual.getPredicateByName(expectedRule.getHead().getPredicateName());
        PredicateAssert.assertThat(actualPredicate).containsEquivalentDerivationRule(expectedRule, derivedLiteralsStrategy);
        return this;
    }

    /**
     * Checks whether the actual schema contains a derivation rule equivalent to expectedRule considering
     * that two derived ordinary literals are equivalent iff their definition rules are equivalent
     *
     * @param expectedRule not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public DependencySchemaAssert containsEquivalentDerivationRule(DerivationRule expectedRule) {
        Predicate actualPredicate = actual.getPredicateByName(expectedRule.getHead().getPredicateName());
        PredicateAssert.assertThat(actualPredicate).containsEquivalentDerivationRule(expectedRule);
        return this;
    }

    /**
     * Checks whether the actual schema contains a predicate equivalent to expectedPredicate considering
     * that two derived ordinary literals (appearing in the definition rules of the given predicate) are equivalent
     * according to the derivedLiteralStrategy given
     *
     * @param expectedPredicate       not null
     * @param derivedLiteralsStrategy not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public DependencySchemaAssert containsEquivalentPredicate(Predicate expectedPredicate, LogicSchemaAssert.DerivedLiteralStrategy derivedLiteralsStrategy) {
        Predicate actualPredicate = actual.getPredicateByName(expectedPredicate.getName());
        PredicateAssert.assertThat(actualPredicate).checkDerivationRulesEquivalenceWithStrategy(expectedPredicate, derivedLiteralsStrategy);
        return this;
    }

    /**
     * Checks whether the actual schema contains a predicate equivalent to expectedPredicate considering
     * that two derived ordinary literals (appearing in the definition rules of the given predicate) are equivalent
     * iff their definition rules are equivalent
     *
     * @param expectedPredicate not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public DependencySchemaAssert containsEquivalentPredicate(Predicate expectedPredicate) {
        Predicate actualPredicate = actual.getPredicateByName(expectedPredicate.getName());
        PredicateAssert.assertThat(actualPredicate).isLogicallyEquivalentTo(expectedPredicate);
        return this;
    }

}
