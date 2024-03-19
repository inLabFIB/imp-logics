package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;

public class ConjunctiveQuerySpec {

    private final List<TermSpec> termSpecList;
    private final BodySpec body;

    public ConjunctiveQuerySpec(List<TermSpec> termSpecList, BodySpec body) {
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
