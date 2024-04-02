package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import java.util.List;
import java.util.Objects;

public class QueryFactory {

    private QueryFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static ConjunctiveQuery createConjunctiveQuery(List<Term> headTerms, List<Literal> body) {
        return new ConjunctiveQuery(headTerms, body);
    }

    public static Query createQuery(List<Term> headTerms, List<Literal> body) {
        checkArguments(headTerms, body);
        if (isConjunctiveQuery(body)) return new ConjunctiveQuery(headTerms, body);
        return new Query(headTerms, body);
    }

    private static void checkArguments(List<Term> headTerms, List<Literal> body) {
        if (Objects.isNull(headTerms)) throw new IllegalArgumentException("Head terms cannot be null");
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
    }

    private static boolean isConjunctiveQuery(List<Literal> body) {
        return body.stream().allMatch(l -> l instanceof OrdinaryLiteral ol && ol.isPositive() && ol.isBase());
    }
}
