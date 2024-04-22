package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Objects;

/**
 * Implementation of the logic comparison built-in literal.
 * <p>
 * E.g. "{@code x < 4}"
 */
public class ComparisonBuiltInLiteral extends BuiltInLiteral {

    private final Term leftTerm;
    private final Term rightTerm;
    private final ComparisonOperator operator;

    /**
     * Constructs a new {@code ComparisonBuiltInLiteral}.
     *
     * @param leftTerm  left term.
     * @param rightTerm right term.
     * @param operator  comparison operator.
     * @throws IllegalArgumentException if left term, right term or comparison operator are {@code null}.
     */
    public ComparisonBuiltInLiteral(Term leftTerm, Term rightTerm, ComparisonOperator operator) {
        if (Objects.isNull(leftTerm)) throw new IllegalArgumentException("Left term cannot be null");
        if (Objects.isNull(rightTerm)) throw new IllegalArgumentException("Right term cannot be null");
        if (Objects.isNull(operator)) throw new IllegalArgumentException("Operator cannot be null");

        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return leftTerm.getName() + " " + operator.getSymbol() + " " + rightTerm.getName();
    }

    @Override
    public ImmutableTermList getTerms() {
        return new ImmutableTermList(leftTerm, rightTerm);
    }

    /**
     * Constructs new {@code ComparisonBuiltInLiteral} after applying the substitution to the literal terms.
     *
     * @param substitution  substitution to apply.
     * @return              a comparison built-in literal after applying the given substitution. The literal will be
     *                      new if some term has changed, otherwise it will be the same.
     */
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
    public ComparisonBuiltInLiteral buildNegatedLiteral() {
        return new ComparisonBuiltInLiteral(this.getLeftTerm(), this.getRightTerm(), operator.getNegatedOperator());
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
    public String getOperationName() {
        return operator.getSymbol();
    }

}
