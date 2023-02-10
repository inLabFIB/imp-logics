package edu.upc.imp.logics.schema;

import java.util.List;

/**
 * Implementation of a logic constraint. That is, a NormalClause  without head.
 * E.g. " :- Emp(x), not(Adult(x))"
 *
 */
public class LogicConstraint extends NormalClause {
    private final ConstraintID constraintID;

    public LogicConstraint(ConstraintID constraintID, List<Literal> body) {
        super(body);
        this.constraintID = constraintID;
    }

    public ConstraintID getID() {
        return constraintID;
    }
}
