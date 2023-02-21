package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.specification.OrdinaryLiteralSpec;
import edu.upc.imp.logics.specification.TermSpec;
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
        Assertions.assertThat(actual.getAtom().getPredicate().getArity().getNumber()).isEqualTo(spec.getTermSpecList().size());
        Assertions.assertThat(actual.isPositive()).isEqualTo(spec.isPositive());
        for (int i = 0; i < actual.getAtom().getPredicate().getArity().getNumber(); ++i) {
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
}
