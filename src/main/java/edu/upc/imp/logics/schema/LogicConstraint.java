package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.visitor.Visitable;
import edu.upc.imp.logics.schema.visitor.Visitor;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic constraint. That is, a NormalClause  without head.
 * E.g. " :- Emp(x), not(Adult(x))"
 * A LogicConstraint is a weak entity w.r.t. a logic schema, that is:
 * - There are no 2 LogicConstraints with the same ConstraintID in the same schema
 * - A LogicConstraint cannot appear in two schemas
 */
public class LogicConstraint extends NormalClause implements Visitable {
    /**
     * Invariants:
     * - constraintID must not be null
     * - constraintID must be immutable
     */
    private final ConstraintID constraintID;

    public LogicConstraint(ConstraintID constraintID, List<Literal> body) {
        super(body);
        if (Objects.isNull(constraintID)) throw new IllegalArgumentException("ConstraintID cannot be null");
        this.constraintID = constraintID;
    }

    public ConstraintID getID() {
        return constraintID;
    }

    @Override
    public <T, R> T accept(Visitor<T, R> visitor, R context) {
        return visitor.visitLogicConstraint(this, context);
    }

    @Override
    public String toString() {
        return "@ " + getID() + " :- " + this.getBody();
    }
}
