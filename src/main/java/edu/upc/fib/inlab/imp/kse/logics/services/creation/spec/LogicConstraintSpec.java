package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import java.util.List;

/**
 * Specification of a LogicConstraint.
 */
public abstract class LogicConstraintSpec extends NormalClauseSpec implements LogicElementSpec {

    protected LogicConstraintSpec(BodySpec body) {
        super(body);
    }

    protected LogicConstraintSpec(List<LiteralSpec> bodyLiterals) {
        this(new BodySpec(bodyLiterals));
    }

}
