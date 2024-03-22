package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;

/**
 * Specification of a conjunctive query.
 */
public class QuerySpec implements LogicElementSpec {

    private final List<TermSpec> termSpecList;
    private final BodySpec body;

    public QuerySpec(List<TermSpec> termSpecList, BodySpec body) {
        if (termSpecList == null) throw new IllegalArgumentException("Term spec list cannot be null");
        if (body == null) throw new IllegalArgumentException("Body cannot be null");
        this.termSpecList = termSpecList;
        this.body = body;
    }

    public List<TermSpec> getTermSpecList() {
        return termSpecList;
    }

    public List<LiteralSpec> getBodySpec() {
        return body.literals();
    }

}
