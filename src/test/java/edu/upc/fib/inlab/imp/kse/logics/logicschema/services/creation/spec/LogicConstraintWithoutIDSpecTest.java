package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogicConstraintWithoutIDSpecTest {

    @Test
    void should_beAbleToCreateALogicConstraintWithoutIDSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        LogicConstraintWithoutIDSpec logicConstraintWithoutIDSpec = new LogicConstraintWithoutIDSpec(bodySpec) {
        };

        assertThat(logicConstraintWithoutIDSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithoutIDSpec((BodySpec) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_listSpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithoutIDSpec((List<LiteralSpec>) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }
}