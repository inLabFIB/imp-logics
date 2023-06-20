package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import org.junit.jupiter.api.Disabled;
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

        @Nested
        class ParameterIndependentTests {

            private static Stream<Arguments> failCases() {
                return Stream.of(
                        Arguments.of(
                                "Can not compare base literals with derived literals",
                                ImmutableLiteralsListMother.create("P(x), Der(x)"),
                                ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)")
                        ),
                        Arguments.of(
                                "Can not compare positive with negated literals",
                                ImmutableLiteralsListMother.create("P(x)"),
                                ImmutableLiteralsListMother.create("not(P(x))")
                        ),
                        Arguments.of(
                                "Can not compare literals with different arity",
                                ImmutableLiteralsListMother.create("P(x)"),
                                ImmutableLiteralsListMother.create("P(x,x)")
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("failCases")
            public void parameterIndependent_returnFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(true, true, true);
                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);
                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

        }


        // Parametrizado por:
        // - Nombres de predicados derivados (changeDerivedPredicateNameAllowed)
        // - Nombres de variables (changeVariableNamesAllowed)
        // - Orden de literales (changeLiteralOrderAllowed)

        @Disabled("WIP")
        @Nested
        class ChangingDerivedPredicateName {

            private static Stream<Arguments> provideDerivedPredicateNamesRenamed() {
                return Stream.of(
                        Arguments.of(
                                "One derivation rule",
                                ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)"),
                                ImmutableLiteralsListMother.create("P(x), Der2(x)", "Der2(x) :- R(x, y)")
                        ),
                        Arguments.of(
                                "Several derivation rules",
                                ImmutableLiteralsListMother.create(
                                        "P(x), Der(x)",
                                        """ 
                                                Der(x) :- R(x, y)
                                                Der(x) :- S(x)
                                                Der(x) :- T(x)
                                                """),
                                ImmutableLiteralsListMother.create(
                                        "P(x), Der2(x)",
                                        """ 
                                                Der2(x) :- R(x, y)
                                                Der2(x) :- S(x)
                                                Der2(x) :- T(x)
                                                """)
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamed")
            public void changeDerivedPredicateNameAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {

                boolean changingDerivedPredicateNameAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            public void changeDerivedPredicateNameAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Der2(x)", "Der2(x) :- R(x, y), S(y)");

                boolean changingDerivedPredicateNameAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            public void changeDerivedPredicateNameNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");

                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamed")
            public void changeDerivedPredicateNameNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

        }

        @Nested
        class ChangingVariableNames {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicLiteralListsAllowingVariableNamesChanges")
            public void changeVariableNamesAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

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
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListIsTheSame_returnTrue() {
                boolean changeVariableNamesAllowed = false;
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListHaveDifferentVariableNames_returnFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(a), Q(a, b)");
                boolean changeVariableNamesAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

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
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            public void changeLiteralOrderAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            public void changeLiteralOrderNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            public void changeLiteralOrderNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.isIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
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