package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.AtomMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OrdinaryLiteralTest {

    @Test
    public void should_ThrowException_WhenCreatingOrdinaryLiteral_WithNullAtom() {
        assertThatThrownBy(() -> new OrdinaryLiteral(null, true)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_CreatePositiveOrdinaryLiteral_WhenCreatingOrdinaryLiteral_WithNoSign() {
        OrdinaryLiteral oLiteral = new OrdinaryLiteral(AtomMother.createAtomWithVariableNames("P", List.of("x")));
        assertThat(oLiteral.isPositive()).isTrue();
    }
//
//    @Test
//    public void should_returnNewLiteralWithSubstitutedTerms_WhenApplyingSubstitution(){
//        OrdinaryLiteral oLiteral = new OrdinaryLiteral(AtomMother.createAtomWithVariableNames("P", List.of("x", "y")));
//        Substitution substitution = new SubstitutionBuilder()
//                .addMapping("x", "1")
//                .addMapping("y", "b")
//                .build();
//
//        OrdinaryLiteral newLiteral = oLiteral.applySubstitution(substitution);
//        assertThat(newLiteral).isNotSameAs(oLiteral);
//        LiteralAssert.assertThat(newLiteral)
//                .hasPredicate("P", 2)
//                .hasConstant(0, "1")
//                .hasVariable(1, "b");
//    }
//
//    @Nested
//    class UnfoldingTests {
//        @Test
//        public void should_returnSameLiteral_whenLiteralIsBase(){
//            OrdinaryLiteral ordinaryLiteral = LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x"));
//
//            List<ImmutableLiteralsList> actualUnfoldingList = ordinaryLiteral.unfold();
//
//            assertThat(actualUnfoldingList).hasSize(1);
//            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
//            ImmutableLiteralsList expectedUnfolding = new ImmutableLiteralsList(ordinaryLiteral);
//            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
//                    .isLogicallyEquivalentTo(expectedUnfolding)
//                    .hasSameSizeAs(expectedUnfolding);
//        }
//
//        @Test
//        public void should_returnSameLiteral_whenLiteralIsDerived_butNegated(){
//            LogicSchema schema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), not(S(x))");
//            OrdinaryLiteral negatedOrdinaryLiteral = LiteralMother.createOrdinaryLiteral(schema, false, "P", "a", "b");
//
//            List<ImmutableLiteralsList> actualUnfoldingList = negatedOrdinaryLiteral.unfold();
//
//            assertThat(actualUnfoldingList).hasSize(1);
//            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
//            ImmutableLiteralsList expectedUnfolding = new ImmutableLiteralsList(negatedOrdinaryLiteral);
//            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
//                    .isLogicallyEquivalentTo(expectedUnfolding)
//                    .hasSameSizeAs(expectedUnfolding);
//        }
//
//        @Test
//        public void should_returnDefinitionRuleLiterals_whenHasOneDerivationRule_applyingSubstitutionForHeadTerms(){
//            LogicSchema schema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), not(S(x))");
//            OrdinaryLiteral derivedLiteral = LiteralMother.createOrdinaryLiteral(schema, true, "P", "a", "b");
//
//            List<ImmutableLiteralsList> actualUnfoldingList = derivedLiteral.unfold();
//
//            assertThat(actualUnfoldingList).hasSize(1);
//            ImmutableLiteralsList actualUnfolding = actualUnfoldingList.get(0);
//            ImmutableLiteralsList expectedUnfolding = ImmutableLiteralsListMother.create("R(a, b), not(S(b))");
//            ImmutableLiteralsListAssert.assertThat(actualUnfolding)
//                    .isLogicallyEquivalentTo(expectedUnfolding)
//                    .hasSameSizeAs(expectedUnfolding)
//                    .containsOrdinaryLiteral("R", "a", "b")
//                    .containsOrdinaryLiteral(false, "S", "a");
//        }
//
//        @Test
//        public void should_returnSeveralDefinitionRules_whenLiteralHasSeveralDefinitionRules(){
//
//        }
//
//        @Test
//        public void should_avoidCollisionOfTerms_whenLiteralHasTerms_usedInTheDefinitionRules(){
//
//        }
//    }
}
