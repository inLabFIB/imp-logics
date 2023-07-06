package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.HomomorphismBasedEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.TermSpec;
import org.assertj.core.api.Assertions;


public class DerivationRuleAssert extends NormalClauseAssert<DerivationRule> {

    public DerivationRuleAssert(DerivationRule actual) {
        super(actual, DerivationRuleAssert.class);
    }

    public static DerivationRuleAssert assertThat(DerivationRule actual) {
        return new DerivationRuleAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public DerivationRuleAssert correspondsSpec(DerivationRuleSpec spec) {
        Assertions.assertThat(actual.getHead().getPredicate().getName()).isEqualTo(spec.getPredicateName());
        Assertions.assertThat(actual.getHead().getPredicate().getArity()).isEqualTo(spec.getTermSpecList().size());
        for (int i = 0; i < actual.getHead().getPredicate().getArity(); ++i) {
            Term actualTerm = actual.getHeadTerms().get(i);
            TermSpec termSpec = spec.getTermSpecList().get(i);
            TermAssert.assertThat(actualTerm).correspondsSpec(termSpec);
        }
        Assertions.assertThat(actual.getBody()).hasSameSizeAs(spec.getBody());
        for (int i = 0; i < actual.getBody().size(); ++i) {
            Literal actualLit = actual.getBody().get(i);
            LiteralSpec litSpec = spec.getBody().get(i);
            LiteralAssert.assertThat(actualLit).correspondsSpec(litSpec);
        }

        return this;
    }

    /**
     * Checks whether the actual rule is the same as the expected logic rule up-to renaming
     * variables, and derived predicate names.
     *
     * @param expected not-null
     * @return this assertion
     * @see LogicEquivalenceAnalyzer
     */
    @SuppressWarnings("unused")
    public DerivationRuleAssert isLogicallyEquivalent(DerivationRule expected) {
        Assertions.assertThat(new HomomorphismBasedEquivalenceAnalyzer().areEquivalent(actual, expected).orElse(false))
                .overridingErrorMessage("Actual rule: " + actual.toString() + "\n" +
                        "   is not equivalent to \n" +
                        "Expected rule: " + expected.toString()
                )
                .isTrue();
        return this;
    }


}