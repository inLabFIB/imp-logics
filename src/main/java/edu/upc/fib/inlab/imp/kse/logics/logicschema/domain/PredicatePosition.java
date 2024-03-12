package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

public record PredicatePosition(Predicate predicate, int position) {
    public String getPredicateName() {
        return predicate.getName();
    }
}
