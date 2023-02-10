package edu.upc.imp.logics.schema;

import java.util.List;

/**
 * Implementation of a logic constraint. That is, a NormalClause  without head.
 * E.g. " :- Emp(x), not(Adult(x))"
 * A LogicConstraint is a weak entity w.r.t. a logic schema, that is:
 * - There are no 2 LogicConstriants with the same ConstraintID in the same schema
 * - A LogicConstraint cannot appear in two schemas
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
