package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Query createBooleanQuery(String queryString) {
        return createQuery(List.of(), queryString);
    }

    public static ConjunctiveQuery createConjunctiveQuery(List<String> termsString, String queryString) {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create(queryString);
        List<Term> headTerms = TermMother.createTerms(termsString);
        return QueryFactory.createConjunctiveQuery(headTerms, immutableLiteralsList);
    }

    public static ConjunctiveQuery createBooleanConjunctiveQuery(String queryString) {
        return createConjunctiveQuery(List.of(), queryString);
    }

    public static ConjunctiveQuery createBooleanConjunctiveQuery(String conjunctiveQueryString, Set<Predicate> predicates) {
        return createConjunctiveQuery(List.of(), conjunctiveQueryString, predicates);
    }


    public static ConjunctiveQuery createConjunctiveQuery(List<String> termsString, String conjunctiveQueryString, Set<Predicate> predicates) {
        List<Literal> literals = new LinkedList<>();

        String regex = "(\\w+)\\(([^)]*)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conjunctiveQueryString);

        while (matcher.find()) {
            String predicateName = matcher.group(1);
            List<String> termNames = List.of(matcher.group(2).split(","));
            termNames = termNames.stream().map(String::trim).toList();

            List<Term> constantList = TermMother.createTerms(termNames);
            Predicate predicate = predicates.stream().filter(p -> p.getName().equals(predicateName)).findFirst().get();

            literals.add(new OrdinaryLiteral(new Atom(predicate, constantList)));
        }
        return QueryFactory.createConjunctiveQuery(TermMother.createTerms(termsString), literals);
    }
}
