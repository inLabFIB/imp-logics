package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IsomorphismComparatorTest {

    @Nested
    class LiteralListIsomorphismTest {

        // Parametrizado por:
        // - Nombres de predicados derivados (changeDerivedPredicateNameAllowed)
        // - Nombres de variables (changeVariableNamesAllowed)
        // - Orden de literales (changeLiteralOrderAllowed)

        @Nested
        class ChangingDerivedPredicateName {

        }

        @Nested
        class ChangingVariableNames {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicLiteralListsAllowingVariableNamesChanges")
            public void changeVariableNamesAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = true;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

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
            public void changeVariableNamesAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = true;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListIsTheSame_returnTrue() {
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = false;
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListHaveDifferentVariableNames_returnFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(a), Q(a, b)");
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);
                assertThat(isIsomorphism).isFalse();
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
        class ChangingLiteralOrder {
            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            public void changeLiteralOrderAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            public void changeLiteralOrderAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            public void changeLiteralOrderNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            public void changeLiteralOrderNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = false;
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs("name").isFalse();
            }

            private static Stream<Arguments> provideSameLiteralListsInDifferentOrder() {
                return Stream.of(
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

        }
    }

    @Nested
    class LogicConstraintIsomorphismTest {

    }


    @Nested
    class DerivationRuleIsomorphismTest {

    }
}