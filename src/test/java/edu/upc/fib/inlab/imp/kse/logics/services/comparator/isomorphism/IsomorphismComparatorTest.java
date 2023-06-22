package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonOperator;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IsomorphismComparatorTest {

    @Nested
    class LiteralListIsomorphismTest {

        @Nested
        class ParameterIndependentTests {

            @Nested
            class FailedTestCases {
                private static Stream<Arguments> failCases() {
                    return Stream.of(
                            Arguments.of(
                                    "Can not compare base literals with derived literals",
                                    ImmutableLiteralsListMother.create("P(x), Der(x)"),
                                    ImmutableLiteralsListMother.create("P(x), Der" +
                                            "(x)", "Der(x) :- R(x, y)")
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
                            ),
                            Arguments.of(
                                    "Can not compare derived literals with different bodies",
                                    ImmutableLiteralsListMother.create("P(x)", "P(x) :- Q(x)"),
                                    ImmutableLiteralsListMother.create("P(x)", "P(x) :- R(x)")
                            )
                    );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("failCases")
                public void parameterIndependent_returnFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }
            }

            @Nested
            class BooleanBuiltInLiteralCases {

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBooleanBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                private static Stream<Arguments> provideBooleanBuiltInLiteralsIsomorphic() {
                    return Stream.of(
                            Arguments.of(
                                    "Trivial TRUE()",
                                    ImmutableLiteralsListMother.create("TRUE()"),
                                    ImmutableLiteralsListMother.create("TRUE()")
                            ),
                            Arguments.of(
                                    "Trivial FALSE()",
                                    ImmutableLiteralsListMother.create("FALSE()"),
                                    ImmutableLiteralsListMother.create("FALSE()")
                            ),
                            Arguments.of(
                                    "Literal with more literals",
                                    ImmutableLiteralsListMother.create("P(x), FALSE(), x < 1"),
                                    ImmutableLiteralsListMother.create("P(x), FALSE(), x < 1")
                            )
                    );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBooleanBuiltInLiteralsNonIsomorphic")
                void should_returnFalse_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }

                private static Stream<Arguments> provideBooleanBuiltInLiteralsNonIsomorphic() {
                    return Stream.of(
                            Arguments.of(
                                    "Non isomorphic FALSE() vs TRUE()",
                                    ImmutableLiteralsListMother.create("FALSE()"),
                                    ImmutableLiteralsListMother.create("TRUE()")
                            ),
                            Arguments.of(
                                    "Non isomorphic TRUE() vs FALSE()",
                                    ImmutableLiteralsListMother.create("TRUE()"),
                                    ImmutableLiteralsListMother.create("FALSE()")
                            ),
                            Arguments.of(
                                    "With more literals",
                                    ImmutableLiteralsListMother.create("P(x), TRUE(), x < 1"),
                                    ImmutableLiteralsListMother.create("P(x), FALSE(), x < 1")
                            ),
                            Arguments.of(
                                    "With derivation rule",
                                    ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y), FALSE()"),
                                    ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y), TRUE()")
                            )
                    );
                }
            }

            @Nested
            class ComparisonBuiltInLiteralCases {
                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                private static Stream<Arguments> provideBuiltInLiteralsIsomorphic() {
                    List<Arguments> arguments = new LinkedList<>();
                    for (ComparisonOperator operator : ComparisonOperator.values()) {
                        ComparisonOperator symmetricOperator = operator.getSymmetric();
                        String comparisonBuiltinLiteral = "x" + operator.getSymbol() + "y";
                        String comparisonBuiltinLiteralReversed = "y" + symmetricOperator.getSymbol() + "x";
                        arguments.add(Arguments.of(
                                "Trivial " + operator.getSymbol(),
                                ImmutableLiteralsListMother.create("P(x, y), " + comparisonBuiltinLiteral),
                                ImmutableLiteralsListMother.create("P(x, y), " + comparisonBuiltinLiteral)
                        ));

                        arguments.add(Arguments.of(
                                "Reversed " + operator.getSymbol(),
                                ImmutableLiteralsListMother.create("P(x, y), " + comparisonBuiltinLiteral),
                                ImmutableLiteralsListMother.create("P(x, y), " + comparisonBuiltinLiteralReversed)
                        ));
                    }
                    return arguments.stream();
                }

                @ParameterizedTest
                @MethodSource("provideBuiltInLiteralsNotIsomorphic")
                void should_returnFalse_whenBuiltInLiteralsAreNotIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }

                private static Stream<Arguments> provideBuiltInLiteralsNotIsomorphic() {
                    List<Arguments> arguments = new LinkedList<>();
                    for (ComparisonOperator operator : ComparisonOperator.values()) {
                        List<ComparisonOperator> otherOperators = Arrays.stream(ComparisonOperator.values())
                                .filter(o -> !o.equals(operator)
                                ).toList();
                        for (ComparisonOperator otherOperator : otherOperators) {
                            arguments.add(Arguments.of(
                                    "Non isomorphic " + operator.getSymbol() + " vs " + otherOperator.getSymbol(),
                                    ImmutableLiteralsListMother.create("x" + operator.getSymbol() + "y"),
                                    ImmutableLiteralsListMother.create("x" + otherOperator.getSymbol() + "y")
                            ));
                            arguments.add(Arguments.of(
                                    "Non isomorphic " + operator.getSymbol() + " vs " + otherOperator.getSymbol() + " with more literals",
                                    ImmutableLiteralsListMother.create("P(x, y), x" + operator.getSymbol() + "y"),
                                    ImmutableLiteralsListMother.create("P(x, y), x" + otherOperator.getSymbol() + "y")
                            ));
                        }
                    }
                    return arguments.stream();
                }
            }

            @Nested
            class CustomBuiltInLiteralCases {

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideCustomBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenContainsCustomBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                private static Stream<Arguments> provideCustomBuiltInLiteralsIsomorphic() {
                    return Stream.of(
                            Arguments.of(
                                    "Trivial custom builtin literal",
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom(x)", Set.of("custom")),
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom(x)", Set.of("custom"))
                            ),
                            Arguments.of(
                                    "With more literals",
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("P(x), custom(x), x < 1", Set.of("custom")),
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("P(x), custom(x), x < 1", Set.of("custom"))
                            )
                    );
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideCustomBuiltInLiteralsNotIsomorphic")
                void should_returnFalse_whenContainsCustomBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, false);
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }

                private static Stream<Arguments> provideCustomBuiltInLiteralsNotIsomorphic() {
                    return Stream.of(
                            Arguments.of(
                                    "Custom builtin literal does not match with ordinary literal",
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom(x)", Set.of("custom")),
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom(x)", Set.of())
                            ),
                            Arguments.of(
                                    "Custom builtin literal does not match with other custom builtin literal with different name",
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom(x)", Set.of("custom")),
                                    ImmutableLiteralsListMother.createWithCustomBuiltinLiterals("custom2(x)", Set.of("custom2"))
                            )
                    );
                }

            }
        }

        @Nested
        class ParameterIntegrationTests {
            @Test
            public void should_returnTrue_whenCombiningSeveralTypesOfLiteralsAndParameters() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("""
                                P(x), Q(x, y), Q(y, z), z < x, x = y
                                """,
                        """
                                P(x) :- S(x, x), not(T(x))
                                """
                );
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("""
                                Q(b, c), a = b, P1(a), Q(a, b), c < a
                                """,
                        """
                                P1(e) :- S(e,e), not(T(e))
                                """);

                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(true, true, true);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }
        }

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

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            public void changeDerivedPredicateNameAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Der2(x)", "Der2(x) :- R(x, y), S(y)");

                boolean changingDerivedPredicateNameAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            public void changeDerivedPredicateNameNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");

                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamed")
            public void changeDerivedPredicateNameNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, false, changingDerivedPredicateNameAllowed);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

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

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

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

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListIsTheSame_returnTrue() {
                boolean changeVariableNamesAllowed = false;
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @Test
            public void changeVariableNamesNotAllowed_whenLiteralsListHaveDifferentVariableNames_returnFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(a), Q(a, b)");
                boolean changeVariableNamesAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(changeVariableNamesAllowed, false, false);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
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

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            public void changeLiteralOrderAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            public void changeLiteralOrderNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            public void changeLiteralOrderNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(false, changeLiteralOrderAllowed, false);

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

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