package edu.upc.fib.inlab.imp.kse.logics.schema;


import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OrdinaryLiteralTest {

    @Nested
    class CreationTests {
        @Test
        void should_ThrowException_WhenCreatingOrdinaryLiteral_WithNullAtom() {
            assertThatThrownBy(() -> new OrdinaryLiteral(null, true)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_CreatePositiveOrdinaryLiteral_WhenCreatingOrdinaryLiteral_WithNoSign() {
            OrdinaryLiteral oLiteral = new OrdinaryLiteral(AtomMother.createAtomWithVariableNames("P", List.of("x")));
            assertThat(oLiteral.isPositive()).isTrue();
        }
    }

    @Nested
    class ApplySubstitution {

        @ParameterizedTest(name = "[{index}] Substitution for OrdinaryLiteral with isPositive = {0} case")
        @ValueSource(booleans = {true, false})
        void should_returnNewLiteralWithSubstitutedTerms_WhenApplyingSubstitution(boolean isPositiveParam) {
            OrdinaryLiteral oLiteral = LiteralMother.createOrdinaryLiteral(isPositiveParam, "P", "x", "y");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "1")
                    .addMapping("y", "b")
                    .build();

            OrdinaryLiteral newLiteral = oLiteral.applySubstitution(substitution);

            assertThat(newLiteral).isNotSameAs(oLiteral);
            LiteralAssert.assertThat(newLiteral)
                    .isNotSameAs(oLiteral)
                    .hasPredicate("P", 2)
                    .hasConstant(0, "1")
                    .hasVariable(1, "b")
                    .asOrdinaryLiteral()
                    .isPositive(isPositiveParam);
        }
    }


    @Nested
    class UnfoldingTests {
        @Test
        void should_returnSameLiteral_whenLiteralIsBase() {
            OrdinaryLiteral ordinaryLiteral = LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x"));

            List<ImmutableLiteralsList> actualUnfoldingList = ordinaryLiteral.unfold();

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            assertThat(actualUnfolding)
                    .hasSize(1)
                    .containsOrdinaryLiteral("P", "x");

        }

        @Test
        void should_returnSameLiteral_whenLiteralIsDerived_butNegated() {
            LogicSchema schema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), not(S(x))");
            OrdinaryLiteral negatedOrdinaryLiteral = LiteralMother.createOrdinaryLiteral(schema, false, "P", "a", "b");

            List<ImmutableLiteralsList> actualUnfoldingList = negatedOrdinaryLiteral.unfold();

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            assertThat(actualUnfolding)
                    .hasSize(1)
                    .containsOrdinaryLiteral(false, "P", "a", "b");
        }

        @Test
        void should_returnDefinitionRuleLiterals_whenHasOneDerivationRule_applyingSubstitutionForHeadTerms() {
            LogicSchema schema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, z)");
            OrdinaryLiteral derivedLiteral = LiteralMother.createOrdinaryLiteral(schema, true, "P", "a", "b");

            List<ImmutableLiteralsList> actualUnfoldingList = derivedLiteral.unfold();

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            ImmutableLiteralsList expectedUnfolding = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            assertThat(actualUnfolding)
                    .isLogicallyEquivalentTo(expectedUnfolding)
                    .hasSameSizeAs(expectedUnfolding)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        void should_returnSeveralDefinitionRules_whenLiteralHasSeveralDefinitionRules() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                        P(x, y) :- R(x, y), S(y, z)
                        P(x, y) :- R(x, y), T(y, z)
                    """);
            OrdinaryLiteral ordinaryLiteral = LiteralMother.createOrdinaryLiteral(logicSchema, true, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedLiteral = ordinaryLiteral.unfold();

            assertThat(unfoldedLiteral).hasSize(2);
            ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            assertThat(unfoldedLiteral.get(0))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList1)
                    .containsOrdinaryLiteral("R", "a", "b");
            ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("R(a, b), T(b, z)");
            assertThat(unfoldedLiteral.get(1))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList2)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, a, b)");
            OrdinaryLiteral ordinaryLiteral = LiteralMother.createOrdinaryLiteral(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = ordinaryLiteral.unfold();

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(b, z, w)");
            assertThat(unfoldedAtom).hasSize(1);
            assertThat(unfoldedAtom.get(0))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Nested
        class NegatedUnfoldingTests {
            @Test
            void should_notUnfold_whenDefinitionRuleContainsExistentialVariable() {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived())",
                        "Derived() :- A(x)");

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create(
                        "not(Derived())",
                        "Derived() :- A(x)"
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                        .isIsomorphicTo(expectedLiteralsList);
            }

            @Test
            void should_notUnfold_whenDefinitionRuleContainsNonNegatableLiteral() {
                DerivationRule derivationRule = DerivationRuleMother.create("Derived(x) :- NonNegatableLiteral(x)",
                        "Derived",
                        Set.of("NonNegatableLiteral"));
                OrdinaryLiteral negatedDerivedLiteral = new OrdinaryLiteral(new Atom(derivationRule.getHead().getPredicate(), derivationRule.getHeadTerms()), false);

                List<ImmutableLiteralsList> unfoldedLiteralsList = negatedDerivedLiteral.unfoldWithNegationExtension();

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                        .containsOrdinaryLiteral(false, negatedDerivedLiteral.getPredicateName(), "x");
            }

            @Test
            void should_unfoldNegatedLiteral_whenDefinitionRuleContainsSingleRule_withSingleLiteral() {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived(x))",
                        "Derived(x) :- A(x)"
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create(
                        "not(A(x))",
                        "Derived() :- A(x)"
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                        .isIsomorphicTo(expectedLiteralsList);
            }

            public static Stream<Arguments> literalsAndItsNegation() {
                return Stream.of(
                        Arguments.of("A()", "not(A())"),
                        Arguments.of("not(A())", "A()"),
                        Arguments.of("TRUE()", "FALSE()"),
                        Arguments.of("FALSE()", "TRUE()"),
                        Arguments.of("1 < 2", "1 >= 2"),
                        Arguments.of("1 <= 2", "1 > 2"),
                        Arguments.of("1 = 2", "1 <> 2"),
                        Arguments.of("1 <> 2", "1 = 2"),
                        Arguments.of("1 >= 2", "1 < 2"),
                        Arguments.of("1 > 2", "1 <= 2")
                );
            }

            @ParameterizedTest
            @MethodSource("literalsAndItsNegation")
            void should_removeDerivedNegatedLiteral_whenDefinitionRuleContainsSingleRule_withSingleNegatedLiteral(String literal, String negatedLiteral) {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived())",
                        "Derived() :- " + literal
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create(
                        negatedLiteral,
                        "Derived() :- " + literal
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                        .isIsomorphicTo(expectedLiteralsList);
            }

            @Test
            void should_removeDerivedNegatedLiteral_whenDefinitionRuleContainsSingleRule_withMultipleLiterals() {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived(x))",
                        "Derived(x) :- A(x), B(x)"
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                List<ImmutableLiteralsList> expectedUnfolded = List.of(ImmutableLiteralsListMother.create(
                                "not(A(x))",
                                "Derived(x) :- A(x), B(x)"
                        ),
                        ImmutableLiteralsListMother.create(
                                "not(B(x))",
                                "Derived(x) :- A(x), B(x)"
                        ));

                Assertions.assertThat(unfoldedLiteralsList).hasSize(2);
                expectedUnfolded.forEach(expected ->
                        Assertions.assertThat(unfoldedLiteralsList).anySatisfy(unfolded -> assertThat(unfolded)
                                .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                                .isIsomorphicTo(expected)));
            }


            @Test
            void should_removeDerivedNegatedLiteral_whenThereAreSeveralDefinitionRules_withSingleLiteral() {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived(x))",
                        """
                                    Derived(x) :- A(x)
                                    Derived(x) :- B(x)
                                """
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create(
                        "not(B(x)), not(A(x))",
                        """
                                    Derived(x) :- A(x)
                                    Derived(x) :- B(x)
                                """
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                        .usingIsomorphismOptions(new IsomorphismOptions(false, true, false))
                        .isIsomorphicTo(expectedLiteralsList1);
            }


            @Test
            void should_removeDerivedNegatedLiteral_whenThereAreSeveralDefinitionRules_withSeveralLiterals() {
                OrdinaryLiteral ordinaryLiteral = OrdinaryLiteralMother.createOrdinaryLiteral(
                        "not(Derived())",
                        """
                                Derived() :- A1(), A2(), A3()
                                Derived() :- B1(), B2()
                                    """
                );

                List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfoldWithNegationExtension();

                List<ImmutableLiteralsList> expectedUnfolded = ImmutableLiteralsListMother.createListOfImmutableLiterals(
                        List.of(
                                "not(B1()), not(A1())",
                                "not(B2()), not(A1())",
                                "not(B1()), not(A2())",
                                "not(B2()), not(A2())",
                                "not(B1()), not(A3())",
                                "not(B2()), not(A3())"
                        ),
                        """
                                Derived() :- A1(), A2(), A3()
                                Derived() :- B1(), B2()
                                    """
                );

                Assertions.assertThat(unfoldedLiteralsList).hasSize(6);
                expectedUnfolded.forEach(expected ->
                        Assertions.assertThat(unfoldedLiteralsList).anySatisfy(unfolded -> assertThat(unfolded)
                                .usingIsomorphismOptions(new IsomorphismOptions(false, true, false))
                                .isIsomorphicTo(expected)));
            }

        }
    }

    @Nested
    class BuildNegatedLiteralTest {
        @Test
        void should_ReturnNewNegatedLiteral_WhenLiteralIsPositive() {
            OrdinaryLiteral positiveOL = LiteralMother.createOrdinaryLiteral("P", "a", "b");

            OrdinaryLiteral negatedLiteral = positiveOL.buildNegatedLiteral();

            assertThat(negatedLiteral).isNegated()
                    .hasTerms(positiveOL.getTerms())
                    .hasPredicate(positiveOL.getAtom().getPredicate());
            assertThat(negatedLiteral.getAtom()).isNotSameAs(positiveOL.getAtom());
        }

        @Test
        void should_ReturnNewPositiveLiteral_WhenLiteralIsNegative() {
            OrdinaryLiteral positiveOL = LiteralMother.createOrdinaryLiteral(false, "P", "a", "b");

            OrdinaryLiteral negatedLiteral = positiveOL.buildNegatedLiteral();

            assertThat(negatedLiteral).isPositive()
                    .hasTerms(positiveOL.getTerms())
                    .hasPredicate(positiveOL.getAtom().getPredicate());
            assertThat(negatedLiteral.getAtom()).isNotSameAs(positiveOL.getAtom());
        }
    }
}
