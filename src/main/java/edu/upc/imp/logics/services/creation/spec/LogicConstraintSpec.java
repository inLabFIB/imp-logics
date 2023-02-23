package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public abstract class LogicConstraintSpec extends NormalClauseSpec implements LogicElementSpec {

    public LogicConstraintSpec(BodySpec body) {
        super(body);
    }

    public LogicConstraintSpec(List<LiteralSpec> bodyLiterals) {
        this(new BodySpec(bodyLiterals));
    }

}
