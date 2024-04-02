package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LevelAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Level;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class LevelTest {
    @Nested
    class CreationTests {
        @Test
        void should_createEmptyLevel() {
            Level level = new Level(Set.of());

            LevelAssert.assertThat(level).isEmpty();
        }

        @Test
        void should_createLevel_withPredicates() {
            Level level = new Level(Set.of(
                    new Predicate("P", 0),
                    new Predicate("Q", 1)
            ));

            assertThat(level.getAllPredicates()).isNotEmpty();
        }

        @Test
        void should_throwException_whenCreatingNullLevel() {
            assertThatThrownBy(() -> new Level(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenCreatingLevelWithNullPredicate() {
            Set<Predicate> predicates = new HashSet<>();
            predicates.add(null);
            assertThatThrownBy(() -> new Level(predicates))
                    .isInstanceOf(IllegalArgumentException.class);
        }


    }

}