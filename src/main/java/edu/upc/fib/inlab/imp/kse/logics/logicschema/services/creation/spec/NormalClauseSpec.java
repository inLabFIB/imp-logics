package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;
import java.util.Objects;

/**
 * Specification of a NormalClause. E.g. specification of a derivation rule, or logic constraint.
 */
public abstract class NormalClauseSpec {
    private final BodySpec body;

    protected NormalClauseSpec(BodySpec bodySpec) {
        if (Objects.isNull(bodySpec)) throw new IllegalArgumentException("Body cannot be null");
        body = bodySpec;
    }

    public List<LiteralSpec> getBody() {
        return body.literals();
    }
}
