package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConjunctiveQuerySetSpecTest {

    @Test

    void should_throwException_when_setIsNull() {
        assertThatThrownBy(() -> new ConjunctiveQuerySetSpec(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_setIsEmpty() {
        Set<ConjunctiveQuerySpec> emptySet = Collections.emptySet();
        assertThatThrownBy(() -> new ConjunctiveQuerySetSpec(emptySet))
                .isInstanceOf(IllegalArgumentException.class);
    }

}