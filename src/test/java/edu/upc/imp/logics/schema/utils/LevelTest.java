package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class LevelTest {
    @Nested
    class CreationTests {
        @Test
        public void should_throwException_whenCreatingEmptyLevel() {
            assertThatThrownBy(() -> new Level(Set.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenCreatingNullLevel() {
            assertThatThrownBy(() -> new Level(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenCreatingLevelWithNullPredicate() {
            Set<Predicate> predicates = new HashSet<>();
            predicates.add(null);
            assertThatThrownBy(() -> new Level(predicates))
                    .isInstanceOf(IllegalArgumentException.class);
        }


    }

}