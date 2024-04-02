package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import java.util.List;

public class ConjunctiveQuery extends Query {
    ConjunctiveQuery(List<Term> headTerms, List<Literal> body) {
        super(headTerms, body);
        if (!isConjunctiveQuery())
            throw new IllegalArgumentException("Body must only contain positive ordinary literals with base predicates");
    }

    public ImmutableAtomList getBodyAtoms() {
        return new ImmutableAtomList(getBody().stream()
                .map(l -> ((OrdinaryLiteral) l).getAtom())
                .toList());
    }

    @Override
    public boolean isConjunctiveQuery() {
        return true;
    }

}
