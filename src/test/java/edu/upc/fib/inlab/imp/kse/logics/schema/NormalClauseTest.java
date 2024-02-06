package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ConstraintIDMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class NormalClauseTest {

    @Test
    void should_ThrowException_WhenCreatingNormalClause_WithNullBody() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID("1");
        assertThatThrownBy(() -> new LogicConstraint(constraintID, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowException_WhenCreatingNormalClause_WithEmptyBody() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID("1");
        assertThatThrownBy(() -> new LogicConstraint(constraintID, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_MakeBodyImmutable_WhenCreatingNormalClause_WithMutableListInput() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID("1");
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        List<Literal> body = createMutableList(literal);
        LogicConstraint logicConstraint = new LogicConstraint(constraintID, body);

        assertThat(logicConstraint.getBody()).isUnmodifiable();
    }

    private static List<Literal> createMutableList(Literal literal) {
        List<Literal> body = new LinkedList<>();
        body.add(literal);
        return body;
    }
}
