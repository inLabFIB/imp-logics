package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BodySpecTest {

    @Test
    public void should_beAbleToCreateABodySpec() {
        OrdinaryLiteralSpec ordinaryLiteralSpec = new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true);
        BodySpec bodySpec = new BodySpec(List.of(ordinaryLiteralSpec));

        assertThat(bodySpec.literals()).containsExactly(ordinaryLiteralSpec);
    }

    @Test
    public void should_throwException_when_literalsListIsNull() {
        assertThatThrownBy(() -> new BodySpec(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_literalsListIsEmpty() {
        assertThatThrownBy(() -> new BodySpec(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}