package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.imp.logics.services.creation.spec.BuiltInLiteralSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ComparisonBuiltInLiteralAssert extends AbstractAssert<ComparisonBuiltInLiteralAssert, ComparisonBuiltInLiteral> {
    public ComparisonBuiltInLiteralAssert(ComparisonBuiltInLiteral comparisonBuiltInLiteral) {
        super(comparisonBuiltInLiteral, ComparisonBuiltInLiteralAssert.class);
    }

    public static ComparisonBuiltInLiteralAssert assertThat(ComparisonBuiltInLiteral actual) {
        return new ComparisonBuiltInLiteralAssert(actual);
    }

    public ComparisonBuiltInLiteralAssert correspondsSpec(BuiltInLiteralSpec spec) {
        if (spec.getTermSpecs().size() != 2) {
            throw new IllegalArgumentException("Built-in literal spec must have 2 terms to be compared with ComparisonBuiltInLiteral");
        }
        Assertions.assertThat(actual.getOperator().getSymbol()).isEqualTo(spec.getOperator());
        TermAssert.assertThat(actual.getLeftTerm()).correspondsSpec(spec.getTermSpecs().get(0));
        TermAssert.assertThat(actual.getRightTerm()).correspondsSpec(spec.getTermSpecs().get(1));
        return this;
    }

    public ComparisonBuiltInLiteralAssert hasComparisonOperation(String comparisonOperator) {
        Assertions.assertThat(actual.getOperator().getSymbol()).isEqualTo(comparisonOperator);
        return this;
    }
}
