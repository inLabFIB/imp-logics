package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Arity;
import edu.upc.imp.logics.schema.BasePredicate;
import edu.upc.imp.logics.schema.DerivedPredicate;
import edu.upc.imp.logics.schema.Query;

import java.util.List;

public class DerivedPredicateMother {
    /**
     * Create a derived predicate with as many definition rules as predicates given by parameter.
     * Each definition rule is composed of only one predicate
     *
     * @param derivedPredicateName must be non-null
     * @param arity must be non-negative
     * @param predicatesList must be non-null, and non-empty
     * @return a derived predicate
     */
    public static DerivedPredicate createTrivialDerivedPredicate(String derivedPredicateName, int arity, List<BasePredicate> predicatesList) {
        List<Query> queries = predicatesList.stream().map(p -> QueryMother.createTrivialQuery(1, p)).toList();
        return new DerivedPredicate(derivedPredicateName, new Arity(arity), queries);
    }


}
