package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PredicateSpecTest {

    @Test
    void should_beAbleToCreateAPredicateSpec() {
        PredicateSpec predicateSpec = new PredicateSpec("a", 1);

        assertThat(predicateSpec.name()).isEqualTo("a");
        assertThat(predicateSpec.arity()).isEqualTo(1);
    }

    @Test
    void should_throwException_when_nameIsNull() {
        assertThatThrownBy(() -> new PredicateSpec(null, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_arityIsNegative() {
        assertThatThrownBy(() -> new PredicateSpec("a", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

}