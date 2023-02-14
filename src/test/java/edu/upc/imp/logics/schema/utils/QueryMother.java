package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.*;

import java.util.LinkedList;
import java.util.List;

public class QueryMother {

    public static Query createTrivialQuery(int headTermsNumber, String predNameInBody) {
        List<Term> terms = new LinkedList<>();
        for (int i = 0; i < headTermsNumber; ++i) {
            terms.add(new Variable("x" + i));
        }
        return new Query(terms, List.of(LiteralMother.createOrdinaryLiteral(predNameInBody, terms)));
    }

    public static Query createTrivialQuery(int headTermsNumber, Predicate predicateInBody) {
        List<Term> terms = new LinkedList<>();
        for (int i = 0; i < headTermsNumber; ++i) {
            terms.add(new Variable("x" + i));
        }
        return new Query(terms, List.of(new OrdinaryLiteral(new Atom(predicateInBody, terms))));
    }
}
