package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.OrdinaryLiteralSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class LiteralAssert extends AbstractAssert<LiteralAssert, Literal> {

    public LiteralAssert(Literal actual) {
        super(actual, LiteralAssert.class);
    }

    public static LiteralAssert assertThat(Literal actual) {
        return new LiteralAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public LiteralAssert correspondsSpec(LiteralSpec spec) {
        if (actual instanceof OrdinaryLiteral actualOrdinaryLiteral) {
            Assertions.assertThat(spec).isInstanceOf(OrdinaryLiteralSpec.class);
            OrdinaryLiteralAssert.assertThat(actualOrdinaryLiteral).correspondsSpec((OrdinaryLiteralSpec) spec);
        } else if (actual instanceof ComparisonBuiltInLiteral actualComparison) {
            Assertions.assertThat(spec).isInstanceOf(BuiltInLiteralSpec.class);
            ComparisonBuiltInLiteralAssert.assertThat(actualComparison).correspondsSpec((BuiltInLiteralSpec) spec);
        } else if (actual instanceof CustomBuiltInLiteral actualCustomBIL) {
            Assertions.assertThat(spec).isInstanceOf(BuiltInLiteralSpec.class);
            BuiltInLiteralAssert.assertThat(actualCustomBIL).correspondsSpec((BuiltInLiteralSpec) spec);
        } else if (actual instanceof BooleanBuiltInLiteral actualBoolBIL) {
            Assertions.assertThat(spec).isInstanceOf(BuiltInLiteralSpec.class);
            BuiltInLiteralAssert.assertThat(actualBoolBIL).correspondsSpec((BuiltInLiteralSpec) spec);
        } else {
            throw new RuntimeException("Unrecognized literal " + actual.getClass().getName());
        }
        return this;
    }

    public LiteralAssert isOrdinaryLiteral() {
        Assertions.assertThat(actual).isInstanceOf(OrdinaryLiteral.class);
        return this;
    }

    public LiteralAssert isComparisonBuiltInLiteral() {
        Assertions.assertThat(actual).isInstanceOf(ComparisonBuiltInLiteral.class);
        return this;
    }

    public LiteralAssert hasPredicate(String predicateName, int arity) {
        Assertions.assertThat(actual).isInstanceOf(OrdinaryLiteral.class);
        OrdinaryLiteralAssert.assertThat((OrdinaryLiteral) actual).hasPredicate(predicateName, arity);
        return this;
    }

    public LiteralAssert hasBuiltInComparisonOperation(String comparisonOperator) {
        Assertions.assertThat(actual).isInstanceOf(ComparisonBuiltInLiteral.class);
        ComparisonBuiltInLiteralAssert.assertThat((ComparisonBuiltInLiteral) actual).hasComparisonOperation(comparisonOperator);
        return this;
    }

    public LiteralAssert hasBuiltInCustomOperation(String comparisonOperator) {
        Assertions.assertThat(actual).isInstanceOf(CustomBuiltInLiteral.class);
        BuiltInLiteralAssert.assertThat((CustomBuiltInLiteral) actual).hasOperationName(comparisonOperator);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LiteralAssert hasVariable(int index, String variableName) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        Assertions.assertThat(actual.getTerms()).hasSizeGreaterThan(index);
        TermAssert.assertThat(actual.getTerms().get(index)).isVariable(variableName);
        return this;
    }

    public LiteralAssert hasConstant(int index, String constantName) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be greater than 0");
        }
        Assertions.assertThat(actual.getTerms()).hasSizeGreaterThan(index);
        TermAssert.assertThat(actual.getTerms().get(index)).isConstant(constantName);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LiteralAssert containsVariables(String... variableNames) {
        for (int i = 0; i < variableNames.length; i++) {
            String variableName = variableNames[i];
            hasVariable(i, variableName);
        }
        return this;
    }

    public LiteralAssert isPositive(boolean expected) {
        Assertions.assertThat(actual).isInstanceOf(OrdinaryLiteral.class);
        Assertions.assertThat(
                        ((OrdinaryLiteral) actual).isPositive())
                .isEqualTo(expected);
        return this;
    }
}
