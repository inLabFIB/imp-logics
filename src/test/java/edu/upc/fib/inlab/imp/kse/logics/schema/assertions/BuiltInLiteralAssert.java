package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.BuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.TermSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class BuiltInLiteralAssert extends AbstractAssert<BuiltInLiteralAssert, BuiltInLiteral> {
    public BuiltInLiteralAssert(BuiltInLiteral comparisonBuiltInLiteral) {
        super(comparisonBuiltInLiteral, BuiltInLiteralAssert.class);
    }

    public static BuiltInLiteralAssert assertThat(BuiltInLiteral actual) {
        return new BuiltInLiteralAssert(actual);
    }

    public BuiltInLiteralAssert hasOperationName(String operationName) {
        Assertions.assertThat(actual.getOperationName()).isEqualTo(operationName);
        return this;
    }

    public BuiltInLiteralAssert correspondsSpec(BuiltInLiteralSpec spec) {
        Assertions.assertThat(actual.getOperationName()).isEqualTo(spec.getOperator());
        for (int i = 0; i < actual.getTerms().size(); ++i) {
            Term actualTerm = actual.getTerms().get(i);
            TermSpec termSpec = spec.getTermSpecList().get(i);
            TermAssert.assertThat(actualTerm).correspondsSpec(termSpec);
        }
        return this;
    }

    public BuiltInLiteralAssert hasNoTerms() {
        Assertions.assertThat(actual.getTerms()).isEmpty();
        return this;
    }

    public BuiltInLiteralAssert hasVariable(int i, String varName) {
        TermAssert.assertThat(actual.getTerms().get(i))
                .isVariable(varName);
        return this;
    }
}
