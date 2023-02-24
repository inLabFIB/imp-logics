package edu.upc.imp.logics.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mutable implementation of a logic predicate.
 * The logic predicate is mutable in the sense that we can add derivation rules to it at runtime.
 */
public class MutablePredicate extends Predicate {
    /**
     * Invariants:
     * - the list of derivationRules is not null
     * - derivationRules head terms size matches with arity
     * - derivationRules are mutable
     */

    private List<DerivationRule> derivationRules;

    public MutablePredicate(String name, int arity) {
        super(name, arity);
        derivationRules = List.of();
    }

    public MutablePredicate(String name, int arity, List<Query> definitionQueries) {
        super(name, arity);
        if (Objects.isNull(definitionQueries)) throw new IllegalArgumentException("Definition rules cannot be null");
        derivationRules = createDerivationRules(definitionQueries);
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
