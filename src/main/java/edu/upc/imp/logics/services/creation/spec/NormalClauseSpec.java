package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public abstract class NormalClauseSpec {
    private final BodySpec body;

    public NormalClauseSpec(BodySpec bodySpec) {
        body = bodySpec;
    }

    public List<LiteralSpec> getBody() {
        return body.getLiterals();
    }
}
