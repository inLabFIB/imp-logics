package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

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

    public static Query createQuery(List<String> termsString, String queryString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return new Query(headTerms, immutableLiteralsList);
    }

    public static Query createQuery(List<String> termsString, String queryString, String derivationRuleString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString, derivationRuleString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return new Query(headTerms, immutableLiteralsList);
    }

}
