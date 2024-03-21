package edu.upc.fib.inlab.imp.kse.logics.schema;

import java.util.List;
import java.util.Objects;

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

    public Query(List<Term> headTerms, List<Literal> body) {
        if (Objects.isNull(headTerms)) throw new IllegalArgumentException("Head terms cannot be null");
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
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
        return body.stream().allMatch(l -> l instanceof OrdinaryLiteral ol && ol.isPositive() && ol.isBase());
    }
}
