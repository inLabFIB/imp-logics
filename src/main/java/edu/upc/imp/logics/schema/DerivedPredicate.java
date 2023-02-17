package edu.upc.imp.logics.schema;

import java.util.List;
import java.util.Objects;

/**
 * A predicate whose definition depends on several derivation rules
 * E.g. predicate P, with arity 2, is a derived predicate defined by:
 * P(x, y) :- R(x, y)
 * P(x, w) :- S(x, w), T(w)
 * To instantiate a DerivedPredicate, we use Queries, that is, a list of terms together a body:
 * (x, y) :- R(x, y)
 * (x, w) :- S(x, w), T(w)
 * Indeed, it is redundant to include the predicate P in the rules that derive P.
 */
public class DerivedPredicate extends Predicate{
    /**
     * Invariants:
     * - the list of derivationRules is not null
     * - the list of derivationRules it not empty
     * - derivationRules head terms size matches with arity
     * - derivationRules are immutable
     */

    private final List<DerivationRule> derivationRules;

    public DerivedPredicate(String name, Arity arity, List<Query> definitionQueries) {
        super(name, arity);
        if (Objects.isNull(definitionQueries)) throw new IllegalArgumentException("Definition rules cannot be null");
        if (definitionQueries.isEmpty()) throw new IllegalArgumentException("Definition rules cannot be empty");
        this.derivationRules = createDerivationRules(definitionQueries);
    }

    public List<DerivationRule> getDerivationRules() {
        return derivationRules;
    }

    private List<DerivationRule> createDerivationRules(List<Query> definitionQueries) {
        return definitionQueries.stream().map(q->
                new DerivationRule(
                        new Atom(this, q.getHeadTerms()),
                        q.getBody())
        ).toList();
    }

    @Override
    public boolean isDerived(){
        return true;
    }
}
