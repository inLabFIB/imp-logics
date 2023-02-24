package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

/**
 * Specification of a body of a normal clause.
 */
public class BodySpec implements LogicElementSpec {

    private final List<LiteralSpec> literals;

    public BodySpec(List<LiteralSpec> literals) {
        this.literals = literals;
    }

    public List<LiteralSpec> getLiterals() {
        return literals;
    }
}
