package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateNameException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryBuilder {

    private Map<String, Predicate> predicatesByName;

    public QueryBuilder() {
        this(new LinkedHashMap<>());
    }

    public QueryBuilder(Map<String, MutablePredicate> relationalSchema) {
        this.predicatesByName = new LinkedHashMap<>(relationalSchema);
    }

    public QueryBuilder(Set<Predicate> relationalSchema) {
        this.predicatesByName = new LinkedHashMap<>();
        for (Predicate p : relationalSchema) predicatesByName.put(p.getName(), p);
    }

    public Query buildQuery(List<TermSpec> termSpecList, BodySpec bodySpec) {
        return buildQuery(termSpecList, bodySpec.literals());
    }

    public Query buildQuery(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        ContextTermFactory contextTermFactory = new ContextTermFactory(getAllVariableNames(termSpecList, bodySpec));
        ImmutableTermList headTerms = contextTermFactory.buildTerms(termSpecList);
        ImmutableLiteralsList body = buildBody(bodySpec, contextTermFactory);
        return QueryFactory.createQuery(headTerms, body);
    }

    private static Set<String> getAllVariableNames(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        return new DerivationRuleSpec("query", termSpecList, bodySpec).getAllVariableNames();
    }

    private ImmutableLiteralsList buildBody(List<LiteralSpec> bodySpec, ContextTermFactory contextTermFactory) {
        addPredicatesFromBody(bodySpec);
        return new BodyBuilder(new LiteralFactory(predicatesByName, contextTermFactory)).addLiterals(bodySpec).build();
    }

    private void addPredicatesFromBody(List<LiteralSpec> bodySpec) {
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                int numberOfTerms = olSpec.getTermSpecList().size();
                addPredicateIfAbsent(olSpec.getPredicateName(), numberOfTerms);
            } else if (!(literalSpec instanceof BuiltInLiteralSpec)) {
                throw new IMPLogicsException("Unrecognized literalSpec " + literalSpec.getClass().getName());
            }
        }
    }

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, arity));
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity() != arity) {
            throw new RepeatedPredicateNameException(predicateName);
        }
    }

    public Query buildQuery(QuerySpec queriesSpec) {
        return buildQuery(queriesSpec.getTermSpecList(), queriesSpec.getBodySpec());
    }
}
