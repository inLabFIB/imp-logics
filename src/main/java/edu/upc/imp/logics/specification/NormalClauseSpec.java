package edu.upc.imp.logics.specification;

import java.util.List;

public class NormalClauseSpec {
    private final List<LiteralSpec> body;

    public NormalClauseSpec(List<LiteralSpec> body) {
        this.body = body;
    }

    public List<LiteralSpec> getBody() {
        return body;
    }
}
