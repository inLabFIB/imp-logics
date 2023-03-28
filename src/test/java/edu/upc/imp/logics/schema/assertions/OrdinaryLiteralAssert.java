package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.ImmutableTermList;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.TermSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OrdinaryLiteralAssert extends AbstractAssert<OrdinaryLiteralAssert, OrdinaryLiteral> {

    public OrdinaryLiteralAssert(OrdinaryLiteral actual) {
        super(actual, OrdinaryLiteralAssert.class);
    }

    public static OrdinaryLiteralAssert assertThat(OrdinaryLiteral actual) {
        return new OrdinaryLiteralAssert(actual);
    }

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

    public OrdinaryLiteralAssert isPositive(boolean positive) {
        Assertions.assertThat(actual.isPositive()).isEqualTo(positive);
        return this;
    }

    public OrdinaryLiteralAssert hasPredicate(String predicateName, int arity) {
        AtomAssert.assertThat(actual.getAtom()).hasPredicate(predicateName, arity);
        return this;
    }

    public OrdinaryLiteralAssert hasPredicateName(String predicateName) {
        AtomAssert.assertThat(actual.getAtom()).hasPredicateName(predicateName);
        return this;
    }

    public OrdinaryLiteralAssert hasTerms(ImmutableTermList terms) {
        ImmutableTermListAssert.assertThat(actual.getTerms())
                .containsExactlyElementsOf(terms);
        return this;
    }
}
