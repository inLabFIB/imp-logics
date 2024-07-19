package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils.LiteralParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OrdinaryLiteralAssert extends AbstractAssert<OrdinaryLiteralAssert, OrdinaryLiteral> {

    public OrdinaryLiteralAssert(OrdinaryLiteral actual) {
        super(actual, OrdinaryLiteralAssert.class);
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert correspondsSpec(OrdinaryLiteralSpec spec) {
        Assertions.assertThat(actual.getAtom().getPredicate().getName()).isEqualTo(spec.getPredicateName());
        Assertions.assertThat(actual.getAtom().getPredicate().getArity()).isEqualTo(spec.getTermSpecList().size());
        Assertions.assertThat(actual.isPositive()).isEqualTo(spec.isPositive());
        for (int i = 0; i < actual.getAtom().getPredicate().getArity(); ++i) {
            Term actualTerm = actual.getAtom().getTerms().get(i);
            TermSpec termSpec = spec.getTermSpecList().get(i);
            TermAssert.assertThat(actualTerm).correspondsSpec(termSpec);
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert correspondsTo(String expectedLiteralString) {
        Literal expectedLiteral = LiteralParser.parseLiteral(expectedLiteralString);
        LiteralAssert.assertThat(expectedLiteral).isOrdinaryLiteral();
        OrdinaryLiteral expectedOrdinaryLiteral = (OrdinaryLiteral) expectedLiteral;

        assertThat(actual)
                .hasPredicateName(expectedOrdinaryLiteral.getAtom().getPredicateName())
                .hasTerms(expectedOrdinaryLiteral.getAtom().getTerms());

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert hasTerms(ImmutableTermList terms) {
        ImmutableTermListAssert.assertThat(actual.getTerms())
                .containsExactlyElementsOf(terms);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert hasPredicateName(String predicateName) {
        AtomAssert.assertThat(actual.getAtom()).hasPredicateName(predicateName);
        return this;
    }

    public static OrdinaryLiteralAssert assertThat(OrdinaryLiteral actual) {
        return new OrdinaryLiteralAssert(actual);
    }

    public OrdinaryLiteralAssert isPositive() {
        return this.isPositive(true);
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert isPositive(boolean positive) {
        Assertions.assertThat(actual.isPositive()).isEqualTo(positive);
        return this;
    }

    public OrdinaryLiteralAssert isNegated() {
        return this.isPositive(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert hasPredicate(String predicateName, int arity) {
        AtomAssert.assertThat(actual.getAtom()).hasPredicate(predicateName, arity);
        return this;
    }

    /**
     * Asserts that the actual atom should have the very same predicate (i.e., same object reference) as the one given
     * by parameter
     *
     * @param predicate not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public OrdinaryLiteralAssert hasPredicate(Predicate predicate) {
        AtomAssert.assertThat(actual.getAtom()).hasPredicate(predicate);
        return this;
    }
}
