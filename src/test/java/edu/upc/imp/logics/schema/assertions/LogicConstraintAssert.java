package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.services.creation.spec.LiteralSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import org.assertj.core.api.Assertions;

public class LogicConstraintAssert extends NormalClauseAssert<LogicConstraint> {
    public LogicConstraintAssert(LogicConstraint logicConstraint) {
        super(logicConstraint, LogicConstraintAssert.class);
    }

    public static LogicConstraintAssert assertThat(LogicConstraint actual) {
        return new LogicConstraintAssert(actual);
    }

    //TODO: Review assertion cases withId or notId
    public LogicConstraintAssert correspondsSpecWithId(LogicConstraintWithIDSpec spec) {
        Assertions.assertThat(actual.getID().id()).isEqualTo(spec.getId());
        return correspondsSpec(spec);
    }

    public LogicConstraintAssert correspondsSpec(LogicConstraintSpec spec) {
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
