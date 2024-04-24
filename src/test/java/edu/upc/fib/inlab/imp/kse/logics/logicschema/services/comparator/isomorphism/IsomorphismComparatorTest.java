package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IsomorphismComparatorTest {

    @Nested
    class PredicateIsomorphismTest {

        @Test
        void should_returnTrue_whenPredicateAreIsomorphic() {
            IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
            Predicate predicate1 = DerivedPredicateMother.createDerivedPredicate("P", """
                        P(x) :- Q(x, y)
                    """);
            Predicate predicate2 = DerivedPredicateMother.createDerivedPredicate("P", """
                        P(x) :- Q(x, y)
                    """);
            boolean isIsomorphism = isomorphismComparator.areIsomorphic(predicate1, predicate2);
            assertThat(isIsomorphism).isTrue();
        }

        @Test
        void should_returnFalse_whenPredicatesAreNotIsomorphic() {
            IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
            Predicate predicate1 = DerivedPredicateMother.createDerivedPredicate("P", """
                        P(x) :- Q(x, y)
                    """);
            Predicate predicate2 = DerivedPredicateMother.createDerivedPredicate("P", """
                        P(x) :- Q(x, y), R(x, y)
                    """);
            boolean isIsomorphism = isomorphismComparator.areIsomorphic(predicate1, predicate2);
            assertThat(isIsomorphism).isFalse();
        }
    }

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
                void parameterIndependent_returnFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }
            }

            @Nested
            class BooleanBuiltInLiteralCases {

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

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBooleanBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBooleanBuiltInLiteralsNonIsomorphic")
                void should_returnFalse_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }
            }

            @Nested
            class ComparisonBuiltInLiteralCases {

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

                @ParameterizedTest
                @ValueSource(strings = {"=", "<>"})
                void should_returnTrue_whenBuiltInLiteralsAreIsomorphic_ButNotWithFirstPossibleMap(String selfSymmetricOperator) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("x" + selfSymmetricOperator + "z, T(x, y), S(x,z)");
                    ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("z" + selfSymmetricOperator + "x, T(x, y), S(x,z)");
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).isTrue();
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                @ParameterizedTest
                @MethodSource("provideBuiltInLiteralsNotIsomorphic")
                void should_returnFalse_whenBuiltInLiteralsAreNotIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }
            }

            @Nested
            class CustomBuiltInLiteralCases {

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

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideCustomBuiltInLiteralsIsomorphic")
                void should_returnTrue_whenContainsCustomBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isTrue();
                }

                @ParameterizedTest(name = "[{index}] {0}")
                @MethodSource("provideCustomBuiltInLiteralsNotIsomorphic")
                void should_returnFalse_whenContainsCustomBuiltInLiteralsAreIsomorphic(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                    IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                    boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                    assertThat(isIsomorphism).describedAs(name).isFalse();
                }

            }
        }

        @Nested
        class ParameterIntegrationTests {
            @Test
            void should_returnTrue_whenCombiningSeveralTypesOfLiteralsAndParameters() {
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

                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));

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
                        ),
                        Arguments.of(
                                "Several derivation rules, with several depths, changing the order or rules",
                                ImmutableLiteralsListMother.create(
                                        "P(x), Q(x), R(x)",
                                        """ 
                                                P(x) :- A(x)
                                                P(x) :- B(x)
                                                Q(x) :- B(x)
                                                R(x) :- B(x)
                                                A(x) :- a(x)
                                                B(x) :- a(x)
                                                """),
                                ImmutableLiteralsListMother.create(
                                        "P'(x), Q'(x), R'(x)",
                                        """ 
                                                P'(x) :- B'(x)
                                                P'(x) :- A'(x)
                                                Q'(x) :- B'(x)
                                                R'(x) :- B'(x)
                                                A'(x) :- a(x)
                                                B'(x) :- a(x)
                                                """)
                        )
                );
            }

            private static Stream<Arguments> provideDerivedPredicateNamesRenamedNotIsomorphic() {
                return Stream.of(
                        Arguments.of(
                                "With different derivation rules predicate name",
                                ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)"),
                                ImmutableLiteralsListMother.create("P(x), Der2(x)", "Der2(x) :- R(x, y), S(y)")
                        ),
                        Arguments.of(
                                "DerivedPredicates, from inner levels, cannot be mapped to two different isomorphic derived predicates",
                                ImmutableLiteralsListMother.create(
                                        "P(x), Q(x)",
                                        """
                                                P(x) :- R(x), S(x)
                                                Q(x) :- R(x), S(x)
                                                S(x) :- T(x)
                                                """
                                ),
                                ImmutableLiteralsListMother.create(
                                        "P(x), Q(x)",
                                        """
                                                P(x) :- R(x), S1(x)
                                                Q(x) :- R(x), S2(x)
                                                S1(x) :- T(x)
                                                S2(x) :- T(x)
                                                """
                                )
                        ),
                        Arguments.of(
                                "DerivedPredicates, from inner levels, cannot be mapped to two different isomorphic derived predicates (same number of rules)",
                                ImmutableLiteralsListMother.create(
                                        "P(x), Q(x)",
                                        """
                                                P(x) :- R2(x), S(x)
                                                Q(x) :- R1(x), S(x)
                                                S(x) :- T(x)
                                                R1(x) :- U(x)
                                                R2(x) :- U(x)
                                                """
                                ),
                                ImmutableLiteralsListMother.create(
                                        "P(x), Q(x)",
                                        """
                                                P(x) :- R(x), S1(x)
                                                Q(x) :- R(x), S2(x)
                                                S1(x) :- T(x)
                                                S2(x) :- T(x)
                                                R(x) :- U(x)
                                                """
                                )
                        ),
                        Arguments.of(
                                "Two derived predicates are not isomorphic if one of its derivation rules is not isomorphic",
                                ImmutableLiteralsListMother.create(
                                        "P(x), Der(x)",
                                        """
                                                Der(x) :- R(x, y)
                                                Der(x) :- R2(x, y)
                                                Der(x) :- R3(x, y)
                                                """
                                ),
                                ImmutableLiteralsListMother.create(
                                        "P(x), Der(x)",
                                        """
                                                Der(x) :- R(x, y)
                                                Der(x) :- R2(x, y)
                                                Der(x) :- R4(x, y)
                                                """
                                )
                        )

                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamed")
            void changeDerivedPredicateNameAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {

                boolean changingDerivedPredicateNameAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, changingDerivedPredicateNameAllowed));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamedNotIsomorphic")
            void changeDerivedPredicateNameAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changingDerivedPredicateNameAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, changingDerivedPredicateNameAllowed));
                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            @Test
            void changeDerivedPredicateNameNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Der(x)", "Der(x) :- R(x, y)");

                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, changingDerivedPredicateNameAllowed));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideDerivedPredicateNamesRenamed")
            void changeDerivedPredicateNameNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changingDerivedPredicateNameAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, false, changingDerivedPredicateNameAllowed));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

        }

        @Nested
        class ChangingVariableNames {

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

            @Test
            void changeVariableNamesAllowed_resultTrue_whenVarNamesNotToChangeAreCompatible() {
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(true, false, false));
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, b)");
                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2, "x");

                assertThat(isIsomorphism).isTrue();
            }

            @Test
            void changeVariableNamesAllowed_resultFalse_whenVarNamesNotToChangeAreIncompatible() {
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(true, false, false));
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, b)");
                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2, "y");

                assertThat(isIsomorphism).isFalse();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicLiteralListsAllowingVariableNamesChanges")
            void changeVariableNamesAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(changeVariableNamesAllowed, false, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideNonIsomorphicLiteralListsAllowingVariableNamesChanges")
            void changeVariableNamesAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeVariableNamesAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(changeVariableNamesAllowed, false, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }

            @Test
            void changeVariableNamesNotAllowed_whenLiteralsListIsTheSame_returnTrue() {
                boolean changeVariableNamesAllowed = false;
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(changeVariableNamesAllowed, false, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @Test
            void changeVariableNamesNotAllowed_whenLiteralsListHaveDifferentVariableNames_returnFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(a), Q(a, b)");
                boolean changeVariableNamesAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(changeVariableNamesAllowed, false, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);
                assertThat(isIsomorphism).isFalse();
            }
        }

        @Nested
        class ChangingLiteralOrder {
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

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            void changeLiteralOrderAllowed_resultTrue(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, changeLiteralOrderAllowed, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isTrue();
            }

            @Test
            void changeLiteralOrderAllowed_resultFalse() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), R(x, y)");
                boolean changeLiteralOrderAllowed = true;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, changeLiteralOrderAllowed, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isFalse();
            }

            @Test
            void changeLiteralOrderNotAllowed_resultTrue() {
                ImmutableLiteralsList list1 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                ImmutableLiteralsList list2 = ImmutableLiteralsListMother.create("P(x), P(x), Q(x, y)");
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, changeLiteralOrderAllowed, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).isTrue();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideSameLiteralListsInDifferentOrder")
            void changeLiteralOrderNotAllowed_resultFalse(String name, ImmutableLiteralsList list1, ImmutableLiteralsList list2) {
                boolean changeLiteralOrderAllowed = false;
                IsomorphismComparator isomorphismComparator = new IsomorphismComparator(new IsomorphismOptions(false, changeLiteralOrderAllowed, false));

                boolean isIsomorphism = isomorphismComparator.areIsomorphic(list1, list2);

                assertThat(isIsomorphism).describedAs(name).isFalse();
            }
        }
    }

    @Nested
    class LogicConstraintIsomorphismTest {
        @Test
        void should_invokeImmutableLiteralsListIsomorphism_withLogicConstraintBodies() {
            LogicConstraint constraint1 = createDummy();
            LogicConstraint constraint2 = createDummy();

            IsomorphismComparator spyComparator = spy(new IsomorphismComparator(new IsomorphismOptions(false, false, false)));
            spyComparator.areIsomorphic(constraint1, constraint2);

            verify(spyComparator).areIsomorphic(constraint1.getBody(), constraint2.getBody());
        }

        private LogicConstraint createDummy() {
            return LogicConstraintMother.createWithoutID(":- P(x)");
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void should_returnSameResult_asImmutableLiteralsListIsomorphism(boolean expectedResult) {
            LogicConstraint constraint1 = createDummy();
            LogicConstraint constraint2 = createDummy();

            IsomorphismComparator spyComparator = spy(new IsomorphismComparator(new IsomorphismOptions(false, false, false)));
            doReturn(expectedResult).when(spyComparator).areIsomorphic(any(ImmutableLiteralsList.class), any(ImmutableLiteralsList.class));

            boolean areIsomorphic = spyComparator.areIsomorphic(constraint1, constraint2);

            assertThat(areIsomorphic).isEqualTo(expectedResult);
        }

        @Test
        void should_returnTrue_whenLogicConstraintsAreIsomorphic_butHaveDifferentIDs() {
            LogicConstraint constraint1 = LogicConstraintMother.createWithID("@1 :- P(x)");
            LogicConstraint constraint2 = LogicConstraintMother.createWithID("@2 :- P(x)");

            IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
            boolean areIsomorphic = comparator.areIsomorphic(constraint1, constraint2);

            assertThat(areIsomorphic).describedAs("Identical logic constraints should be isomorphic despite having different IDs").isTrue();
        }


    }

    @Nested
    class DerivationRuleIsomorphismTest {

        @Nested
        class ParameterIndependentTest {

            /**
             * The derivation rules are already tested within the immutable literals list.
             */
            @Test
            void should_returnTrue_whenDerivationRulesAreIsomorphic() {
                DerivationRule rule1 = DerivationRuleMother.create("P(x) :- Q(x, y), not(R(x))");
                DerivationRule rule2 = DerivationRuleMother.create("P(x) :- Q(x, y), not(R(x))");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(rule1, rule2);
                assertThat(areIsomorphic).isTrue();
            }

            @Test
            void should_returnFalse_whenDerivationRulesAreNotIsomorphic() {
                DerivationRule rule1 = DerivationRuleMother.create("P(x) :- Q(x, y), not(R(x))");
                DerivationRule rule2 = DerivationRuleMother.create("P(x) :- Q(x, y), R(x)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(rule1, rule2);
                assertThat(areIsomorphic).isFalse();
            }

        }

        @Nested
        class ParameterIntegrationTest {

            private static Stream<Arguments> provideNonIsomorphicDerivationRules() {

                return Stream.of(
                        Arguments.of(
                                "Head with different term names",
                                DerivationRuleMother.create("P(x) :- Q(x, y)"),
                                DerivationRuleMother.create("P(y) :- Q(x, y)")
                        ),
                        Arguments.of(
                                "Head with term which does not exist in normal clause",
                                DerivationRuleMother.create("P(x) :- Q(x, y)"),
                                DerivationRuleMother.create("P(a) :- Q(x, y)")
                        )
                );

            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideNonIsomorphicDerivationRules")
            void should_returnFalse_whenDerivationRulesAreNotIsomorphic(String name, DerivationRule rule1, DerivationRule rule2) {
                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));
                boolean areIsomorphic = comparator.areIsomorphic(rule1, rule2);
                assertThat(areIsomorphic).describedAs(name).isFalse();
            }

        }
    }

    @Nested
    class SchemaIsomorphismTest {
        private static Stream<Arguments> providePredicateSpecs() {
            return Stream.of(
                    Arguments.of(
                            "Different predicates",
                            List.of(new PredicateSpec("Q", 1)),
                            List.of(new PredicateSpec("S", 1))
                    ),
                    Arguments.of(
                            "Different arities",
                            List.of(new PredicateSpec("Q", 1)),
                            List.of(new PredicateSpec("Q", 2))
                    )
            );
        }

        private static Stream<Arguments> provideIsomorphicSchemas() {
            return Stream.of(
                    Arguments.of(
                            "Case 1",
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @1 :- P(x), not(Q(x)), Der1(x), x > 4
                                            @2 :- P(x), not(Q(x)), Der2(x), x < 0
                                            Der1(x) :- S(x, y)
                                            Der2(x) :- S(x, y), not(T(y))
                                            Der2(x) :- S2(x, y), not(T(y))
                                            """),
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @101 :- P(a), not(Q(a)), Der2'(a), a < 0
                                            @102 :- P(a), not(Q(a)), a > 4 , Der1'(a)
                                            Der2'(a) :- not(T(b)), S(a, b)
                                            Der1'(a) :- S(a, b)
                                            Der2'(a) :- S2(a, b), not(T(b))
                                            """)
                    ),
                    Arguments.of(
                            "Case 2",
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            P(x) :- A(x)
                                            P(x) :- B(x)
                                            Q(x) :- B(x)
                                            R(x) :- B(x)
                                            A(x) :- a(x)
                                            B(x) :- a(x)
                                            """),
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            P'(x) :- B'(x)
                                            P'(x) :- A'(x)
                                            Q'(x) :- B'(x)
                                            R'(x) :- B'(x)
                                            A'(x) :- a(x)
                                            B'(x) :- a(x)
                                            """)
                    )
            );
        }

        private static Stream<Arguments> provideNonIsomorphicSchemas() {
            return Stream.of(
                    Arguments.of(
                            "Cannot map the same predicate, from logic constraints, to two different predicates",
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @1 :- P1(x), Q(x)
                                            @2 :- P2(x), Q(x)
                                            Q(x) :- R(x)
                                            S(x) :- R(x)
                                            """),
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @1 :- P1(x), Q(x)
                                            @2 :- P2(x), S(x)
                                            Q(x) :- R(x)
                                            S(x) :- R(x)
                                            """)
                    ),
                    Arguments.of(
                            "Cannot map the same predicate, from derivation rules, to several predicates",
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            P(x) :- A(x)
                                            P(x) :- B(x)
                                            Q(x) :- B(x)
                                            R(x) :- B(x)
                                            A(x) :- a(x)
                                            B(x) :- a(x)
                                            """),
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            P'(x) :- A'(x)
                                            P'(x) :- B'(x)
                                            Q'(x) :- A'(x)
                                            R'(x) :- B'(x)
                                            A'(x) :- a(x)
                                            B'(x) :- a(x)
                                            """)
                    ),
                    Arguments.of(
                            "Cannot map the same predicate, from logic constraint and derivation rule, to two different predicates",
                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @1 :- A(x)
                                            P(x) :- A(x)
                                            A(x) :- a(x)
                                            B(x) :- a(x)
                                            """),

                            LogicSchemaMother.buildLogicSchemaWithIDs(
                                    """
                                            @1 :- A'(x)
                                            P(x) :- B'(x)
                                            A'(x) :- a(x)
                                            B'(x) :- a(x)
                                            """)
                    )
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("providePredicateSpecs")
        void should_returnFalse_whenSchemasDiffer_inBasePredicates_evenWhenTheyAreNotUsed(String name, List<PredicateSpec> predicates1, List<PredicateSpec> predicates2) {
            LogicSchema schema1 = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates("@1 :- P(x)", predicates1);
            LogicSchema schema2 = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates("@1 :- P(x)", predicates2);

            IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
            boolean areIsomorphic = comparator.areIsomorphic(schema1, schema2);

            assertThat(areIsomorphic).describedAs(name).isFalse();
        }

        @Test
        void should_returnFalse_whenSchemasDiffer_inDerivedPredicates_evenWhenTheyAreNotUsed() {
            LogicSchema schema1 = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates("P(x) :- R(x)");
            LogicSchema schema2 = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates("Q(x) :- S(x)");

            IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
            boolean areIsomorphic = comparator.areIsomorphic(schema1, schema2);

            assertThat(areIsomorphic).isFalse();
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("provideIsomorphicSchemas")
        void should_returnTrue_whenSchemasAreIsomorphic(String name, LogicSchema schema1, LogicSchema schema2) {
            IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));
            boolean areIsomorphic = comparator.areIsomorphic(schema1, schema2);
            assertThat(areIsomorphic).describedAs(name).isTrue();
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("provideNonIsomorphicSchemas")
        void should_returnFalse_whenSchemaDiffer_inNumberOfRepeatedIsomorphicDerivationRules(String name, LogicSchema schema1, LogicSchema schema2) {
            IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));
            boolean areIsomorphic = comparator.areIsomorphic(schema1, schema2);
            assertThat(areIsomorphic).describedAs(name).isFalse();
        }
    }

    @Nested
    class QueryIsIsomorphismTest {

        @Nested
        class NotPermittingAnyChange {
            @Test
            void should_returnTrue_whenQueriesAreIdentical() {
                Query query1 = QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q(x, y)");
                Query query2 = QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q(x, y)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).isTrue();
            }

            @Test
            void should_returnFalse_whenQueriesRetrieveDifferentTerms() {
                Query query1 = QueryMother.createQuery(List.of("x"), "P(x, y), Q(x, y)");
                Query query2 = QueryMother.createQuery(List.of("y"), "P(x, y), Q(x, y)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).isFalse();
            }

            @Test
            void should_returnFalse_whenQueriesDoNotRetrieveTheSameNumberOfTerms() {
                Query query1 = QueryMother.createQuery(List.of("x", "x"), "P(x, y), Q(x, y)");
                Query query2 = QueryMother.createQuery(List.of("x"), "P(x, y), Q(x, y)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).isFalse();
            }

            @Test
            void should_returnFalse_whenQueryBodiesContainDifferentLiterals() {
                Query query1 = QueryMother.createQuery(List.of("x", "y"), "P(x, y)");
                Query query2 = QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q(x, y)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).isFalse();
            }

            @Test
            void should_returnFalse_whenQueryBodiesContainDifferentLiterals_evenIfIdentical() {
                Query query1 = QueryMother.createQuery(List.of("x", "y"), "P(x, y)");
                Query query2 = QueryMother.createQuery(List.of("x", "y"), "P(x, y), P(x, y)");

                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(false, false, false));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).isFalse();
            }
        }

        @Nested
        class PermittingAllChanges {

            private static Stream<Arguments> provideNonIsomorphicQueriesUpToAllPossibleChanges() {

                return Stream.of(
                        Arguments.of(
                                "Return terms is in different position",
                                QueryMother.createQuery(List.of("x"), "P(x, y)"),
                                QueryMother.createQuery(List.of("x"), "P(y, x)")
                        ),
                        Arguments.of(
                                "Second query has more literals",
                                QueryMother.createQuery(List.of("x", "y"), "P(x, y)"),
                                QueryMother.createQuery(List.of("a", "b"), "P(a, b), Q(a, b)")
                        ),
                        Arguments.of(
                                "Second query repeats variable names",
                                QueryMother.createQuery(List.of("x"), "Q(x, y)"),
                                QueryMother.createQuery(List.of("a"), "Q(a, a)")
                        ),
                        Arguments.of(
                                "First query repeats variable names",
                                QueryMother.createQuery(List.of("a"), "Q(a, a)"),
                                QueryMother.createQuery(List.of("x"), "Q(x, y)")
                        ),
                        Arguments.of(
                                "Second query has a derivation rule not isomorphic",
                                QueryMother.createQuery(List.of("a"), "Q1(a, b)", "Q1(a,b) :- T(a,b)"),
                                QueryMother.createQuery(List.of("a"), "Q2(a, b)", "Q2(a,b) :- T(a,b), T(b,a)")
                        )
                );

            }

            private static Stream<Arguments> provideIsomorphicQueriesUpToAllPossibleChanges() {

                return Stream.of(
                        Arguments.of(
                                "Changing variable names",
                                QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q(x, y)"),
                                QueryMother.createQuery(List.of("a", "b"), "P(a, b), Q(a, b)")
                        ),
                        Arguments.of(
                                "Changing literals order names",
                                QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q(x, y)"),
                                QueryMother.createQuery(List.of("x", "y"), "Q(x, y), P(x, y)")
                        ),
                        Arguments.of(
                                "Changing derived predicate names",
                                QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q1(x, y)", "Q1(x, y) :- T(x, y)"),
                                QueryMother.createQuery(List.of("x", "y"), "P(x, y), Q2(x, y)", "Q2(x, y) :- T(x, y)")
                        )
                );
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideNonIsomorphicQueriesUpToAllPossibleChanges")
            void should_returnFalse_whenQueriesAreNotIsomorphicUpToAllChanges(String name, Query query1, Query query2) {
                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).describedAs(name).isFalse();
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("provideIsomorphicQueriesUpToAllPossibleChanges")
            void should_returnTrue_whenQueriesAreIsomorphicUpToPermittedChanges(String name, Query query1, Query query2) {
                IsomorphismComparator comparator = new IsomorphismComparator(new IsomorphismOptions(true, true, true));
                boolean areIsomorphic = comparator.areIsomorphic(query1, query2);
                assertThat(areIsomorphic).describedAs(name).isTrue();
            }
        }
    }

}