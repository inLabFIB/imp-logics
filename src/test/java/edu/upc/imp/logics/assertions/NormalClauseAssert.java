package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.NormalClause;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public abstract class NormalClauseAssert<T extends NormalClause> extends AbstractAssert<NormalClauseAssert<T>, T> {
    public NormalClauseAssert(T actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public NormalClauseAssert<T> hasBodySize(int size) {
        Assertions.assertThat(actual.getBody()).hasSize(size);
        return this;
    }

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
        Assertions.assertThat(actual.getBody()).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isOrdinaryLiteral()
                        .hasPredicate(predicateName, variableNames.length)
                        .containsVariables(variableNames)
        );
        return this;
    }
}
