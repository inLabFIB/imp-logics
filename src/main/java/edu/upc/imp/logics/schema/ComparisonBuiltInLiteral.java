package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.visitor.Visitor;

import java.util.Objects;

/**
 * Implementation of the logic ComparisonBuiltInLiteral.
 * E.g. "x < 4".
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
    public <T, R> T accept(Visitor<T, R> visitor, R context) {
        return visitor.visitBuiltInLiteral(this, context);
    }

    @Override
    public String getOperationName() {
        return operator.getSymbol();
    }
}
