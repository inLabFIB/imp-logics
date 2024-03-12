package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;

/**
 * Specification of a logic constraint with no ID.
 */
public class LogicConstraintWithoutIDSpec extends LogicConstraintSpec {

    public LogicConstraintWithoutIDSpec(List<LiteralSpec> bodyLiterals) {
        super(bodyLiterals);
    }

    public LogicConstraintWithoutIDSpec(BodySpec body) {
        super(body);
    }
}
