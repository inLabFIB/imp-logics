package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.List;

public class DerivedPredicateMother {
    /**
     * Create a derived predicate with as many definition rules as predicates given by parameter.
     * Each definition rule is composed of only one predicate
     *
     * @param derivedPredicateName must be non-null
     * @param arity                must be non-negative
     * @param predicatesList       must be non-null, and non-empty
     * @return a derived predicate
     */
    public static MutablePredicate createTrivialDerivedPredicate(String derivedPredicateName, int arity, List<Predicate> predicatesList) {
        List<Query> queries = predicatesList.stream().map(p -> QueryMother.createTrivialQuery(1, p)).toList();
        return new MutablePredicate(derivedPredicateName, arity, queries);
    }


    public static Predicate createOArityDerivedPredicate(String predicateName, Predicate predicateP) {
        Query query = new Query(List.of(), List.of(new OrdinaryLiteral(new Atom(predicateP, List.of()))));
        return new Predicate(predicateName, 0, List.of(query));
    }

    public static Predicate createDerivedPredicate(String predicateName, String schemaString) {
        return LogicSchemaMother.buildLogicSchemaWithIDs(schemaString).getPredicateByName(predicateName);
    }
}
