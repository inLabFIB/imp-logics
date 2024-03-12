package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogicConstraintWithIDSpecTest {

    @Test
    void should_beAbleToCreateALogicConstraintWithIDSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        LogicConstraintWithIDSpec logicConstraintWithIDSpec = new LogicConstraintWithIDSpec("111", bodySpec) {
        };

        assertThat(logicConstraintWithIDSpec.getId()).isEqualTo("111");
        assertThat(logicConstraintWithIDSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    void should_throwException_when_idIsNull() {
        String constraintId = null;
        BodySpec body = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec(constraintId, body) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec("111", (BodySpec) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_listSpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec("111", (List<LiteralSpec>) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

}