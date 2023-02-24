package edu.upc.imp.logics.services.creation.spec;

/**
 * Specification of a term. E.g., specification of a constant, or a term.
 */
public abstract class TermSpec implements LogicElementSpec {
    private final String name;

    public TermSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
