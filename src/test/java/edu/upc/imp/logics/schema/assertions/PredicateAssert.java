package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.Predicate;
import edu.upc.imp.logics.services.comparator.LogicEquivalenceAnalyzer;
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

    public PredicateAssert hasArity(int arity) {
        Assertions.assertThat(actual.getArity()).isEqualTo(arity);
        return this;
    }

    public PredicateAssert isDerived(boolean expected) {
        Assertions.assertThat(actual.isDerived()).isEqualTo(expected);
        return this;
    }

    public PredicateAssert isLogicallyEquivalentTo(Predicate expectedPredicate) {
        Assertions.assertThat(actual.getName()).isEqualTo(expectedPredicate.getName());
        Assertions.assertThat(actual.getArity()).isEqualTo(expectedPredicate.getArity());
        Assertions.assertThat(actual.isBase()).isEqualTo(expectedPredicate.isBase());
        assertThatAllActualDerivationRulesAreExpected(expectedPredicate.getDerivationRules());
        assertThatAllExpectedDerivationRulesAreContained(expectedPredicate.getDerivationRules());
        return this;
    }

    private void assertThatAllExpectedDerivationRulesAreContained(List<DerivationRule> expectedDerivationRules) {
        for (DerivationRule expectedDerivationRule : expectedDerivationRules) {
            boolean expectedFoundInActual = isDerivationRuleContained(expectedDerivationRule, actual.getDerivationRules());
            if (!expectedFoundInActual) {
                Assertions.fail("Derivation rule \"" + expectedDerivationRule + "\" expected, but missing in actual");
            }
        }
    }

    private void assertThatAllActualDerivationRulesAreExpected(List<DerivationRule> expectedDerivationRules) {
        for (DerivationRule actualDerivationRule : actual.getDerivationRules()) {
            boolean isExpected = isDerivationRuleContained(actualDerivationRule, expectedDerivationRules);
            if (!isExpected) {
                Assertions.fail("Derivation rule \"" + actualDerivationRule + "\" is not expected");
            }
        }
    }

    private boolean isDerivationRuleContained(DerivationRule derivationRule, List<DerivationRule> listOfRules) {
        for (DerivationRule aRuleOfTheList : listOfRules) {
            LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new LogicEquivalenceAnalyzer();
            if (logicEquivalenceAnalyzer.areEquivalent(derivationRule, aRuleOfTheList)) {
                return true;
            }
        }
        return false;
    }
}
