package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of a logic constraint. That is, a NormalClause  without head.
 * E.g. " :- Emp(x), not(Adult(x))"
 * A LogicConstraint is a weak entity w.r.t. a logic schema, that is:
 * - There are no 2 LogicConstraints with the same ConstraintID in the same schema
 * - A LogicConstraint cannot appear in two schemas
 */
public class LogicConstraint extends NormalClause {
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
    public String toString() {
        return "@" + getID() + " :- " + this.getBody();
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isSafe() {
        Set<Variable> variablesInPositiveOrdinaryLiterals = getBody().getVariablesInPositiveOrdinaryLiterals();

        Set<Variable> variablesInNegativeOrdinaryLiterals = getBody().getVariablesInNegativeOrdinaryLiterals();
        Set<Variable> variablesInBuiltInLiterals = getBody().getVariablesInBuiltInLiterals();

        Set<Variable> variablesInNegativeLiteralsOrBuiltInLiterals = new LinkedHashSet<>();
        variablesInNegativeLiteralsOrBuiltInLiterals.addAll(variablesInNegativeOrdinaryLiterals);
        variablesInNegativeLiteralsOrBuiltInLiterals.addAll(variablesInBuiltInLiterals);
        return variablesInPositiveOrdinaryLiterals.containsAll(variablesInNegativeLiteralsOrBuiltInLiterals);
    }

}
