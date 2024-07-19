package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.Objects;

/**
 * Specification of a term. E.g., specification of a constant, or a term.
 */
public abstract class TermSpec implements LogicElementSpec {
    private final String name;

    protected TermSpec(String name) {
        if (Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermSpec termSpec)) return false;

        return Objects.equals(name, termSpec.name);
    }
}
