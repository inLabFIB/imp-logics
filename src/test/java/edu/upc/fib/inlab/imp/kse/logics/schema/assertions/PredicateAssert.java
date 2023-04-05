package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
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

    @SuppressWarnings("UnusedReturnValue")
    public PredicateAssert isLogicallyEquivalentTo(Predicate expectedPredicate) {
        return isLogicallyEquivalentTo(expectedPredicate, LogicSchemaAssert.DerivedLiteralStrategy.HOMOMORPHIC_RULES);
    }

    public PredicateAssert isLogicallyEquivalentTo(Predicate expectedPredicate, LogicSchemaAssert.DerivedLiteralStrategy derivedLiteralsStrategy) {
        Assertions.assertThat(actual.getName())
                .describedAs("Name of predicate")
                .isEqualTo(expectedPredicate.getName());
        Assertions.assertThat(actual.getArity())
                .describedAs("Arity of predicate " + actual.getName())
                .isEqualTo(expectedPredicate.getArity());
        Assertions.assertThat(actual.isBase())
                .describedAs("IsBase value of predicate " + actual.getName())
                .isEqualTo(expectedPredicate.isBase());
        assertThatAllActualDerivationRulesAreExpected(expectedPredicate.getDerivationRules(), derivedLiteralsStrategy.getAnalyzer());
        assertThatAllExpectedDerivationRulesAreContained(expectedPredicate.getDerivationRules(), derivedLiteralsStrategy.getAnalyzer());
        return this;
    }

    private void assertThatAllExpectedDerivationRulesAreContained(List<DerivationRule> expectedDerivationRules, LogicEquivalenceAnalyzer analyzer) {
        for (DerivationRule expectedDerivationRule : expectedDerivationRules) {
            boolean expectedFoundInActual = isDerivationRuleContained(expectedDerivationRule, actual.getDerivationRules(), analyzer);
            if (!expectedFoundInActual) {
                Assertions.fail("Derivation rule \"" + expectedDerivationRule + "\" expected, but missing in actual");
            }
        }
    }

    private void assertThatAllActualDerivationRulesAreExpected(List<DerivationRule> expectedDerivationRules, LogicEquivalenceAnalyzer analyzer) {
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
