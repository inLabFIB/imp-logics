package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicConstraintWithIDSpecTest {

    @Test
    public void should_beAbleToCreateALogicConstraintWithIDSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        LogicConstraintWithIDSpec logicConstraintWithIDSpec = new LogicConstraintWithIDSpec("111", bodySpec) {
        };

        assertThat(logicConstraintWithIDSpec.getId()).isEqualTo("111");
        assertThat(logicConstraintWithIDSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    public void should_throwException_when_idIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec(null, new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)))) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec("111", (BodySpec) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_listSpecIsNull() {
        assertThatThrownBy(() -> new LogicConstraintWithIDSpec("111", (List<LiteralSpec>) null) {
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

}