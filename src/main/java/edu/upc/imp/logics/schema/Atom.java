package edu.upc.imp.logics.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic Atom.
 * An Atom consists of a Predicate (e.g. "Employee") together with a list of Terms (e.g. "x", "y").
 * An atom should belong, at most, to one NormalClause, or one literal. That is,
 * atoms should not be reused several times.
 */
public class Atom {
    /**
     *  Invariants:
     *   - Predicate must not be null
     *   - terms must not be null
     *   - The arity of the predicate should coincide with its list of terms
     *   - Terms list is inmutable
     */
    private final Predicate predicate;
    private final List<Term> terms;

    public Atom(Predicate predicate, List<Term> terms) {
        if (Objects.isNull(predicate)) throw new IllegalArgumentException("Predicate cannot be null");
        if (Objects.isNull(terms)) throw new IllegalArgumentException("Terms cannot be null");
        predicate.getArity().checkMatches(terms.size());

        this.predicate = predicate;
        this.terms = Collections.unmodifiableList(terms);
    }


    public Predicate getPredicate() {
        return predicate;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
