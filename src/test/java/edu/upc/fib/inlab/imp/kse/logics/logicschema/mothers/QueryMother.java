package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.LinkedList;
import java.util.List;

public class QueryMother {

    public static Query createTrivialQuery(int headTermsNumber, String predNameInBody) {
        List<Term> terms = new LinkedList<>();
        for (int i = 0; i < headTermsNumber; ++i) {
            terms.add(new Variable("x" + i));
        }
        return QueryFactory.createQuery(terms, List.of(LiteralMother.createOrdinaryLiteral(predNameInBody, terms)));
    }

    public static Query createTrivialQuery(int headTermsNumber, Predicate predicateInBody) {
        List<Term> terms = new LinkedList<>();
        for (int i = 0; i < headTermsNumber; ++i) {
            terms.add(new Variable("x" + i));
        }
        return QueryFactory.createQuery(terms, List.of(new OrdinaryLiteral(new Atom(predicateInBody, terms))));
    }

    public static Query createQuery(List<String> termsString, String queryString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return QueryFactory.createQuery(headTerms, immutableLiteralsList);
    }

    public static Query createQuery(List<String> termsString, String queryString, String derivationRuleString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString, derivationRuleString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return QueryFactory.createQuery(headTerms, immutableLiteralsList);
    }

    public static ConjunctiveQuery createConjunctiveQuery(List<String> termsString, String queryString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return QueryFactory.createConjunctiveQuery(headTerms, immutableLiteralsList);
    }

    public static ConjunctiveQuery createConjunctiveQuery(String queryString) {
        return createConjunctiveQuery(List.of(), queryString);
    }

}
