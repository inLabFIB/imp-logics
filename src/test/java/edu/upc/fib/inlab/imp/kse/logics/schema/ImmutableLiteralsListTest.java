package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicInstanceOfAssertFactories;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class ImmutableLiteralsListTest {

    @Nested
    class CreateImmutableLiteralsList {
        @Test
        void should_throwException_whenTryCreateImmutableLiteralsListWithElementNull() {
            List<Literal> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableLiteralsList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenTryCreateImmutableLiteralsListWithNull() {
            assertThatThrownBy(() -> new ImmutableLiteralsList((List<Literal>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_createImmutableLiteralsEmptyList() {
            assertThat(new ImmutableLiteralsList()).isEmpty();
        }

        @Test
        void should_createEmptyLiteralList_whenCreatingImmutableLiteralList_withEmptyList() {
            ImmutableLiteralsList actualTermList = new ImmutableLiteralsList(List.of());
            ImmutableLiteralsListAssert.assertThat(actualTermList).isEmpty();
        }
    }

    @Nested
    class ApplySubstitution {
        @Test
        void should_ReturnNewLiteralsList_WithReplacedTerms_WhenApplyingSubstitution() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, y), R(y, z), x < y");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "a")
                    .build();

            ImmutableLiteralsList actualLiteralsList = immutableLiteralsList.applySubstitution(substitution);

            ImmutableLiteralsListAssert.assertThat(actualLiteralsList)
                    .isNotSameAs(immutableLiteralsList)
                    .hasSize(3)
                    .containsOrdinaryLiteral("P", "a", "y")
                    .containsOrdinaryLiteral("R", "y", "z")
                    .containsComparisonBuiltInLiteral("a", "<", "y");
        }

        @Nested
        class Traceability {
            @Test
            void should_rememberOriginalLiterals_WhenApplyingSubstitution() {
                ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, y), R(y, z), x < y");
                Substitution substitution = new SubstitutionBuilder()
                        .addMapping("x", "a")
                        .build();

                ImmutableLiteralsList actualLiteralsList = immutableLiteralsList.applySubstitution(substitution);

                ImmutableLiteralsListAssert.assertThat(actualLiteralsList)
                        .literalComesFrom(0, immutableLiteralsList.get(0))
                        .literalHasNoOriginal(1)
                        .literalComesFrom(2, immutableLiteralsList.get(2));
            }
        }
    }

    @Nested
    class ReturnVariables {
        @Test
        void should_ReturnUsedVariables() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), R(y, 2), x < y");

            Set<Variable> usedVariables = immutableLiteralsList.getUsedVariables();

            assertThat(usedVariables)
                    .hasSize(2)
                    .contains(new Variable("x"), new Variable("y"));
        }

        @Test
        void should_returnVariables_fromPositiveOrdinaryLiterals() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), not(R(y, 2)), a < b");

            Set<Variable> variablesInPositiveOrdinaryLiterals = immutableLiteralsList.getVariablesInPositiveOrdinaryLiterals();

            Set<Variable> expectedVariables = Set.of(new Variable("x"));
            assertThat(variablesInPositiveOrdinaryLiterals).containsExactlyInAnyOrderElementsOf(expectedVariables);
        }

        @Test
        void should_returnVariables_fromNegativeOrdinaryLiterals() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), not(R(y, 2)), a < b");

            Set<Variable> variablesInPositiveOrdinaryLiterals = immutableLiteralsList.getVariablesInNegativeOrdinaryLiterals();

            Set<Variable> expectedVariables = Set.of(new Variable("y"));
            assertThat(variablesInPositiveOrdinaryLiterals).containsExactlyInAnyOrderElementsOf(expectedVariables);
        }

        @Test
        void should_returnVariables_fromFromBuiltInLiterals() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), not(R(y, 2)), a < b");

            Set<Variable> variablesInPositiveOrdinaryLiterals = immutableLiteralsList.getVariablesInBuiltInLiterals();

            Set<Variable> expectedVariables = Set.of(new Variable("a"), new Variable("b"));
            assertThat(variablesInPositiveOrdinaryLiterals).containsExactlyInAnyOrderElementsOf(expectedVariables);
        }

    }

    @Nested
    class UnfoldingTests {
        @Test
        void should_returnLiteralsList_whenUnfoldedLiteralIsBase() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x), R(x)");

            List<ImmutableLiteralsList> actualUnfoldingList = immutableLiteralsList.unfold(0);

            assertThat(actualUnfoldingList)
                    .hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(2)
                    .containsOrdinaryLiteral("P", "x")
                    .containsOrdinaryLiteral("R", "x");

        }

        @Test
        void should_returnLiteralsList_whenUnfoldedLiteralIsBuiltIn() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x), R(x, y), x <= y");

            List<ImmutableLiteralsList> actualUnfoldingList = immutableLiteralsList.unfold(2);

            assertThat(actualUnfoldingList).hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(3)
                    .containsOrdinaryLiteral("P", "x")
                    .containsOrdinaryLiteral("R", "x", "y")
                    .containsComparisonBuiltInLiteral("x", "<=", "y");

        }

        @Test
        void should_returnSameLiteralList_whenLiteralIsDerived_butNegated() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), not(P(a, b))",
                    "P(x, y) :- S(x, y)"
            );

            List<ImmutableLiteralsList> actualUnfoldingList = literalsList.unfold(1);

            assertThat(actualUnfoldingList).hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(2)
                    .containsOrdinaryLiteral(false, "P", "a", "b")
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        void should_returnListWithDefinitionRuleLiterals_whenHasOneDerivationRule_applyingSubstitutionForHeadTerms() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b)",
                    "P(x, y) :- S(x, y)"
            );

            List<ImmutableLiteralsList> actualUnfoldingList = literalsList.unfold(1);

            assertThat(actualUnfoldingList).hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("S", "a", "b");
        }

        @Test
        void should_returnSeveralLists_whenLiteralHasSeveralDefinitionRules() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b)",
                    """
                            P(x, y) :- S(x, y)
                            P(x, y) :- T(x, y)
                            """
            );
            List<ImmutableLiteralsList> unfoldedLiteral = literalsList.unfold(1);

            assertThat(unfoldedLiteral).hasSize(2)
                    .element(0, as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("S", "a", "b");
            ImmutableLiteralsListAssert.assertThat(unfoldedLiteral.get(1))
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("T", "a", "b");
        }

        @Test
        void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b), U(b, z)",
                    "P(x, y) :- S(x, y), T(y, z), V(a, b)"
            );

            List<ImmutableLiteralsList> unfoldedAtom = literalsList.unfold(1);

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(a, b), T(b, z'), V(a',b'), U(b,z)");
            assertThat(unfoldedAtom).hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(5)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("S", "a", "b");
        }

        @Test
        void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_evenWithTermsInDerivationHead() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "P(a, b), Q(z)",
                    "P(x, y) :- S(x,y), R(x, y, a, b, z)"
            );

            List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(0);

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("S(a, b), R(a, b, a', b', z'), Q(z)");
            assertThat(unfoldedLiteralsList).hasSize(1)
                    .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                    .hasSize(3)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("S", "a", "b");
        }

        @Nested
        class Traceability {
            @Test
            void should_rememberOriginalLiterals_whenThereIsOneDerivationRule() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "A(x), Derived(x), C(x)",
                        "Derived(x) :- B1(x), B2(x)");

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(1);

                assertThat(unfoldedLiteralsList)
                        .hasSize(1)
                        .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .literalHasNoOriginal(0)
                        .literalComesFrom(1, literalsList.get(1))
                        .literalComesFrom(2, literalsList.get(1))
                        .literalHasNoOriginal(3);
            }

            @Test
            void should_rememberOriginalLiterals_whenThereAreSeveralDerivationRules() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create("A(x), Derived(x), C(x)",
                        """
                                Derived(x) :- B1(x), B2(x)
                                Derived(x) :- B3(x), B4(x)
                                """);

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(1);

                assertThat(unfoldedLiteralsList)
                        .hasSize(2);

                for (ImmutableLiteralsList unfoldedLiterals : unfoldedLiteralsList) {
                    ImmutableLiteralsListAssert.assertThat(unfoldedLiterals)
                            .literalHasNoOriginal(0)
                            .literalComesFrom(1, literalsList.get(1))
                            .literalComesFrom(2, literalsList.get(1))
                            .literalHasNoOriginal(3);
                }
            }

            @Test
            void should_rememberOriginalLiterals_whenWeApplySubstitutionToAvoidClash() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "A(x), Derived(x), C(y)",
                        "Derived(x) :- B1(x), B2(y)");

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(1);

                assertThat(unfoldedLiteralsList)
                        .hasSize(1)
                        .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .literalHasNoOriginal(0)
                        .literalComesFrom(1, literalsList.get(1))
                        .literalComesFrom(2, literalsList.get(1))
                        .literalHasNoOriginal(3);
            }

            @Test
            void should_rememberOriginalLiteralPositions_whenWeApplyUnfold() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "A(1,1)",
                        "A(x,y) :- B(x,y,1), B(x,x,x)");

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(0);

                // SOLUTION: B(1,1,1), B(1,1,1)
                assertThat(unfoldedLiteralsList)
                        .hasSize(1)
                        .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .hasSize(2)
                        .literalPositionComesFrom(0, 0, literalsList.get(0), 0)
                        .literalPositionComesFrom(0, 1, literalsList.get(0), 1)
                        .literalPositionHasNoOriginal(0, 2)
                        .literalPositionComesFrom(1, 0, literalsList.get(0), 0)
                        .literalPositionComesFrom(1, 1, literalsList.get(0), 0)
                        .literalPositionComesFrom(1, 2, literalsList.get(0), 0);
            }

        }

        @Nested
        class UnfoldingNegatedLiterals {
            @Test
            void should_returnSameLiteralsList_whenNegatedLiteralCannotBeUnfolded() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "P(x), not(Derived())",
                        "Derived() :- A(x)"
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfoldWithNegationExtension(1);

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create(
                        "P(x), not(Derived())",
                        "Derived() :- A(x)"
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1)
                        .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                        .isIsomorphicTo(expectedLiteralsList);
            }

            @Test
            void should_returnOneLiteralsListUnfolded_whenNegatedLiteralCanBeUnfolded_IntoOneRule() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "P(x), not(Derived(x))",
                        """
                                Derived(x) :- A(x)
                                Derived(x) :- B(x)
                                """
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfoldWithNegationExtension(1);

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create(
                        "P(x), not(B(x)), not(A(x))",
                        """
                                Derived(x) :- A(x)
                                Derived(x) :- B(x)
                                """
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1)
                        .first(as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, true, false))
                        .isIsomorphicTo(expectedLiteralsList);
            }

            @Test
            void should_returnSeveralLiteralsListUnfolded_whenNegatedLiteralCanBeUnfolded_IntoSeveralRules() {
                ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                        "P(x), not(Derived(x))",
                        "Derived(x) :- A(x), B(x)"
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfoldWithNegationExtension(1);

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create(
                        "P(x), not(A(x))",
                        "Derived(x) :- A(x), B(x)"
                );
                ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create(
                        "P(x), not(B(x))",
                        "Derived(x) :- A(x), B(x)"
                );

                assertThat(unfoldedLiteralsList).hasSize(2)
                        .element(0, as(LogicInstanceOfAssertFactories.IMMUTABLE_LITERALS_LIST))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, true, false))
                        .isIsomorphicTo(expectedLiteralsList1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(1))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, true, false))
                        .isIsomorphicTo(expectedLiteralsList2);
            }
        }
    }

    @Nested
    class EqualsTest {

        @ParameterizedTest(name = "{0}")
        @MethodSource("equalsLiterals")
        void should_returnTrue_whenLiteralsAreEquals(String description, List<String> stringLiterals) {
            List<Literal> ordinaryLiterals = stringLiterals.stream()
                    .map(stringLiteral -> (Literal) LiteralMother.createOrdinaryLiteral(stringLiteral))
                    .toList();
            ImmutableLiteralsList immutableLiteralsList1 = new ImmutableLiteralsList(ordinaryLiterals);
            ImmutableLiteralsList immutableLiteralsList2 = new ImmutableLiteralsList(ordinaryLiterals);

            boolean equals = immutableLiteralsList1.equals(immutableLiteralsList2);

            assertThat(equals).isTrue();
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("notEqualsLiterals")
        void should_returnFalse_whenLiteralsAreNotEquals(String description, String literalsList1, String literalsList2) {
            ImmutableLiteralsList immutableLiteralsList1 = ImmutableLiteralsListMother.create(literalsList1);
            ImmutableLiteralsList immutableLiteralsList2 = ImmutableLiteralsListMother.create(literalsList2);
            boolean equals = immutableLiteralsList1.equals(immutableLiteralsList2);

            assertThat(equals).as(description).isFalse();
        }

        private static Stream<Arguments> equalsLiterals() {
            return Stream.of(
                    Arguments.of("Literal List with one literal",
                            List.of("P(x, y)")
                    ),
                    Arguments.of("Literal List with several literals",
                            List.of("P(x, y)", "Q(x, y)")
                    )
            );
        }

        private static Stream<Arguments> notEqualsLiterals() {
            return Stream.of(
                    Arguments.of("Literal List with different size",
                            "P(x, y), Q(x, y)",
                            "P(x, y)"
                    ),
                    Arguments.of("Literal List with different literals",
                            "P(x, y), Q(x, y)",
                            "P(x, y), Q(x, z)"
                    ),
                    Arguments.of("Literal List with different order",
                            "P(x, y), Q(x, y)",
                            "Q(x, y), P(x, y)"
                    ),
                    Arguments.of("Literal List with different literals order",
                            "P(x, y), Q(x, y)",
                            "Q(x, y), P(x, z)"
                    )
            );
        }

    }

}