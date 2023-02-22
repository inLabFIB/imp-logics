package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.specification.LiteralSpec;
import edu.upc.imp.logics.specification.LogicConstraintSpec;
import org.assertj.core.api.Assertions;

public class LogicConstraintAssert extends NormalClauseAssert<LogicConstraint> {
    public LogicConstraintAssert(LogicConstraint logicConstraint) {
        super(logicConstraint, LogicConstraintAssert.class);
    }

    public static LogicConstraintAssert assertThat(LogicConstraint actual) {
        return new LogicConstraintAssert(actual);
    }

    public LogicConstraintAssert correspondsSpec(LogicConstraintSpec spec) {
        Assertions.assertThat(actual.getID().id()).isEqualTo(spec.getId());

        Assertions.assertThat(actual.getBody()).hasSameSizeAs(spec.getBody());
        for (int i = 0; i < actual.getBody().size(); ++i) {
            Literal actualLit = actual.getBody().get(i);
            LiteralSpec litSpec = spec.getBody().get(i);
            LiteralAssert.assertThat(actualLit).correspondsSpec(litSpec);
        }

        return this;
    }

    public LogicConstraintAssert hasID(String id) {
        Assertions.assertThat(actual.getID().id()).isEqualTo(id);
        return this;
    }

}
