package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LevelHierarchyAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LevelHierarchy;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class LogicSchemaTest {

    @Nested
    class Create {
        @Test
        public void should_throwException_WhenCreatingLogicSchema_WithRepeatedPredicateName() {
            Predicate p1 = new MutablePredicate("p", 1);
            Predicate p2 = new MutablePredicate("p", 1);
            assertThatThrownBy(() -> new LogicSchema(Set.of(p1, p2), Set.of()))
                    .isInstanceOf(RepeatedPredicateName.class);
        }

        @Test
        public void should_throwException_WhenCreatingLogicSchema_WithRepeatedConstraintID() {
            Predicate p = new MutablePredicate("p", 1);
            LogicConstraint c1 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID("1"), p);
            LogicConstraint c2 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID("1"), p);
            assertThatThrownBy(() -> new LogicSchema(Set.of(p), Set.of(c1, c2)))
                    .isInstanceOf(RepeatedConstraintID.class);
        }

        @Test
        public void should_throwException_WhenCreatingLogicSchema_WithConstraint_UsingPredicateNotFromSchema() {
            Predicate predicateNotInSchema = new MutablePredicate("p", 1);
            LogicConstraint c1 = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID("1"), predicateNotInSchema);
            assertThatThrownBy(() -> new LogicSchema(Set.of(), Set.of(c1)))
                    .isInstanceOf(PredicateOutsideSchema.class);
        }

        @Test
        public void should_throwException_WhenCreatingLogicSchema_WithDerivedPredicate_UsingPredicateNotFromSchema() {
            Query query = QueryMother.createTrivialQuery(1, "predicateNotInSchemaName");
            Predicate derivedPredicate = new MutablePredicate("p", 1, List.of(query));

            assertThatThrownBy(() -> new LogicSchema(Set.of(derivedPredicate), Set.of()))
                    .isInstanceOf(PredicateOutsideSchema.class);
        }

        @Test
        public void should_notThrowException_WhenCreatingTheLogicSchema_BringingFirstDerivedPredicates_AndThenBasePredicates() {
            Predicate basePredicate = new MutablePredicate("q", 1);
            String derivedPredicateName = "p";
            Predicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate));
            assertThatNoException().isThrownBy(() -> new LogicSchema(Set.of(derivedPredicate, basePredicate), Set.of()));

        }
    }

    @Nested
    class RetrievePredicate {
        @Test
        public void should_retrievePredicate_WhenGivingTheirName() {
            String predicateName = "p";
            Predicate p = new MutablePredicate(predicateName, 1);
            LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of());
            assertThat(logicSchema.getPredicateByName(predicateName)).isSameAs(p);
        }

        @Test
        public void should_throwException_WhenRetrievingNonExistentPredicate() {
            LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
            assertThatThrownBy(() -> logicSchema.getPredicateByName("P"));
        }
    }

    @Nested
    class RetrieveLogicConstraint {
        @Test
        public void should_retrieveLogicConstraint_WhenGivingItsID() {
            Predicate p = new MutablePredicate("p", 1);
            LogicConstraint logicConstraint = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID("1"), p);

            LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of(logicConstraint));
            ConstraintID constraintID = logicConstraint.getID();
            assertThat(logicSchema.getLogicConstraintByID(constraintID)).isSameAs(logicConstraint);
        }

        @Test
        public void should_throwException_WhenRetrievingLogicConstraint_WithNonExistentID() {
            LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
            ConstraintID constraintID = ConstraintIDMother.createConstraintID("1");
            assertThatThrownBy(() -> logicSchema.getLogicConstraintByID(constraintID))
                    .isInstanceOf(LogicConstraintNotExists.class);
        }

        @Test
        public void should_retrieveLogicConstraint_WhenGivingAnEquivalentID() {
            Predicate p = new MutablePredicate("p", 1);
            LogicConstraint logicConstraintExpected = LogicConstraintMother.createTrivialLogicConstraint(ConstraintIDMother.createConstraintID("1"), p);
            LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of(logicConstraintExpected));

            LogicConstraint logicConstraintActual = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraintActual).isSameAs(logicConstraintExpected);
        }
    }

    @Nested
    class RetrieveDerivationRule {
        @Test
        public void should_retrieveDerivationRules_WhenGivingTheirPredicateName() {
            MutablePredicate basePredicate1 = new MutablePredicate("q", 1);
            MutablePredicate basePredicate2 = new MutablePredicate("r", 1);
            String derivedPredicateName = "p";
            MutablePredicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate1, basePredicate2));
            List<DerivationRule> derivationRules = derivedPredicate.getDerivationRules();
            LogicSchema logicSchema = new LogicSchema(Set.of(basePredicate1, basePredicate2, derivedPredicate), Set.of());

            assertThat(logicSchema.getDerivationRulesByPredicateName(derivedPredicateName)).containsExactlyInAnyOrderElementsOf(derivationRules);
        }

        @Test
        public void should_throwException_WhenRetrievingDerivationRules_WithNonExistentPredicateName() {
            LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
            assertThatThrownBy(() -> logicSchema.getDerivationRulesByPredicateName("nonExistentPredicateName"))
                    .isInstanceOf(PredicateNotExists.class);
        }

        @Test
        public void should_throwException_WhenRetrievingDerivationRules_WithNonDerivedPredicateName() {
            String basePredicateName = "p";
            Predicate basePredicate = new MutablePredicate(basePredicateName, 2);
            LogicSchema logicSchema = new LogicSchema(Set.of(basePredicate), Set.of());
            assertThatThrownBy(() -> logicSchema.getDerivationRulesByPredicateName(basePredicateName))
                    .isInstanceOf(PredicateIsNotDerived.class);
        }
    }

    @Nested
    class ComputeLevelHierarchy {

        @Test
        public void should_includeDerivedLiterals_ThatDoesNotUseBasePredicates_inFirstHierarchyLevel() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- TRUE()
                    """);

            LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(levelHierarchy).hasNoPredicateInLevel(0);
            LevelHierarchyAssert.assertThat(levelHierarchy).containsExactlyPredicateNamesInLevel(1, "P");
        }

        @Test
        public void should_makeDerivedLiterals_DefinedOverDerivedLiterals_ThatDoesNotUseBasePredicates_inNextHierarchyLevel() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- TRUE()
                    Q(x) :- P(x)
                    """);

            LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(levelHierarchy).hasNoPredicateInLevel(0);
            LevelHierarchyAssert.assertThat(levelHierarchy).containsExactlyPredicateNamesInLevel(1, "P");
            LevelHierarchyAssert.assertThat(levelHierarchy).containsExactlyPredicateNamesInLevel(2, "Q");
        }


        @Test
        public void should_returnNoLevel_whenThereIsNoPredicate() {
            LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());

            LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(levelHierarchy).hasLevels(0);
        }

        @Test
        public void should_computeLevels_whenThereIsNoDerivationRule() {
            LogicSchema logicSchema = new LogicSchema(Set.of(new Predicate("P", 0),
                    new Predicate("Q", 0)),
                    Set.of());

            LevelHierarchy levelHierarchy = logicSchema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(levelHierarchy).hasLevels(1);
            LevelHierarchyAssert.assertThat(levelHierarchy).containsExactlyPredicateNamesInLevel(0, "P", "Q");
        }

        @Test
        public void should_computeLevels_whenEachPredicateHasOnlyOneDerivationRule() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- R2(x), S1(x)
                    R2(x) :- Q1(x)
                    """);

            LevelHierarchy hierarchy = schema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(hierarchy).hasLevels(3)
                    .containsExactlyPredicateNamesInLevel(0, "S1", "Q1")
                    .containsExactlyPredicateNamesInLevel(1, "R2")
                    .containsExactlyPredicateNamesInLevel(2, "P");
        }

        @Test
        public void should_computeLevels_whenPredicatesHaveSeveralDerivationRules() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- R2(x)
                    P(x) :- Q1(x)
                    R2(x) :- S1(x)
                    """);

            LevelHierarchy hierarchy = schema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(hierarchy).hasLevels(3)
                    .containsExactlyPredicateNamesInLevel(0, "S1", "Q1")
                    .containsExactlyPredicateNamesInLevel(1, "R2")
                    .containsExactlyPredicateNamesInLevel(2, "P");
        }

        @Test
        public void should_computeLevels_whenDerivedRuleDoesNotUsePredicates() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- R2(x), S1(x)
                    R2(x) :- 3 > x
                    """);

            LevelHierarchy hierarchy = schema.computeLevelHierarchy();

            LevelHierarchyAssert.assertThat(hierarchy).hasLevels(3)
                    .containsExactlyPredicateNamesInLevel(0, "S1")
                    .containsExactlyPredicateNamesInLevel(1, "R2")
                    .containsExactlyPredicateNamesInLevel(2, "P");
        }
    }

    @Nested
    class IsEmpty {

        @Test
        public void should_beEmpty_whenLogicSchemaIsEmpty() {
            LogicSchema logicSchema = new LogicSchema(Collections.emptySet(), Collections.emptySet());
            assertThat(logicSchema.isEmpty()).isTrue();
        }

        @Test
        public void should_beNotEmpty_whenLogicSchemaOnlyContainsBasePredicates() {
            Set<Predicate> predicates = Set.of(new Predicate("P", 2));
            LogicSchema logicSchema = new LogicSchema(predicates, Collections.emptySet());
            assertThat(logicSchema.isEmpty()).isFalse();
        }

        @Test
        public void should_beNotEmpty_whenLogicSchemaContainsLogicConstraintWithPredicates() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    @1 :- a < b
                    """);
            assertThat(logicSchema.isEmpty()).isFalse();
        }

        @Test
        public void should_beNotEmpty_whenLogicSchemaContainsDerivationRule() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    P(x) :- a < b
                    """);
            assertThat(logicSchema.isEmpty()).isFalse();
        }
    }

    @Nested
    class IsSafe {

        @Test
        public void should_returnTrue_whenAllNormalClausesAllSafe() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    @1 :- P(x)
                    @2 :- P(x), not(Q(x))
                    P(x) :- Q(x)
                    P(x) :- R(x, y), not(Q(x))
                    """);

            assertThat(logicSchema.isSafe()).isTrue();
        }

        @Test
        public void should_returnFalse_whenAnyNormalClausesIsUnsafe() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                    @1 :- P(x)
                    @2 :- P(x), not(Q(x))
                    P(x) :- Q(x)
                    P(x) :- R(x, y), not(Q(x))
                    P(x) :- x = x
                    """);

            assertThat(logicSchema.isSafe()).isFalse();
        }
    }
}
