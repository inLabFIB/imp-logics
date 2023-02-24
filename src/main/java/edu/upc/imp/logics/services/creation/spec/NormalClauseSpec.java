package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

/**
 * Specification of a NormalClause. E.g. specification of a derivation rule, or logic constraint.
 */
public abstract class NormalClauseSpec {
    private final BodySpec body;

    public NormalClauseSpec(BodySpec bodySpec) {
        body = bodySpec;
    }

    public List<LiteralSpec> getBody() {
        return body.getLiterals();
    }
}
