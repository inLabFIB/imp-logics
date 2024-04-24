package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Optional;

/**
 * Implementation of BooleanBuiltInLiteral constants TRUE(), and FALSE()
 */
public class BooleanBuiltInLiteral extends BuiltInLiteral {

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    private final boolean value;

    public BooleanBuiltInLiteral(boolean value) {
        this.value = value;
    }

    public static Optional<Boolean> fromOperator(String operator) {
        Boolean booleanValue = switch (operator) {
            case TRUE -> true;
            case FALSE -> false;
            default -> null;
        };
        return Optional.ofNullable(booleanValue);
    }

    @Override
    public String getOperationName() {
        return value ? TRUE : FALSE;
    }

    @Override
    public ImmutableTermList getTerms() {
        return new ImmutableTermList();
    }

    @Override
    public BooleanBuiltInLiteral applySubstitution(Substitution substitution) {
        return this;
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static String fromValue(boolean booleanValue) {
        if (booleanValue) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    @Override
    public BooleanBuiltInLiteral buildNegatedLiteral() {
        return new BooleanBuiltInLiteral(!value);
    }

    public boolean isTrue() {
        return value;
    }

    @Override
    public String toString() {
        return fromValue(value) + "()";
    }

    public boolean isFalse() {
        return !value;
    }
}
