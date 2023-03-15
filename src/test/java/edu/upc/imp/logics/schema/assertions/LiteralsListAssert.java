package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.services.comparator.LogicEquivalenceAnalyzer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

public class LiteralsListAssert extends AbstractAssert<LiteralsListAssert, List<Literal>> {
    private final LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new LogicEquivalenceAnalyzer();

    protected LiteralsListAssert(List<Literal> actual) {
        super(actual, LiteralsListAssert.class);
    }

    public static LiteralsListAssert assertThat(List<Literal> actual) {
        return new LiteralsListAssert(actual);
    }

    public LiteralsListAssert isLogicallyEquivalentTo(List<Literal> expected) {
        boolean equivalence = logicEquivalenceAnalyzer.areEquivalent(actual, expected);
        Assertions.assertThat(equivalence).isTrue();
        return this;
    }

    public LiteralsListAssert hasSameSizeAs(List<Literal> expectedUnfolding) {
        Assertions.assertThat(actual).hasSameSizeAs(expectedUnfolding);
        return this;
    }

    public LiteralsListAssert containsOrdinaryLiteral(String predicateName, String... variableNames) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isOrdinaryLiteral()
                        .hasPredicate(predicateName, variableNames.length)
                        .containsVariables(variableNames)
        );
        return this;
    }

    public LiteralsListAssert containsOrdinaryLiteral(boolean isPositive, String predicateName, String... variableNames) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isOrdinaryLiteral()
                        .isPositive(isPositive)
                        .hasPredicate(predicateName, variableNames.length)
                        .containsVariables(variableNames)
        );
        return this;
    }

    public LiteralsListAssert isEmpty() {
        Assertions.assertThat(actual).isEmpty();
        return this;
    }

    public LiteralsListAssert hasSize(int expectedSize) {
        Assertions.assertThat(actual).hasSize(expectedSize);
        return this;
    }

    public LiteralsListAssert containsComparisonBuiltInLiteral(String leftVariableName, String comparisonOperator, String rightVariableName) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isComparisonBuiltInLiteral()
                        .hasBuiltInComparisonOperation(comparisonOperator)
                        .containsVariables(leftVariableName, rightVariableName)
        );
        return this;

    }
}
