package edu.upc.imp.logics.schema;

import java.util.ArrayList;
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
public class MutablePredicate extends Predicate {
    /**
     * Invariants:
     * - the list of derivationRules is not null
     * - derivationRules head terms size matches with arity
     * - derivationRules are mutable
     */

    private List<DerivationRule> derivationRules;

    public MutablePredicate(String name, Arity arity, List<Query> definitionQueries) {
        super(name, arity);
        if (Objects.isNull(definitionQueries)) throw new IllegalArgumentException("Definition rules cannot be null");
        derivationRules = createDerivationRules(definitionQueries);
    }

    public MutablePredicate(String name, Arity arity) {
        super(name, arity);
        derivationRules = List.of();
    }

    public List<DerivationRule> getDerivationRules() {
        return derivationRules;
    }

    private List<DerivationRule> createDerivationRules(List<Query> definitionQueries) {
        return definitionQueries.stream().map(q ->
                new DerivationRule(
                        new Atom(this, q.getHeadTerms()),
                        q.getBody())
        ).toList();
    }

    public void addDerivationRule(Query definitionRule) {
        derivationRules = new ArrayList<>(derivationRules);
        derivationRules.add(new DerivationRule(
                new Atom(this, definitionRule.getHeadTerms()),
                definitionRule.getBody()));
        derivationRules = derivationRules.stream().toList();
    }

    @Override
    public boolean isDerived() {
        return !derivationRules.isEmpty();
    }
}
