package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class LogicConstraintTest {

    @Test
    public void should_ThrowIllegalArgumentException_WhenCreatingLogicConstraint_WithNullId() {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatThrownBy(() -> new LogicConstraint(null, List.of(literal)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}