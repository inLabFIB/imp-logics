package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.utils.ImmutableLiteralsListMother;
import edu.upc.imp.logics.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ImmutableLiteralsListTest {

    @Nested
    class CreateImmutableLiteralsList {
        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithElementNull() {
            List<Literal> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableLiteralsList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithNull() {
            assertThatThrownBy(() -> new ImmutableLiteralsList((List<Literal>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_createImmutableLiteralsEmptyList() {
            assertThat(new ImmutableLiteralsList()).isEmpty();
        }

        @Test
        public void should_createEmptyLiteralList_whenCreatingImmutableLiteralList_withEmptyList() {
            ImmutableLiteralsList actualTermList = new ImmutableLiteralsList(List.of());
            ImmutableLiteralsListAssert.assertThat(actualTermList).isEmpty();
        }
    }

    @Test
    public void should_ReturnNewLiteralsList_WithReplacedTerms_WhenApplyingSubstitution() {
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

    @Test
    public void should_ReturnUsedVariables() {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), R(y, 2), x < y");

        Set<Variable> usedVariables = immutableLiteralsList.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }

    @Nested
    class UnfoldingTests {
        @Test
        public void should_returnLiteralsList_whenUnfoldedLiteralIsBase() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x), R(x)");

            List<ImmutableLiteralsList> actualUnfoldingList = immutableLiteralsList.unfold(0);

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
                    .hasSize(2)
                    .containsOrdinaryLiteral("P", "x")
                    .containsOrdinaryLiteral("R", "x");

        }

        @Test
        public void should_returnLiteralsList_whenUnfoldedLiteralIsBuiltIn() {
            ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x), R(x, y), x <= y");

            List<ImmutableLiteralsList> actualUnfoldingList = immutableLiteralsList.unfold(2);

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
                    .hasSize(3)
                    .containsOrdinaryLiteral("P", "x")
                    .containsOrdinaryLiteral("R", "x", "y")
                    .containsComparisonBuiltInLiteral("x", "<=", "y");

        }

        @Test
        public void should_returnSameLiteralList_whenLiteralIsDerived_butNegated() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), not(P(a, b))",
                    "P(x, y) :- S(x, y)"
            );

            List<ImmutableLiteralsList> actualUnfoldingList = literalsList.unfold(1);

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
                    .hasSize(2)
                    .containsOrdinaryLiteral(false, "P", "a", "b")
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        public void should_returnListWithDefinitionRuleLiterals_whenHasOneDerivationRule_applyingSubstitutionForHeadTerms() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b)",
                    "P(x, y) :- S(x, y)"
            );

            List<ImmutableLiteralsList> actualUnfoldingList = literalsList.unfold(1);

            assertThat(actualUnfoldingList).hasSize(1);
            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("S", "a", "b");
        }

        @Test
        public void should_returnSeveralLists_whenLiteralHasSeveralDefinitionRules() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b)",
                    """
                            P(x, y) :- S(x, y)
                            P(x, y) :- T(x, y)
                            """
            );
            List<ImmutableLiteralsList> unfoldedLiteral = literalsList.unfold(1);

            assertThat(unfoldedLiteral).hasSize(2);
            ImmutableLiteralsListAssert.assertThat(unfoldedLiteral.get(0))
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("S", "a", "b");
            ImmutableLiteralsListAssert.assertThat(unfoldedLiteral.get(1))
                    .hasSize(2)
                    .containsOrdinaryLiteral("R", "a", "b")
                    .containsOrdinaryLiteral("T", "a", "b");
        }

        @Test
        public void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "R(a, b), P(a, b), U(b, z)",
                    "P(x, y) :- S(x, y), T(y, z), V(a, b)"
            );

            List<ImmutableLiteralsList> unfoldedAtom = literalsList.unfold(1);

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(a, b), T(b, z'), V(a',b'), U(b,z)");
            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(5)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("S", "a", "b");
        }

        @Test
        public void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_evenWithTermsInDerivationHead() {
            ImmutableLiteralsList literalsList = ImmutableLiteralsListMother.create(
                    "P(a, b), Q(z)",
                    "P(x, y) :- S(x,y), R(x, y, a, b, z)"
            );

            List<ImmutableLiteralsList> unfoldedLiteralsList = literalsList.unfold(0);

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("S(a, b), R(a, b, a', b', z'), Q(z)");
            assertThat(unfoldedLiteralsList).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedLiteralsList.get(0))
                    .hasSize(3)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("S", "a", "b");
        }
    }
}