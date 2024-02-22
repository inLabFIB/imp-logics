package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.Objects;

/**
 * Implementation of a logic Term. A Term is either a Variable or a Constant.
 * E.g.: variable "x", constant "1"
 * The distinction between Variables and Users is done through the subclass
 * This class is a ValueObject. Hence, the same Term can be safely reused
 * among several Atoms, or Built-in literals.
 */
public abstract class Term {
    /**
     * Invariants:
     * - name is not null
     * - name is not empty
     */
    private final String name;

    protected Term(String name) {
        if (Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return name.equals(term.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public abstract Term applySubstitution(Substitution substitution);

    public boolean isVariable() {
        return false;
    }

    public boolean isConstant() {
        return false;
    }

    public abstract <T> T accept(LogicSchemaVisitor<T> visitor);
}
