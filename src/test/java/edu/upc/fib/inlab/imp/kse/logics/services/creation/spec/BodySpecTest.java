package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BodySpecTest {

    @Test
    void should_beAbleToCreateABodySpec() {
        OrdinaryLiteralSpec ordinaryLiteralSpec = new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true);
        BodySpec bodySpec = new BodySpec(List.of(ordinaryLiteralSpec));

        assertThat(bodySpec.literals()).containsExactly(ordinaryLiteralSpec);
    }

    @Test
    void should_throwException_when_literalsListIsNull() {
        assertThatThrownBy(() -> new BodySpec(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_literalsListIsEmpty() {
        List<LiteralSpec> body = List.of();
        assertThatThrownBy(() -> new BodySpec(body))
                .isInstanceOf(IllegalArgumentException.class);
    }

}