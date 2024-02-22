package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.schema.MutablePredicate;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionalDependencyTest {

    @Nested
    class CreationTests {
        @Test
        void shuldThrowException_whenCreatingFD_withDeterminedAttributesOutOfRange() {
            Predicate pred = new MutablePredicate("P", 2);
            Set<Integer> keyPositions = Set.of(0);
            Set<Integer> determinedPositions = Set.of(1, 2, 3);
            assertThatThrownBy(() -> new FunctionalDependency(pred, keyPositions, determinedPositions)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shuldThrowException_whenCreatingFD_withKeyAttributesOutOfRange() {
            Predicate pred = new MutablePredicate("P", 2);
            Set<Integer> keyPositions = Set.of(1, 2, 3);
            Set<Integer> determinedPositions = Set.of(0);
            assertThatThrownBy(() -> new FunctionalDependency(pred, keyPositions, determinedPositions)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shuldThrowException_whenCreatingFD_withKeyAttributesAndDeterminedAttributes_NotDisjoint() {
            Predicate pred = new MutablePredicate("P", 2);
            Set<Integer> keyPositions = Set.of(0, 1);
            Set<Integer> determinedPositions = Set.of(1);
            assertThatThrownBy(() -> new FunctionalDependency(pred, keyPositions, determinedPositions)).isInstanceOf(IllegalArgumentException.class);
        }

    }



    @Test
    void shouldIdentifyAsKeyDependency_whenKeysAndDeterminedCoversWholeArity() {
        FunctionalDependency dependency = new FunctionalDependency(new MutablePredicate("P", 3), Set.of(0), Set.of(1, 2));

        boolean isKey = dependency.isKeyDependency();

        assertThat(isKey).isTrue();
    }

    @Test
    void shouldNotIdentifyAsKeyDependency_whenKeysAndDeterminedDoNotCoverWholeArity() {
        FunctionalDependency dependency = new FunctionalDependency(new MutablePredicate("P", 4), Set.of(0), Set.of(1, 2));

        boolean isKey = dependency.isKeyDependency();

        assertThat(isKey).isFalse();
    }
}
