package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogicConstraintSpecTest {

    @Test
    void should_beAbleToCreateALogicConstraintSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpec(bodySpec) {
        };

        assertThat(logicConstraintSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintSpec((BodySpec) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_listSpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintSpec((List<LiteralSpec>) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

}