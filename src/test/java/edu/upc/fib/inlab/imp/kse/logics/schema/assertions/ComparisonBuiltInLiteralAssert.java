package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.EqualityComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralParser;
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

    @SuppressWarnings("UnusedReturnValue")
    public ComparisonBuiltInLiteralAssert correspondsTo(String expectedLiteralString) {
        Literal expectedLiteral = LiteralParser.parseLiteral(expectedLiteralString);
        LiteralAssert.assertThat(expectedLiteral).isComparisonBuiltInLiteral();
        ComparisonBuiltInLiteral expectedComparisonBuiltInLiteral = (ComparisonBuiltInLiteral) expectedLiteral;

        assertThat(actual)
                .hasComparisonOperation(expectedComparisonBuiltInLiteral.getOperationName())
                .hasLeftTerm(expectedComparisonBuiltInLiteral.getTerms().get(0))
                .hasRightTerm(expectedComparisonBuiltInLiteral.getTerms().get(1));
        return this;
    }

    public ComparisonBuiltInLiteralAssert isEquality() {
        objects.assertIsInstanceOf(info, actual, EqualityComparisonBuiltInLiteral.class);
        return this;
    }

    public ComparisonBuiltInLiteralAssert hasComparisonOperation(String comparisonOperator) {
        Assertions.assertThat(actual.getOperator().getSymbol()).isEqualTo(comparisonOperator);
        return this;
    }

    private ComparisonBuiltInLiteralAssert hasLeftTerm(Term expected) {
        Assertions.assertThat(actual.getLeftTerm()).isEqualTo(expected);
        return this;
    }

    @SuppressWarnings("unused")
    public TermAssert leftTerm() {
        return TermAssert.assertThat(actual.getLeftTerm());
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

    @SuppressWarnings("UnusedReturnValue")
    private ComparisonBuiltInLiteralAssert hasRightTerm(Term expected) {
        Assertions.assertThat(actual.getRightTerm()).isEqualTo(expected);
        return this;
    }

    @SuppressWarnings("unused")
    public TermAssert rightTerm() {
        return TermAssert.assertThat(actual.getRightTerm());
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
