package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IsomorphismComparatorTest {

    @Nested
    class LiteralListIsomorphismTest {

        // Parametrizado por:
        // - Nombres de variables (changeVariableNamesAllowed)
        // - Nombres de predicados derivados (changeDerivedPredicateNameAllowed)
        // - Orden de literales (changeLiteralOrderAllowed)

        @Nested
        class ChangingVariableNames {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicLiteralListsAllowingVariableNamesChanges")
            public void changeVariableNamesAllowed_resultTrue(String name, ImmutableLiteralsList literalList1, ImmutableLiteralsList literalList2) {
                boolean changeVariableNamesAllowed = true;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(literalList1, literalList2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }


            private static Stream<Arguments> provideIsomorphicLiteralListsAllowingVariableNamesChanges() {
                return Stream.of(
                        Arguments.of(
                                "Trivial case",
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))")
                        ),
                        Arguments.of(
                                "Identical case",
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(x,y), not(Q(y))")
                        ),
                        Arguments.of(
                                "Identical case with repeated variables",
                                ImmutableLiteralsListMother.create("P(a,b), P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(x,y), P(x,y), not(Q(y))")
                        ),
                        Arguments.of(
                                "Identical case with repeated variables in different places",
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(b,a), not(Q(a))")
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideNonIsomorphicLiteralListsAllowingVariableNamesChanges")
            public void changeVariableNamesAllowed_resultFalse(String name, ImmutableLiteralsList literalList1, ImmutableLiteralsList literalList2) {
                boolean changeVariableNamesAllowed = true;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(literalList1, literalList2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            private static Stream<Arguments> provideNonIsomorphicLiteralListsAllowingVariableNamesChanges() {
                return Stream.of(
                        Arguments.of(
                                "Non isomorphic case",
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(x,y), not(Q(x))")
                        ),
                        Arguments.of(
                                "Non isomorphic case with repeated variables",
                                ImmutableLiteralsListMother.create("P(a,b), P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(x,y), P(x,y), not(Q(x))")
                        ),
                        Arguments.of(
                                "Non isomorphic case with repeated variables in different places",
                                ImmutableLiteralsListMother.create("P(a,b)"),
                                ImmutableLiteralsListMother.create("P(x,x)")
                        ),
                        Arguments.of(
                                "Non isomorphic case with constant",
                                ImmutableLiteralsListMother.create("P(a,b), not(Q(b))"),
                                ImmutableLiteralsListMother.create("P(1,b), not(Q(b))")
                        )
                );
            }
        }

        @Nested
        class ChangingDerivedPredicateName {

        }

        @Nested
        class ChangingLiteralOrder {
            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicLiteralListsAllowingLiteralOrderChanges")
            public void changeLiteralOrderAllowed_resultTrue(String name, ImmutableLiteralsList literalList1, ImmutableLiteralsList literalList2) {
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(literalList1, literalList2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Disabled("WIP - Fail non some variables case")
            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideNonIsomorphicLiteralListsAllowingLiteralOrderChanges")
            public void changeLiteralOrderAllowed_resultFalse(String name, ImmutableLiteralsList literalList1, ImmutableLiteralsList literalList2) {
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(literalList1, literalList2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            private static Stream<Arguments> provideIsomorphicLiteralListsAllowingLiteralOrderChanges() {
                return Stream.of(
                        Arguments.of(
                                "Same order",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)")
                        ),
                        Arguments.of(
                                "Non same order",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("Q(x, y), P(x)")
                        ),
                        Arguments.of(
                                "Repeated literals",
                                ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("Q(x, y), P(x), P(x)")
                        ),
                        Arguments.of(
                                "Repeated predicates",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y), P(y)"),
                                ImmutableLiteralsListMother.create("Q(x, y), P(y), P(x)")
                        ),
                        Arguments.of(
                                "Using polarity",
                                ImmutableLiteralsListMother.create("not(P(x)), Q(x, y)"),
                                ImmutableLiteralsListMother.create("Q(x,y), not(P(x))")
                        )
                );
            }

            private static Stream<Arguments> provideNonIsomorphicLiteralListsAllowingLiteralOrderChanges() {
                return Stream.of(
                        Arguments.of(
                                "Non same predicates",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("P(x), R(x, y)")
                        ),
                        Arguments.of(
                                "Non same variables",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("P(a), Q(a, b)")
                        ),
                        Arguments.of(
                                "Non same constants",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("P(1), Q(1, 2)")
                        ),
                        Arguments.of(
                                "Non same polarity",
                                ImmutableLiteralsListMother.create("P(x), Q(x, y)"),
                                ImmutableLiteralsListMother.create("P(x), not(Q(x, y))")
                        )
                );
            }
        }

    }

    @Nested
    class LogicConstraintIsomorphismTest {

    }


    @Nested
    class DerivationRuleIsomorphismTest {

    }

}