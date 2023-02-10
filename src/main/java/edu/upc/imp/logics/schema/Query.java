package edu.upc.imp.logics.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Query {
    /**
     * Invariants:
     * - headTerms cannot be null, although might be empty
     * - headTerms must be inmutable
     * - body cannot be null
     * - body cannot be empty
     * - body is inmutable
     */

    private final List<Term> headTerms;
    private final List<Literal> body;

    public Query(List<Term> headTerms, List<Literal> body) {
        if(Objects.isNull(headTerms)) throw new IllegalArgumentException("Head terms cannot be null");
        if(Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if(body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
        this.headTerms = Collections.unmodifiableList(headTerms);
        this.body = Collections.unmodifiableList(body);
    }

    public List<Term> getHeadTerms() {
        return headTerms;
    }

    public List<Literal> getBody() {
        return body;
    }
}
