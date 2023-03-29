package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class LogicConstraintTest {

    @Test
    public void should_ThrowIllegalArgumentException_WhenCreatingLogicConstraint_WithNullId() {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatThrownBy(() -> new LogicConstraint(null, List.of(literal)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}