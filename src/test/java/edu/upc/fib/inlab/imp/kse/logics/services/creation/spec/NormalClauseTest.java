package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NormalClauseTest {

    @Test
    void should_beAbleToCreateANormalClauseSpec() {
        OrdinaryLiteralSpec ordinaryLiteralSpec = new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true);
        BodySpec bodySpec = new BodySpec(List.of(ordinaryLiteralSpec));
        NormalClauseSpec normalClauseSpec = new NormalClauseSpec(bodySpec) {
        };

        assertThat(normalClauseSpec.getBody()).containsExactly(ordinaryLiteralSpec);
    }

    @Test
    void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new NormalClauseSpec(null) {
        }).isInstanceOf(IllegalArgumentException.class);
    }

}