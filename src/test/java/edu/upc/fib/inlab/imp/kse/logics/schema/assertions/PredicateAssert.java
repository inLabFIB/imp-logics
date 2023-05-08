package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicStructureComparator;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class PredicateAssert extends AbstractAssert<PredicateAssert, Predicate> {

    public PredicateAssert(Predicate predicate) {
        super(predicate, PredicateAssert.class);
    }

    public static PredicateAssert assertThat(Predicate actual) {
        return new PredicateAssert(actual);
    }

    public PredicateAssert hasName(String name) {
        Assertions.assertThat(actual.getName()).isEqualTo(name);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public PredicateAssert hasArity(int arity) {
        Assertions.assertThat(actual.getArity()).isEqualTo(arity);
        return this;
    }

    @SuppressWarnings("unused")
    public PredicateAssert isDerived(boolean expected) {
        Assertions.assertThat(actual.isDerived()).isEqualTo(expected);
        return this;
    }

    /**
     * Two predicates P and Q are said to be equivalent iff any two atoms P(x) and Q(x), being x any possible
     * list of constants, P(x) and Q(x) evaluates the same on any possible database.
     * <p>
     * To be equivalent, it is necessary that P and Q have the same name, arity, and both must be base (or both
     * must be derived).
     * <p>
     * When both are derived, it is undecidable to know whether they are equivalent, or not. Hence, this assert
     * applies a sound (but incomplete) strategy based on checking whether their derivation rules are homomorphic,
     * where on its turn, two derivated literals are considered to be homomorphic iff their terms are homomorphic and
     * their derivation rules are homomorphic too.
     */
    @SuppressWarnings("UnusedReturnValue")
    public PredicateAssert isLogicallyEquivalentTo(Predicate expectedPredicate) {
        checkHasSameName(expectedPredicate);
        checkHasSameArity(expectedPredicate);
        checkIsBaseIffExpectedIsBase(expectedPredicate);
        checkDerivationRulesEquivalenceWithStrategy(expectedPredicate, LogicSchemaAssert.DerivedLiteralStrategy.HOMOMORPHIC_RULES);
        return this;
    }

    /**
     * Two predicates are said to have the same structure if:
     * - they have the same name,
     * - they have the same arity,
     * - they are both base, or they are both derived
     * - their derivation rules have the very same structure, recursively, according to the LogicStructureComparator
     *
     * @param expectedPredicate not null
     * @return whether the actual predicate has the same structure as the expectedPredicate
     */
    public PredicateAssert hasSameStructureAs(Predicate expectedPredicate) {
        checkHasSameName(expectedPredicate);
        checkHasSameArity(expectedPredicate);
        checkIsBaseIffExpectedIsBase(expectedPredicate);
        checkDerivationRulesHaveSameStructure(expectedPredicate);
        return this;
    }

    private void checkDerivationRulesHaveSameStructure(Predicate expectedPredicate) {
        assertThatAllActualDerivationRulesAreExpectedModusStructure(expectedPredicate.getDerivationRules());
        assertThatAllExpectedDerivationRulesAreContainedModusStructure(expectedPredicate.getDerivationRules());
    }

    private void assertThatAllExpectedDerivationRulesAreContainedModusStructure(List<DerivationRule> expectedDerivationRules) {
        for (DerivationRule expectedDerivationRule : expectedDerivationRules) {
            boolean expectedFoundInActual = isDerivationRuleContainedModusStructure(expectedDerivationRule, actual.getDerivationRules());
            if (!expectedFoundInActual) {
                Assertions.fail("Derivation rule \"" + expectedDerivationRule + "\" expected, but missing in actual");
            }
        }
    }

    private void assertThatAllActualDerivationRulesAreExpectedModusStructure(List<DerivationRule> expectedDerivationRules) {
        for (DerivationRule actualDerivationRule : actual.getDerivationRules()) {
            boolean isExpected = isDerivationRuleContainedModusStructure(actualDerivationRule, expectedDerivationRules);
            if (!isExpected) {
                Assertions.fail("Derivation rule \"" + actualDerivationRule + "\" is not expected");
            }
        }
    }

    private boolean isDerivationRuleContainedModusStructure(DerivationRule derivationRule, List<DerivationRule> listOfRules) {
        for (DerivationRule aRuleOfTheList : listOfRules) {
            if (new LogicStructureComparator(true, true).haveSameStructure(derivationRule, aRuleOfTheList)) {
                return true;
            }
        }
        return false;
    }

    protected void checkDerivationRulesEquivalenceWithStrategy(Predicate expectedPredicate, LogicSchemaAssert.DerivedLiteralStrategy derivedLiteralsStrategy) {
        assertThatAllActualDerivationRulesAreEquivalentToExpected(expectedPredicate.getDerivationRules(), derivedLiteralsStrategy.getAnalyzer());
        assertThatAllEquivalentExpectedDerivationRulesAreContained(expectedPredicate.getDerivationRules(), derivedLiteralsStrategy.getAnalyzer());
    }

    private void checkIsBaseIffExpectedIsBase(Predicate expectedPredicate) {
        Assertions.assertThat(actual.isBase())
                .describedAs("IsBase value of predicate " + actual.getName())
                .isEqualTo(expectedPredicate.isBase());
    }

    private void checkHasSameArity(Predicate expectedPredicate) {
        Assertions.assertThat(actual.getArity())
                .describedAs("Arity of predicate " + actual.getName())
                .isEqualTo(expectedPredicate.getArity());
    }

    private void checkHasSameName(Predicate expectedPredicate) {
        Assertions.assertThat(actual.getName())
                .describedAs("Name of predicate")
                .isEqualTo(expectedPredicate.getName());
    }

    private void assertThatAllEquivalentExpectedDerivationRulesAreContained(List<DerivationRule> expectedDerivationRules, LogicEquivalenceAnalyzer analyzer) {
        for (DerivationRule expectedDerivationRule : expectedDerivationRules) {
            boolean expectedFoundInActual = isDerivationRuleContained(expectedDerivationRule, actual.getDerivationRules(), analyzer);
            if (!expectedFoundInActual) {
                Assertions.fail("Derivation rule \"" + expectedDerivationRule + "\" expected, but missing in actual");
            }
        }
    }

    private void assertThatAllActualDerivationRulesAreEquivalentToExpected(List<DerivationRule> expectedDerivationRules, LogicEquivalenceAnalyzer analyzer) {
        for (DerivationRule actualDerivationRule : actual.getDerivationRules()) {
            boolean isExpected = isDerivationRuleContained(actualDerivationRule, expectedDerivationRules, analyzer);
            if (!isExpected) {
                Assertions.fail("Derivation rule \"" + actualDerivationRule + "\" is not expected");
            }
        }
    }


    private boolean isDerivationRuleContained(DerivationRule derivationRule, List<DerivationRule> listOfRules, LogicEquivalenceAnalyzer analyzer) {
        for (DerivationRule aRuleOfTheList : listOfRules) {
            if (analyzer.areEquivalent(derivationRule, aRuleOfTheList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the actual predicate contains a derivation rule equivalent to expectedRule considering
     * that two derived ordinary literals are equivalent according to the given strategy.
     *
     * @param expectedRule not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public PredicateAssert containsEquivalentDerivationRule(DerivationRule expectedRule, LogicSchemaAssert.DerivedLiteralStrategy derivedLiteralsStrategy) {
        if (!isDerivationRuleContained(expectedRule, actual.getDerivationRules(), derivedLiteralsStrategy.getAnalyzer())) {
            Assertions.fail("Missing derivation rule " + expectedRule);
        }
        return this;
    }

    /**
     * Checks whether the actual predicate contains a derivation rule equivalent to expectedRule considering
     * that two derived ordinary literals are equivalent iff their definition rules are equivalent
     *
     * @param expectedRule not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public PredicateAssert containsEquivalentDerivationRule(DerivationRule expectedRule) {
        if (!isDerivationRuleContained(expectedRule, actual.getDerivationRules(), new LogicEquivalenceAnalyzer())) {
            Assertions.fail("Missing derivation rule " + expectedRule);
        }
        return this;
    }


}
