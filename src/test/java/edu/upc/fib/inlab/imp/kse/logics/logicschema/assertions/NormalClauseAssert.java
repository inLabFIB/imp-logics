package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BooleanBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.CustomBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.NormalClause;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

public abstract class NormalClauseAssert<T extends NormalClause> extends AbstractAssert<NormalClauseAssert<T>, T> {

    public NormalClauseAssert(T actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public ImmutableLiteralsListAssert body() {
        return ImmutableLiteralsListAssert.assertThat(actual.getBody());
    }

    public NormalClauseAssert<T> hasBodySize(int size) {
        Assertions.assertThat(actual.getBody()).hasSize(size);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NormalClauseAssert<T> containsOrdinaryLiteral(String predicateName, int arity) {
        return containsOrdinaryLiteral(predicateName, arity, true);
    }

    public NormalClauseAssert<T> containsOrdinaryLiteral(String predicateName, int arity, boolean positive) {
        Assertions.assertThat(actual.getBody()).anySatisfy(lit -> {
            Assertions.assertThat(lit).isInstanceOf(OrdinaryLiteral.class);
            OrdinaryLiteral ol = (OrdinaryLiteral) lit;
            OrdinaryLiteralAssert.assertThat(ol).isPositive(positive);
            OrdinaryLiteralAssert.assertThat(ol).hasPredicate(predicateName, arity);
        });
        return this;
    }

    public NormalClauseAssert<T> containsOrdinaryLiteral(String predicateName, String... variableNames) {
        ImmutableLiteralsListAssert.assertThat(actual.getBody()).containsOrdinaryLiteral(predicateName, variableNames);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NormalClauseAssert<T> containsComparisonBuiltInLiteral(String comparisonOperator, String leftVariable, String rightVariable) {
        Assertions.assertThat(actual.getBody()).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isComparisonBuiltInLiteral()
                        .containsVariables(leftVariable, rightVariable)
                        .asComparisonBuiltInLiteral()
                        .hasComparisonOperation(comparisonOperator)
        );
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NormalClauseAssert<T> containsBooleanBuiltInLiteral(boolean booleanValue) {
        Assertions.assertThat(actual.getBody()).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .asInstanceOf(InstanceOfAssertFactories.type(BooleanBuiltInLiteral.class))
                        .satisfies(l ->
                                BuiltInLiteralAssert.assertThat(l)
                                        .hasOperationName(BooleanBuiltInLiteral.fromValue(booleanValue))
                        )
        );
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NormalClauseAssert<T> containsCustomBuiltInLiteral(String operationName) {
        Assertions.assertThat(actual.getBody()).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .asInstanceOf(InstanceOfAssertFactories.type(CustomBuiltInLiteral.class))
                        .satisfies(l ->
                                BuiltInLiteralAssert.assertThat(l)
                                        .hasOperationName(operationName)
                        )
        );
        return this;
    }
}
