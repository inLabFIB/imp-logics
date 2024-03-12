package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import java.util.List;

/**
 * A Query is composed of a list of terms, and a body
 * E.g.: (x,y) :- P(x, y), not(R(x))
 * Queries are used to define DerivedPredicates.
 * For instance, the derived predicate D, with arity 2, is defined by the queries:
 * (x,y) :- P(x, y), not(R(x))
 * (x,y) :- P(x, y), not(S(x))
 */
public class Query {
    /**
     * Invariants:
     * - headTerms cannot be null, although it might be empty
     * - headTerms must be immutable
     * - body cannot be null
     * - body cannot be empty
     * - body is immutable
     */

    private final ImmutableTermList headTerms;
    private final ImmutableLiteralsList body;

    Query(List<Term> headTerms, List<Literal> body) {
        this.headTerms = new ImmutableTermList(headTerms);
        this.body = new ImmutableLiteralsList(body);
    }

    public ImmutableTermList getHeadTerms() {
        return headTerms;
    }

    public ImmutableLiteralsList getBody() {
        return body;
    }

    public boolean isConjunctiveQuery() {
        return false;
    }
}
