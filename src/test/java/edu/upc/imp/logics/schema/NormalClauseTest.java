package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.ConstraintIDMother;
import edu.upc.imp.logics.schema.utils.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class NormalClauseTest {

    @Test
    public void should_ThrowException_WhenCreatingNormalClause_WithNullBody() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID(1);
        assertThatThrownBy(() -> new LogicConstraint(constraintID, null));
    }

    @Test
    public void should_ThrowException_WhenCreatingNormalClause_WithEmptyBody() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID(1);
        assertThatThrownBy(() -> new LogicConstraint(constraintID, List.of()));
    }

    @Test
    public void should_MakeBodyImmutable_WhenCreatingNormalClause() {
        ConstraintID constraintID = ConstraintIDMother.createConstraintID(1);
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        List<Literal> body = new LinkedList<>();
        body.add(literal);
        LogicConstraint logicConstraint = new LogicConstraint(constraintID, body);

        assertThat(logicConstraint.getBody()).isUnmodifiable();
    }
}
