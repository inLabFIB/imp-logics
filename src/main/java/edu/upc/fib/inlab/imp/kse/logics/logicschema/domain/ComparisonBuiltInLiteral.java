package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Objects;

/**
 * Implementation of the logic ComparisonBuiltInLiteral.
 * {@code E.g. "x < 4".}
 */
public class ComparisonBuiltInLiteral extends BuiltInLiteral {

    /**
     * Invariants:
     * - leftTerm must not be null
     * - rightTerm must not be null
     * - operator must not be null
     */
    private final Term leftTerm;
    private final Term rightTerm;
    private final ComparisonOperator operator;

    public ComparisonBuiltInLiteral(Term leftTerm, Term rightTerm, ComparisonOperator operator) {
        if (Objects.isNull(leftTerm)) throw new IllegalArgumentException("Left term cannot be null");
        if (Objects.isNull(rightTerm)) throw new IllegalArgumentException("Right term cannot be null");
        if (Objects.isNull(operator)) throw new IllegalArgumentException("Operator cannot be null");

        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
        this.operator = operator;
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    @Override
    public ImmutableTermList getTerms() {
        return new ImmutableTermList(leftTerm, rightTerm);
    }

    @Override
    public ComparisonBuiltInLiteral applySubstitution(Substitution substitution) {
        if (substitution.replacesSomeVariableOf(this.getUsedVariables())) {
            return new ComparisonBuiltInLiteral(leftTerm.applySubstitution(substitution),
                    rightTerm.applySubstitution(substitution),
                    operator
            );
        } else return this;
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getOperationName() {
        return operator.getSymbol();
    }

    @Override
    public String toString() {
        return leftTerm.getName() + " " + operator.getSymbol() + " " + rightTerm.getName();
    }

    @Override
    public ComparisonBuiltInLiteral buildNegatedLiteral() {
        return new ComparisonBuiltInLiteral(this.getLeftTerm(), this.getRightTerm(), operator.getNegatedOperator());
    }
}
