package edu.upc.fib.inlab.imp.kse.logics.schema;


import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OrdinaryLiteralTest {

    @Nested
    class CreationTests {
        @Test
        public void should_ThrowException_WhenCreatingOrdinaryLiteral_WithNullAtom() {
            assertThatThrownBy(() -> new OrdinaryLiteral(null, true)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_CreatePositiveOrdinaryLiteral_WhenCreatingOrdinaryLiteral_WithNoSign() {
            OrdinaryLiteral oLiteral = new OrdinaryLiteral(AtomMother.createAtomWithVariableNames("P", List.of("x")));
            assertThat(oLiteral.isPositive()).isTrue();
        }
    }

    @Nested
    class ApplySubstitution {

        @ParameterizedTest(name = "[{index}] Substitution for OrdinaryLiteral with isPositive = {0} case")
        @ValueSource(booleans = {true, false})
        public void should_returnNewLiteralWithSubstitutedTerms_WhenApplyingSubstitution(boolean isPositiveParam) {
            OrdinaryLiteral oLiteral = LiteralMother.createOrdinaryLiteral(isPositiveParam, "P", "x", "y");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "1")
                    .addMapping("y", "b")
                    .build();

            OrdinaryLiteral newLiteral = oLiteral.applySubstitution(substitution);

            assertThat(newLiteral).isNotSameAs(oLiteral);
            LiteralAssert.assertThat(newLiteral)
                    .isNotSameAs(oLiteral)
                    .isPositive(isPositiveParam)
                    .hasPredicate("P", 2)
                    .hasConstant(0, "1")
                    .hasVariable(1, "b");
        }
    }


    @Nested
    class UnfoldingTests {
        @Test
        public void should_returnSameLiteral_whenLiteralIsBase() {
            OrdinaryLiteral ordinaryLiteral = LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x"));

            List<ImmutableLiteralsList> actualUnfoldingList = ordinaryLiteral.unfold();

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            assertThat(actualUnfolding)
                    .hasSize(1)
                    .containsOrdinaryLiteral("P", "x");

        }

        @Test
        public void should_returnSameLiteral_whenLiteralIsDerived_butNegated() {
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
        public void should_returnDefinitionRuleLiterals_whenHasOneDerivationRule_applyingSubstitutionForHeadTerms() {
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
        public void should_returnSeveralDefinitionRules_whenLiteralHasSeveralDefinitionRules() {
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
        public void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
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
    }
}
