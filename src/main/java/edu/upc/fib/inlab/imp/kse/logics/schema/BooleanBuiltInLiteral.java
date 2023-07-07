package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

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

    public static String fromValue(boolean booleanValue) {
        if (booleanValue) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    @Override
    public String getOperationName() {
        return value ? TRUE : FALSE;
    }

    @Override
    public ImmutableTermList getTerms() {
        return new ImmutableTermList();
    }

    public boolean isTrue() {
        return value;
    }

    public boolean isFalse() {
        return !value;
    }

    @Override
    public BooleanBuiltInLiteral applySubstitution(Substitution substitution) {
        return new BooleanBuiltInLiteral(value);
    }

    @Override
    public BooleanBuiltInLiteral buildNegatedLiteral() {
        return new BooleanBuiltInLiteral(!value);
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return fromValue(value) + "()";
    }
}
