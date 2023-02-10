package edu.upc.imp.logics.schema;

import java.util.Objects;

/**
 * Implementation of the logic BinaryBuiltInLiteral.
 * E.g. "x < 4".
 */
public class BinaryBuiltInLiteral extends BuiltInLiteral {

    /**
     * Invariants:
     * - leftTerm must not be null
     * - rightTerm must not be null
     * - operation must not be null
     */
    private final Term leftTerm;
    private final Term rightTerm;
    private final BinaryOperation operation;

    public BinaryBuiltInLiteral(Term leftTerm, Term rightTerm, BinaryOperation operation) {
        if (Objects.isNull(leftTerm)) throw new IllegalArgumentException("Left term cannot be null");
        if (Objects.isNull(rightTerm)) throw new IllegalArgumentException("Right term cannot be null");
        if (Objects.isNull(operation)) throw new IllegalArgumentException("Operation cannot be null");

        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
        this.operation = operation;
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    public BinaryOperation getOperation() {
        return operation;
    }
}
