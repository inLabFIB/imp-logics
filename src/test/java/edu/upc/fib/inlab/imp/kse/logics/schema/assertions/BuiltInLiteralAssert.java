package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.BuiltInLiteral;
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

    public BuiltInLiteralAssert hasNoTerms() {
        Assertions.assertThat(actual.getTerms()).isEmpty();
        return this;
    }
}
