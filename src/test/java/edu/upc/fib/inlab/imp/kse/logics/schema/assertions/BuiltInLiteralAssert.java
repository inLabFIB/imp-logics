package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralParser;
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

    @SuppressWarnings("UnusedReturnValue")
    public BuiltInLiteralAssert correspondsSpec(BuiltInLiteralSpec spec) {
        Assertions.assertThat(actual.getOperationName()).isEqualTo(spec.getOperator());
        for (int i = 0; i < actual.getTerms().size(); ++i) {
            Term actualTerm = actual.getTerms().get(i);
            TermSpec termSpec = spec.getTermSpecList().get(i);
            TermAssert.assertThat(actualTerm).correspondsSpec(termSpec);
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public BuiltInLiteralAssert correspondsTo(String expectedLiteralString) {
        Literal expectedLiteral = LiteralParser.parseLiteral(expectedLiteralString);
        LiteralAssert.assertThat(expectedLiteral).isBuiltInLiteral();
        BuiltInLiteral expectedBuiltInLiteral = (BuiltInLiteral) expectedLiteral;

        if (expectedBuiltInLiteral instanceof ComparisonBuiltInLiteral) {
            Assertions.assertThat(actual)
                    .asInstanceOf(LogicInstanceOfAssertFactories.COMPARISON_BUILT_IN_LITERAL)
                    .correspondsTo(expectedLiteralString);
        } else if (expectedBuiltInLiteral instanceof BooleanBuiltInLiteral expectedBBIL) {
            BuiltInLiteralAssert.assertThat(actual).isBooleanBuiltInLiteral();
            //TODO: move following lines to BBILAssert when created
            BooleanBuiltInLiteral actualBBIL = (BooleanBuiltInLiteral) actual;
            Assertions.assertThat(actualBBIL.isTrue()).isEqualTo(expectedBBIL.isTrue());
        } else if (expectedBuiltInLiteral instanceof CustomBuiltInLiteral expectedCustomBIL) {
            BuiltInLiteralAssert.assertThat(actual)
                    .isCustomBuiltInLiteral()
                    .isEqualTo(expectedCustomBIL); //TODO: think about this when used!
        } else throw new RuntimeException("Unknown BuiltInLiteral type");

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public BuiltInLiteralAssert isBooleanBuiltInLiteral() {
        Assertions.assertThat(actual).isInstanceOf(BooleanBuiltInLiteral.class);
        return this;
    }

    public BuiltInLiteralAssert isCustomBuiltInLiteral() {
        Assertions.assertThat(actual).isInstanceOf(CustomBuiltInLiteral.class);
        return this;
    }

    public BuiltInLiteralAssert hasOperationName(String operationName) {
        Assertions.assertThat(actual.getOperationName()).isEqualTo(operationName);
        return this;
    }

    @SuppressWarnings("unused")
    public ComparisonBuiltInLiteralAssert asComparisonBuiltInLiteral() {
        objects.assertIsInstanceOf(info, actual, ComparisonBuiltInLiteral.class);
        return new ComparisonBuiltInLiteralAssert((ComparisonBuiltInLiteral) actual).as(info.description());
    }
    //TODO: move to LiteralAssert or make BuiltInLiteralAssert an extension of LiteralAssert

    @SuppressWarnings("UnusedReturnValue")
    public BuiltInLiteralAssert hasNoTerms() {
        Assertions.assertThat(actual.getTerms()).isEmpty();
        return this;
    }
    //TODO: move to LiteralAssert or make BuiltInLiteralAssert an extension of LiteralAssert

    public BuiltInLiteralAssert hasVariable(int i, String varName) {
        TermAssert.assertThat(actual.getTerms().get(i))
                .isVariable(varName);
        return this;
    }
}
