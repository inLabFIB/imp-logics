package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.visitor.Visitable;
import edu.upc.imp.logics.schema.visitor.Visitor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic Atom.
 * An Atom consists of a Predicate (e.g. "Employee") together with a list of Terms (e.g. "x", "y").
 * An atom should belong, at most, to one NormalClause, or one literal. That is,
 * atoms should not be reused several times.
 */
public class Atom implements Visitable {
    /**
     * Invariants:
     * - Predicate must not be null
     * - terms must not be null
     * - The arity of the predicate should coincide with its list of terms
     * - Terms list is immutable
     */
    private final Predicate predicate;
    private final List<Term> terms;

    public Atom(Predicate predicate, List<Term> terms) {
        if (Objects.isNull(predicate)) throw new IllegalArgumentException("Predicate cannot be null");
        if (Objects.isNull(terms)) throw new IllegalArgumentException("Terms cannot be null");
        checkArityMatches(predicate.getArity(), terms);

        this.predicate = predicate;
        this.terms = Collections.unmodifiableList(terms);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<Term> getTerms() {
        return terms;
    }

    private static void checkArityMatches(Arity arity, List<Term> terms) {
        if (arity.getNumber() != terms.size()) throw new ArityMismatch(arity.getNumber(), terms.size());
    }

    @Override
    public <T, R> T accept(Visitor<T, R> visitor, R context) {
        return visitor.visitAtom(this, context);
    }
}
