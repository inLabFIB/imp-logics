package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LevelAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Level;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LevelHierarchy;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.LevelHierarchyException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateNotInLevel;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.DerivedPredicateMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LevelHierarchyMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LevelHierarchyTest {
    @Nested
    class CreationTests {
        @Test
        void should_ThrowException_WhenLevelsIsNull() {
            assertThatThrownBy(() -> new LevelHierarchy(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_notThrowException_WhenLevelsIsEmpty() {
            assertThatCode(() -> new LevelHierarchy(List.of()))
                    .doesNotThrowAnyException();
        }

        @Test
        void should_ThrowException_WhenLevelsContainsNulls() {
            List<Level> levels = new LinkedList<>();
            levels.add(null);
            assertThatThrownBy(() -> new LevelHierarchy(levels))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_ThrowException_WhenBasePredicate_IsNotIn0Level() {
            Predicate predicateP = new Predicate("P", 0);
            Predicate predicateQ = new Predicate("Q", 0);
            Level level0 = new Level(Set.of(predicateP));
            Level level1 = new Level(Set.of(predicateQ));

            assertThatThrownBy(() -> new LevelHierarchy(List.of(level0, level1)))
                    .isInstanceOf(LevelHierarchyException.class);
        }

        @Test
        void should_ThrowException_WhenDerivedLiteral_UsesPredicate_NotLowerInTheHierarchy() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs("P(x) :- Q(x)");
            Predicate predicateP = schema.getPredicateByName("P");
            Predicate predicateQ = schema.getPredicateByName("Q");
            Level level0 = new Level(Set.of(predicateP, predicateQ));

            assertThatThrownBy(() -> new LevelHierarchy(List.of(level0)))
                    .isInstanceOf(LevelHierarchyException.class);
        }

    }

    @Test
    void should_returnTheNumberOfLevels() {
        LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(List.of("P"), List.of("Q"));
        assertThat(levelHierarchy.getNumberOfLevels()).isEqualTo(2);
    }

    @Test
    void should_returnLevel() {
        LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(List.of("P"), List.of("Q"));

        Level level = levelHierarchy.getLevel(0);

        LevelAssert.assertThat(level).containsExactlyPredicateNames("P");
    }

    @Nested
    class ReturnLevelIndexTests {
        @Test
        void should_throwException_whenPredicateIsNull() {
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(List.of("P"));

            assertThatThrownBy(() -> levelHierarchy.getLevelIndexOfPredicate(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_returnLevelIndexOfPredicate() {
            Predicate predicateP = new Predicate("P", 0);
            Set<Predicate> level0 = Set.of(predicateP);
            Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);

            int index = levelHierarchy.getLevelIndexOfPredicate(predicateP);

            assertThat(index).isZero();
        }

        @Test
        void should_throwException_whenTryingToReturnLevelIndex_OfNotContainedPredicate() {
            Predicate predicateP = new Predicate("P", 0);
            Set<Predicate> level0 = Set.of(predicateP);
            Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);


            Predicate inexistentPredicate = new Predicate("R", 0);
            assertThatThrownBy(() -> levelHierarchy.getLevelIndexOfPredicate(inexistentPredicate))
                    .isInstanceOf(PredicateNotInLevel.class);

        }
    }

    @Nested
    class ReturnLevelTests {

        @Test
        void should_throwException_whenPredicateIsNull() {
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(List.of("P"));

            assertThatThrownBy(() -> levelHierarchy.getLevelOfPredicate(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_returnLevelOfPredicate() {
            Predicate predicateP = new Predicate("P", 0);
            Set<Predicate> level0 = Set.of(predicateP);
            Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);

            Level level = levelHierarchy.getLevelOfPredicate(predicateP);

            LevelAssert.assertThat(level).containsExactlyPredicateNames("P");
        }

        @Test
        void should_throwException_whenTryingToReturnLevel_OfNotContainedPredicate() {
            Predicate predicateP = new Predicate("P", 0);
            Set<Predicate> level0 = Set.of(predicateP);
            Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
            LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);

            Predicate inexistentPredicate = new Predicate("R", 0);
            assertThatThrownBy(() -> levelHierarchy.getLevelOfPredicate(inexistentPredicate))
                    .isInstanceOf(PredicateNotInLevel.class);

        }
    }

    @Test
    void should_returnBaseLevel() {
        Predicate predicateP = new Predicate("P", 0);
        Set<Predicate> level0 = Set.of(predicateP);
        Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
        LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);

        Level level = levelHierarchy.getBasePredicatesLevel();

        LevelAssert.assertThat(level).containsExactlyPredicateNames("P");
    }

    @Test
    void should_returnIterableDerivedLevels() {
        Predicate predicateP = new Predicate("P", 0);
        Set<Predicate> level0 = Set.of(predicateP);
        Set<Predicate> level1 = Set.of(DerivedPredicateMother.createOArityDerivedPredicate("Q", predicateP));
        LevelHierarchy levelHierarchy = LevelHierarchyMother.createLevelHierarchy(level0, level1);

        List<Level> derivedLevels = levelHierarchy.getDerivedLevels();

        assertThat(derivedLevels).hasSize(1);
        Level derivedLevel = derivedLevels.get(0);
        LevelAssert.assertThat(derivedLevel).containsExactlyPredicateNames("Q");
    }
}