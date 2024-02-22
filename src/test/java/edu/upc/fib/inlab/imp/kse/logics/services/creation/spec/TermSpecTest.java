package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TermSpecTest {

    @Test
    void should_beAbleToCreateATermSpec() {
        TermSpec termSpec = new TermSpec("a") {
        };
        assertThat(termSpec.getName()).isEqualTo("a");
    }

    @Test
    void should_throwException_when_nameIsNull() {
        assertThatThrownBy(() -> new TermSpec(null) {
        }).isInstanceOf(IllegalArgumentException.class);
    }

}