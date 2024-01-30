package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ComparisonBuiltInLiteralAssert extends AbstractAssert<ComparisonBuiltInLiteralAssert, ComparisonBuiltInLiteral> {
    public ComparisonBuiltInLiteralAssert(ComparisonBuiltInLiteral comparisonBuiltInLiteral) {
        super(comparisonBuiltInLiteral, ComparisonBuiltInLiteralAssert.class);
    }

    public static ComparisonBuiltInLiteralAssert assertThat(ComparisonBuiltInLiteral actual) {
        return new ComparisonBuiltInLiteralAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ComparisonBuiltInLiteralAssert correspondsSpec(BuiltInLiteralSpec spec) {
        if (spec.getTermSpecList().size() != 2) {
            throw new IllegalArgumentException("Built-in literal spec must have 2 terms to be compared with ComparisonBuiltInLiteral");
        }
        Assertions.assertThat(actual.getOperator().getSymbol()).isEqualTo(spec.getOperator());
        TermAssert.assertThat(actual.getLeftTerm()).correspondsSpec(spec.getTermSpecList().get(0));
        TermAssert.assertThat(actual.getRightTerm()).correspondsSpec(spec.getTermSpecList().get(1));
        return this;
    }

    public ComparisonBuiltInLiteralAssert hasComparisonOperation(String comparisonOperator) {
        Assertions.assertThat(actual.getOperator().getSymbol()).isEqualTo(comparisonOperator);
        return this;
    }

    public ComparisonBuiltInLiteralAssert hasLeftVariable(String variableName) {
        TermAssert.assertThat(actual.getLeftTerm()).isVariable(variableName);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ComparisonBuiltInLiteralAssert hasRightVariable(String variableName) {
        TermAssert.assertThat(actual.getRightTerm()).isVariable(variableName);
        return this;
    }

    @SuppressWarnings("unused")
    public ComparisonBuiltInLiteralAssert hasLeftConstant(String constantName) {
        TermAssert.assertThat(actual.getLeftTerm()).isConstant(constantName);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue, unused")
    public ComparisonBuiltInLiteralAssert hasRightConstant(String constantName) {
        TermAssert.assertThat(actual.getRightTerm()).isConstant(constantName);
        return this;
    }



}
