package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

public class PredicateComparator {

    private PredicateComparator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean hasSameNameAndArityAs(Predicate p1, Predicate p2) {
        return p1.getName().equals(p2.getName()) && p1.getArity() == p2.getArity();
    }
}
