package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.ImmutableLiteralsList;
import edu.upc.imp.logics.services.comparator.LogicEquivalenceAnalyzer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ImmutableLiteralsListAssert extends AbstractAssert<ImmutableLiteralsListAssert, ImmutableLiteralsList> {
    private final LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new LogicEquivalenceAnalyzer();

    protected ImmutableLiteralsListAssert(ImmutableLiteralsList actual) {
        super(actual, ImmutableLiteralsListAssert.class);
    }

    public static ImmutableLiteralsListAssert assertThat(ImmutableLiteralsList actual) {
        return new ImmutableLiteralsListAssert(actual);
    }

    public ImmutableLiteralsListAssert isLogicallyEquivalentTo(ImmutableLiteralsList expected) {
        boolean equivalence = logicEquivalenceAnalyzer.areEquivalent(actual, expected);
        Assertions.assertThat(equivalence).isTrue();
        return this;
    }

    public ImmutableLiteralsListAssert hasSameSizeAs(ImmutableLiteralsList expectedUnfolding) {
        Assertions.assertThat(actual).hasSameSizeAs(expectedUnfolding);
        return this;
    }

    public ImmutableLiteralsListAssert containsOrdinaryLiteral(String predicateName, String... variableNames) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isOrdinaryLiteral()
                        .hasPredicate(predicateName, variableNames.length)
                        .containsVariables(variableNames)
        );
        return this;
    }

    public ImmutableLiteralsListAssert containsOrdinaryLiteral(boolean isPositive, String predicateName, String... variableNames) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isOrdinaryLiteral()
                        .isPositive(isPositive)
                        .hasPredicate(predicateName, variableNames.length)
                        .containsVariables(variableNames)
        );
        return this;
    }

    public ImmutableLiteralsListAssert isEmpty() {
        Assertions.assertThat(actual).isEmpty();
        return this;
    }
}
