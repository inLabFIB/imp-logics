package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.*;
import edu.upc.fib.inlab.imp.kse.logics.services.printer.LogicSchemaPrinter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Optional;
import java.util.Set;

public class LogicSchemaAssert extends AbstractAssert<LogicSchemaAssert, LogicSchema> {

    private HomomorphismBasedEquivalenceAnalyzer analyzer = new HomomorphismBasedEquivalenceAnalyzer();

    public LogicSchemaAssert(LogicSchema logicSchema) {
        super(logicSchema, LogicSchemaAssert.class);
    }

    public static LogicSchemaAssert assertThat(LogicSchema actual) {
        return new LogicSchemaAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsPredicate(String predicateName, int arity) {
        Assertions.assertThat(actual.getAllPredicates())
                .anySatisfy(predicate -> PredicateAssert.assertThat(predicate)
                        .hasName(predicateName)
                        .hasArity(arity));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsConstraintID(String constraintID) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .anySatisfy(constraint -> LogicConstraintAssert.assertThat(constraint)
                        .hasID(constraintID));
        return this;
    }

    public LogicSchemaAssert containsExactlyThesePredicateNames(String... predicateNames) {
        Assertions.assertThat(actual.getAllPredicates())
                .map(Predicate::getName)
                .containsExactlyInAnyOrder(predicateNames);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsExactlyTheseConstraintIDs(String... constraintIDs) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .map(LogicConstraint::getID)
                .map(ConstraintID::id)
                .containsExactlyInAnyOrder(constraintIDs);
        return this;
    }

    public void hasConstraintsSize(int size) {
        Assertions.assertThat(actual.getAllLogicConstraints()).hasSize(size);
    }


    public LogicSchemaAssert isEmpty() {
        Assertions.assertThat(actual.isEmpty())
                .describedAs("Actual logic schema is not empty")
                .isTrue();
        return this;
    }

    /**
     * Asserts whether the actual logicSchema is equivalent to the expectedSchema.
     * That is, whether they have equivalent predicates (base, and derived), and equivalent constraints.
     * Do note that this comparison is NOT agnostic with the name of the derived predicates.
     * <p>
     * Since this check is not decidable, this method applies a sound (but not complete) strategy.
     * In particular, for each normal clause of one schema, it tries to find an homomorphic normal clause of the other
     * schema, and viceversa.
     *
     * @param expectedSchema not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert isLogicallyEquivalentTo(LogicSchema expectedSchema) {
        assertAllPredicatesAreEquivalent(expectedSchema);
        assertAllLogicConstraintsAreEquivalent(expectedSchema);
        return this;
    }


    public LogicSchemaAssert hasSameStructureAs(LogicSchema expectedSchema) {
        assertAllPredicatesHaveSameStructure(expectedSchema);
        assertAllLogicConstraintsHaveSameStructure(expectedSchema);
        return this;
    }

    private void assertAllLogicConstraintsHaveSameStructure(LogicSchema expectedSchema) {
        for (LogicConstraint actualConstraint : actual.getAllLogicConstraints()) {
            boolean actualIsExpected = logicConstraintIsContainedInListModusStructure(actualConstraint, expectedSchema.getAllLogicConstraints());
            if (!actualIsExpected) {
                Assertions.fail("Actual constraint \"" + actualConstraint + "\" is not expected");
            }
        }

        for (LogicConstraint expectedConstraint : expectedSchema.getAllLogicConstraints()) {
            boolean expectedIsFound = logicConstraintIsContainedInListModusStructure(expectedConstraint, actual.getAllLogicConstraints());
            if (!expectedIsFound) {
                Assertions.fail("Expected constraint \"" + expectedConstraint + "\" is missing");
            }
        }
    }

    private boolean logicConstraintIsContainedInListModusStructure(LogicConstraint constraint, Set<LogicConstraint> constraintSet) {
        for (LogicConstraint aConstraintFromSet : constraintSet) {
            if (new LogicStructureComparator(true, true).haveSameStructure(constraint, aConstraintFromSet)) {
                return true;
            }
        }
        return false;
    }

    private void assertAllPredicatesHaveSameStructure(LogicSchema expectedSchema) {
        for (Predicate actualPredicate : actual.getAllPredicates()) {
            try {
                Predicate expectedPredicate = expectedSchema.getPredicateByName(actualPredicate.getName());
                PredicateAssert.assertThat(actualPredicate).hasSameStructureAs(expectedPredicate);
            } catch (PredicateNotExists e) {
                Assertions.fail("Actual predicate " + actualPredicate.getName() + " was not expected");
            }
        }
        for (Predicate expectedPredicate : expectedSchema.getAllPredicates()) {
            try {
                actual.getPredicateByName(expectedPredicate.getName());
            } catch (PredicateNotExists e) {
                Assertions.fail("Missing expected predicate " + expectedPredicate.getName());
            }
        }
    }


    /**
     * Asserts whether the actual logicSchema constraints are equivalent to the expectedSchema constraints.
     * Do note that this comparison IS agnostic with the name of the derived predicates.
     * <p>
     * Since this check is not decidable, this method applies a sound (but not complete) strategy.
     * In particular, for each logicConstraint of one schema, it tries to find an homomorphic logicConstraint of the other
     * schema, and viceversa. In such case, two derived ordinary literals are considered to be homomorphic if they have
     * homomorphic definition rules. In practice, this means that two logic constraints are equivalent if they are
     * the same up-to renaming of variables and derived predicate names.
     *
     * @param expectedSchema not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert assertAllLogicConstraintsAreEquivalent(LogicSchema expectedSchema) {
        assertAllLogicConstraintsAreEquivalentAccordingToAnalyzer(expectedSchema, DerivedLiteralStrategy.HOMOMORPHIC_RULES.getAnalyzer());
        return this;
    }

    protected LogicSchemaAssert assertAllLogicConstraintsAreEquivalentAccordingToAnalyzer(LogicSchema expectedSchema, LogicEquivalenceAnalyzer analyzer) {
        for (LogicConstraint actualConstraint : actual.getAllLogicConstraints()) {
            Optional<Boolean> actualIsExpected = logicConstraintIsContainedInList(actualConstraint, expectedSchema.getAllLogicConstraints(), analyzer);
            if (actualIsExpected.isPresent() && !actualIsExpected.get()) {
                Assertions.fail("Actual constraint \"" + actualConstraint + "\" is not expected");
            } else if (actualIsExpected.isEmpty()) {
                Assertions.fail("Current logicEquivalenceAnalyzer: " + analyzer.getClass().getName() + "\n" +
                        " could not determine if actual constraint: " + actualConstraint + "\n" +
                        "   is present in schema\n" +
                        "Expected schema: " + new LogicSchemaPrinter().print(expectedSchema) + "\n");
            }
        }

        for (LogicConstraint expectedConstraint : expectedSchema.getAllLogicConstraints()) {
            Optional<Boolean> expectedIsFound = logicConstraintIsContainedInList(expectedConstraint, actual.getAllLogicConstraints(), analyzer);
            if (expectedIsFound.isPresent() && !expectedIsFound.get()) {
                Assertions.fail("Expected constraint \"" + expectedConstraint + "\" is missing");
            } else if (expectedIsFound.isEmpty()) {
                Assertions.fail("Current logicEquivalenceAnalyzer: " + analyzer.getClass().getName() + "\n" +
                        " could not determine if expected constraint: " + expectedConstraint + "\n" +
                        "   is present in schema\n" +
                        "Actual schema: " + new LogicSchemaPrinter().print(actual) + "\n");
            }
        }

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
     * In particular, for each derivation rule of one schema, it tries to find an homomorphic derivation rule of the other
     * schema, and viceversa.
     *
     * @param expectedSchema not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert assertAllPredicatesAreEquivalent(LogicSchema expectedSchema) {
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


    public enum DerivedLiteralStrategy {
        HOMOMORPHIC_RULES(new HomomorphicRulesHomomorphismCriteria()),
        SAME_NAME(new SamePredicateNameCriteria());

        private final DerivedOrdinaryLiteralHomomorphismCriteria criteria;

        DerivedLiteralStrategy(DerivedOrdinaryLiteralHomomorphismCriteria criteria) {
            this.criteria = criteria;
        }

        public LogicEquivalenceAnalyzer getAnalyzer() {
            return new HomomorphismBasedEquivalenceAnalyzer(new HomomorphismFinder(this.criteria));
        }

    }

    /**
     * Checks whether the actual schema contains a constraint equivalent to expectedConstraint considering
     * that two derived ordinary literals are equivalent if their derivation rules are equivalent
     *
     * @param expectedConstraint not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public LogicSchemaAssert containsEquivalentConstraint(LogicConstraint expectedConstraint) {
        Optional<Boolean> containmentResult = this.logicConstraintIsContainedInList(expectedConstraint, actual.getAllLogicConstraints());
        if (containmentResult.isPresent() && !containmentResult.get()) {
            Assertions.fail("Missing expected constraint " + expectedConstraint);
        } else if (containmentResult.isEmpty()) {
            Assertions.fail("Current logicEquivalenceAnalyzer: " + analyzer.getClass().getName() + "\n" +
                    " could not determine if expected constraint: " + expectedConstraint + "\n" +
                    "   is present in schema\n" +
                    "Actual schema: " + new LogicSchemaPrinter().print(actual) + "\n");
        }
        return this;
    }

    /**
     * Checks whether the actual schema contains a constraint equivalent to expectedConstraint considering
     * that two derived ordinary literals are equivalent according to the given strategy.
     *
     * @param expectedConstraint      not null
     * @param derivedLiteralsStrategy not null
     * @return this assert
     */
    @SuppressWarnings("unused")
    public LogicSchemaAssert containsEquivalentConstraint(LogicConstraint expectedConstraint, DerivedLiteralStrategy derivedLiteralsStrategy) {
        Optional<Boolean> containmentResult = this.logicConstraintIsContainedInList(expectedConstraint, actual.getAllLogicConstraints(), derivedLiteralsStrategy.getAnalyzer());
        if (containmentResult.isPresent() && !containmentResult.get()) {
            Assertions.fail("Missing expected constraint " + expectedConstraint);
        } else if (containmentResult.isEmpty()) {
            Assertions.fail("Current logicEquivalenceAnalyzer: " + analyzer.getClass().getName() + "\n" +
                    " could not determine if expected constraint: " + expectedConstraint + "\n" +
                    "   is present in schema\n" +
                    "Actual schema: " + new LogicSchemaPrinter().print(actual) + "\n");
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
    public LogicSchemaAssert containsEquivalentDerivationRule(DerivationRule expectedRule, DerivedLiteralStrategy derivedLiteralsStrategy) {
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
    public LogicSchemaAssert containsEquivalentDerivationRule(DerivationRule expectedRule) {
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
    public LogicSchemaAssert containsEquivalentPredicate(Predicate expectedPredicate, DerivedLiteralStrategy derivedLiteralsStrategy) {
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
    public LogicSchemaAssert containsEquivalentPredicate(Predicate expectedPredicate) {
        Predicate actualPredicate = actual.getPredicateByName(expectedPredicate.getName());
        PredicateAssert.assertThat(actualPredicate).isLogicallyEquivalentTo(expectedPredicate);
        return this;
    }

}
