package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.schema.MutablePredicate;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalDependencyTest {

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
