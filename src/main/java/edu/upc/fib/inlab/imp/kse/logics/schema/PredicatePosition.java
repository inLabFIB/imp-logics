package edu.upc.fib.inlab.imp.kse.logics.schema;

public record PredicatePosition(Predicate predicate, int position) {
    public String getPredicateName() {
        return predicate.getName();
    }
}
