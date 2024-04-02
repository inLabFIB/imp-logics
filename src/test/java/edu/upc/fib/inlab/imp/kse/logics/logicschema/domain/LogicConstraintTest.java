package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicConstraintMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LogicConstraintTest {

    @Test
    void should_ThrowIllegalArgumentException_WhenCreatingLogicConstraint_WithNullId() {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        ConstraintID constraintID = null;
        List<Literal> body = List.of(literal);
        assertThatThrownBy(() -> new LogicConstraint(constraintID, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    class IsSafe {
        @ParameterizedTest
        @ValueSource(strings = {
                ":- P(x)",
                ":- P(x), not(Q(x))",
                ":- P(x), x=1",
                ":- P(x), not(Q(x, 1))",
                ":- not(Q(x, 1)), P(x)",
                ":- P()",
        })
        void should_ReturnTrue_WhenCallingIsSafe_WithSafeLogicConstraint(String logicConstraintString) {
            LogicConstraint logicConstraint = LogicConstraintMother.createWithoutID(logicConstraintString);
            assertThat(logicConstraint.isSafe()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                ":- P(x), not(Q(x, y))",
                ":- P(x), x=y",
                ":- x=y",
                ":- not(P(x))",
        })
        void should_ReturnFalse_WhenCallingIsSafe_WithUnsafeLogicConstraint(String logicConstraintString) {
            LogicConstraint logicConstraint = LogicConstraintMother.createWithoutID(logicConstraintString);
            assertThat(logicConstraint.isSafe()).isFalse();

        }

    }

}