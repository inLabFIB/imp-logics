package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomBuiltInLiteral extends BuiltInLiteral {
    /**
     * Invariants:
     * - operationName cannot be null neither empty,
     * - terms cannot contain nulls, but might be empty.
     */
    private final String operationName;
    private final ImmutableTermList terms;

    public CustomBuiltInLiteral(String operationName, List<Term> terms) {
        if (Objects.isNull(operationName)) throw new IllegalArgumentException("OperationName cannot be null");
        if (operationName.isEmpty()) throw new IllegalArgumentException("OperationName cannot be empty");
        this.operationName = operationName;
        this.terms = new ImmutableTermList(terms);
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    @Override
    public ImmutableTermList getTerms() {
        return terms;
    }

    @Override
    public CustomBuiltInLiteral applySubstitution(Substitution substitution) {
        return new CustomBuiltInLiteral(this.operationName, terms.applySubstitution(substitution));
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String toString() {
        String termsAsString = terms.stream().map(Term::getName).collect(Collectors.joining(", "));
        return this.operationName + "(" + termsAsString + ")";
    }

}
