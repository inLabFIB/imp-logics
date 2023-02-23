package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public class LogicConstraintWithoutIDSpec extends LogicConstraintSpec {

    public LogicConstraintWithoutIDSpec(List<LiteralSpec> bodyLiterals) {
        super(bodyLiterals);
    }

    public LogicConstraintWithoutIDSpec(BodySpec body) {
        super(body);
    }
}
