package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;
import java.util.Set;

/**
 * Specification of a LogicConstraint.
 */
public abstract class LogicConstraintSpec extends NormalClauseSpec implements LogicElementSpec {

    protected LogicConstraintSpec(List<LiteralSpec> bodyLiterals) {
        this(new BodySpec(bodyLiterals));
    }

    protected LogicConstraintSpec(BodySpec body) {
        super(body);
    }

    @Override
    public Set<String> getAllVariableNames() {
        return new BodySpec(getBody()).getAllVariableNames();
    }
}
