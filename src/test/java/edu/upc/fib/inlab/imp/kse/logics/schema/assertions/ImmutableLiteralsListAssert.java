package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralParser;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

public class ImmutableLiteralsListAssert extends AbstractListAssert<ImmutableLiteralsListAssert, ImmutableLiteralsList, Literal, LiteralAssert> {
    private final LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new LogicEquivalenceAnalyzer();

    protected ImmutableLiteralsListAssert(ImmutableLiteralsList actual) {
        super(actual, ImmutableLiteralsListAssert.class);
    }

    public static ImmutableLiteralsListAssert assertThat(ImmutableLiteralsList actual) {
        return new ImmutableLiteralsListAssert(actual);
    }

    /**
     * Checks whether the actual literals are the same as the expected literals up-to renaming
     * variables, and derived predicate names.
     *
     * @param expected not null
     * @return this assert
     */
    public ImmutableLiteralsListAssert isLogicallyEquivalentTo(ImmutableLiteralsList expected) {
        boolean equivalence = logicEquivalenceAnalyzer.areEquivalent(actual, expected);
        Assertions.assertThat(equivalence)
                .describedAs("Actual literals list: " + actual.toString() + "\n" +
                        "   is not equivalent to\n" +
                        "Expected literals list: " + expected.toString() + "\n")
                .isTrue();
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

    @Override
    protected LiteralAssert toAssert(Literal value, String description) {
        return LiteralAssert.assertThat(value).as(description);
    }

    @Override
    protected ImmutableLiteralsListAssert newAbstractIterableAssert(Iterable<? extends Literal> iterable) {
        return assertThat(new ImmutableLiteralsList(newArrayList(iterable)));
    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert containsComparisonBuiltInLiteral(String leftVariableName, String comparisonOperator, String rightVariableName) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .isComparisonBuiltInLiteral()
                        .hasBuiltInComparisonOperation(comparisonOperator)
                        .containsVariables(leftVariableName, rightVariableName)
        );
        return this;

    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert hasLiteral(int index, String expectedLiteralString) {
        Assertions.assertThat(actual.size())
                .withFailMessage("Expecting to have some element at index %d", index)
                .isGreaterThan(index);

        Literal expectedLiteral = LiteralParser.parseLiteral(expectedLiteralString);
        Literal actualLiteral = actual.get(index);

        if (expectedLiteral instanceof OrdinaryLiteral expectedOL) {
            Assertions.assertThat(actualLiteral)
                    .asInstanceOf(InstanceOfAssertFactories.type(OrdinaryLiteral.class))
                    .satisfies(
                            ol -> OrdinaryLiteralAssert.assertThat(ol)
                                    .hasPredicateName(expectedOL.getAtom().getPredicateName())
                                    .hasTerms(expectedOL.getAtom().getTerms())
                    );
        }
        return this;
    }

    public void containsExactlyLiteralsOf(List<String> sortedLiterals) {
        for (int i = 0; i < sortedLiterals.size(); i++) {
            hasLiteral(i, sortedLiterals.get(i));
        }
    }

}
