package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.LiteralPosition;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralParser;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.HomomorphismBasedEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismComparator;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismOptions;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.util.Lists.newArrayList;

public class ImmutableLiteralsListAssert extends AbstractListAssert<ImmutableLiteralsListAssert, ImmutableLiteralsList, Literal, LiteralAssert> {
    private final LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
    private IsomorphismOptions isomorphismOptions = new IsomorphismOptions();


    protected ImmutableLiteralsListAssert(ImmutableLiteralsList actual) {
        super(actual, ImmutableLiteralsListAssert.class);
    }

    public static ImmutableLiteralsListAssert assertThat(ImmutableLiteralsList actual) {
        return new ImmutableLiteralsListAssert(actual);
    }


    /**
     * Checks whether the actual literals are the same as the expected literals up to renaming variable names
     * and derived predicate names.
     * <br>
     * This assert considers two base predicates to be equal iff they have the very same predicate name and arity
     * That is, two predicates of different logic schemas can be considered equal
     *
     * @param expected not null
     * @return this assert
     * @see LogicEquivalenceAnalyzer
     */
    public ImmutableLiteralsListAssert isLogicallyEquivalentTo(ImmutableLiteralsList expected) {
        Optional<Boolean> equivalenceResult = logicEquivalenceAnalyzer.areEquivalent(actual, expected);
        if (equivalenceResult.isPresent()) {
            Assertions.assertThat(equivalenceResult)
                    .describedAs("Actual literals list: " + actual.toString() + "\n" +
                            "   is not equivalent to\n" +
                            "Expected literals list: " + expected.toString() + "\n")
                    .contains(true);
        } else {
            Assertions.fail("Current logicEquivalenceAnalyzer: " + logicEquivalenceAnalyzer.getClass().getName() + "\n" +
                    " could not determine if actual literals list: " + actual.toString() + "\n" +
                    "   is equivalent to\n" +
                    "Expected literals list: " + expected.toString() + "\n");
        }
        return this;
    }

    public ImmutableLiteralsListAssert hasSameSizeAs(ImmutableLiteralsList expectedUnfolding) {
        Assertions.assertThat(actual).hasSameSizeAs(expectedUnfolding);
        return this;
    }

    /* LITERAL COMES FROM ASSERTS */
    public ImmutableLiteralsListAssert literalHasNoOriginal(int i) {
        assertThat(actual).hasSizeGreaterThan(i);

        Literal actualLit = actual.get(i);
        Assertions.assertThat(actual.getOriginalLiteral(actualLit))
                .as("Literal " + actualLit + " has an original literal")
                .isNotPresent();

        return this;
    }

    /**
     * Checks that the currentLiteral has an original literal which is exactly the same (i.e., same object reference)
     * as the expectedOriginalLiteral
     *
     * @param currentLiteral          not null
     * @param expectedOriginalLiteral not null
     * @return this assert
     */
    public ImmutableLiteralsListAssert literalComesFrom(Literal currentLiteral, Literal expectedOriginalLiteral) {
        Optional<Literal> actualOriginalLiteral = actual.getOriginalLiteral(currentLiteral);
        assertThat(actual).contains(currentLiteral);
        Assertions.assertThat(actualOriginalLiteral)
                .as("No original literal for " + currentLiteral)
                .isPresent();
        Assertions.assertThat(actualOriginalLiteral)
                .as("Original literal of " + currentLiteral + " is " + actualOriginalLiteral.get() + ", but has not the same reference as " + expectedOriginalLiteral)
                .containsSame(expectedOriginalLiteral);
        return this;
    }

    /**
     * Checks that the i-th literal has an original literal which is exactly the same (i.e., same object reference)
     * as the expectedOriginalLiteral
     *
     * @param i                       >= 0
     * @param expectedOriginalLiteral not null
     * @return this assert
     */
    public ImmutableLiteralsListAssert literalComesFrom(int i, Literal expectedOriginalLiteral) {
        Literal currentLiteral = this.actual.get(i);
        return this.literalComesFrom(currentLiteral, expectedOriginalLiteral);
    }

    public ImmutableLiteralsListAssert literalPositionComesFrom(
            int literalIndex, int termIndex,
            Literal expectedOriginalLiteral, int expectedOriginalTerm
    ) {
        Literal currentLiteral = this.actual.get(literalIndex);
        return this.literalPositionComesFrom(currentLiteral, termIndex, expectedOriginalLiteral, expectedOriginalTerm);
    }

    public ImmutableLiteralsListAssert literalPositionComesFrom(
            Literal currentLiteral, int termIndex,
            Literal expectedOriginalLiteral, int expectedOriginalTerm
    ) {
        Optional<LiteralPosition> actualOriginalLiteralPosition = actual.getOriginalLiteralPosition(currentLiteral, termIndex);
        LiteralPosition currentLiteralPosition = new LiteralPosition(currentLiteral, termIndex);
        LiteralPosition expectedOriginalLiteralPosition = new LiteralPosition(expectedOriginalLiteral, expectedOriginalTerm);
        Assertions.assertThat(actualOriginalLiteralPosition)
                .as("No original literal position for " + currentLiteralPosition)
                .isPresent()
                .as("Original literal position of " + currentLiteralPosition + " is " + actualOriginalLiteralPosition.get() + ", but has not the same reference as " + expectedOriginalLiteralPosition)
                .contains(expectedOriginalLiteralPosition);
        return this;
    }

    public ImmutableLiteralsListAssert literalPositionHasNoOriginal(int literalIndex, int termIndex) {
        assertThat(actual).hasSizeGreaterThan(literalIndex);

        Literal actualLit = actual.get(literalIndex);
        LiteralPosition actualLiteralPosition = new LiteralPosition(actualLit, termIndex);
        Assertions.assertThat(actual.getOriginalLiteralPosition(actualLit, termIndex))
                .as("Literal Position" + actualLiteralPosition + " has an original literal position")
                .isNotPresent();

        return this;
    }

    /* CONTAINMENT ASSERTS */
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
                        .containsVariables(variableNames)
                        .asOrdinaryLiteral()
                        .isPositive(isPositive)
                        .hasPredicate(predicateName, variableNames.length)

        );
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert containsComparisonBuiltInLiteral(String leftVariableName, String comparisonOperator, String rightVariableName) {
        Assertions.assertThat(actual).anySatisfy(
                lit -> LiteralAssert.assertThat(lit)
                        .containsVariables(leftVariableName, rightVariableName)
                        .asComparisonBuiltInLiteral()
                        .hasComparisonOperation(comparisonOperator)

        );
        return this;

    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert hasLiteral(int index, String expectedLiteralString) {
        Assertions.assertThat(actual)
                .withFailMessage("Expecting to have some element at index %d", index)
                .hasSizeGreaterThan(index);

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
        } else {
        } //Todo: implement this!!!

//        else if (expectedLiteral instanceof BuiltInLiteral expectedBIL) {
//            Assertions.assertThat(actualLiteral)
//                    .asInstanceOf(InstanceOfAssertFactories.type(BuiltInLiteral.class))
//                    .has
//
//            throw new RuntimeException("Not implemented yet!");
//        }
        return this;
    }

    public void containsExactlyLiteralsOf(List<String> sortedLiterals) {
        for (int i = 0; i < sortedLiterals.size(); i++) {
            hasLiteral(i, sortedLiterals.get(i));
        }
    }

    /* ISOMORPHIC ASSERTS */

    /**
     * Checks whether the actual literals have an isomorphism to the expected literals without
     * changing the variable names given by parameter.
     * <p>
     * This assert is useful, for instance, to check the correct unfolding of a derived atom. E.g.: suppose an atom
     * "P(a)" with derivation rule "P(x) :- R(x, a)". Its unfolding should bring a result isomorphic to "R(a,a')"
     * possibly changing the variable "a'" but not "a".
     *
     * @param expected            not null
     * @param varNamesNotToChange not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert isIsomorphicToWithoutReplacingVariables(ImmutableLiteralsList expected, String... varNamesNotToChange) {
        boolean haveIsomorphism = new IsomorphismComparator(isomorphismOptions).areIsomorphic(actual, expected, varNamesNotToChange);
        Assertions.assertThat(haveIsomorphism)
                .describedAs("Actual literal list: " + actual + " \n" +
                        "has no isomorphism with the expected literal list: " + expected +
                        " without changing the variable names: " + Arrays.toString(varNamesNotToChange)).isTrue();
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert isIsomorphicTo(ImmutableLiteralsList expectedLiteralsList) {
        boolean haveIsomorphism = new IsomorphismComparator(isomorphismOptions).areIsomorphic(actual, expectedLiteralsList);
        Assertions.assertThat(haveIsomorphism)
                .describedAs("Actual literal list: " + actual + " \n" +
                        "has no isomorphism with the expected literal list: " + expectedLiteralsList).isTrue();
        return this;
    }

    public ImmutableLiteralsListAssert usingIsomorphismOptions(IsomorphismOptions options) {
        isomorphismOptions = new IsomorphismOptions(options);
        return this;
    }

    /* AUXILIARS */
    @Override
    protected LiteralAssert toAssert(Literal value, String description) {
        return LiteralAssert.assertThat(value).as(description);
    }

    @Override
    protected ImmutableLiteralsListAssert newAbstractIterableAssert(Iterable<? extends Literal> iterable) {
        return assertThat(new ImmutableLiteralsList(newArrayList(iterable)));
    }


}
