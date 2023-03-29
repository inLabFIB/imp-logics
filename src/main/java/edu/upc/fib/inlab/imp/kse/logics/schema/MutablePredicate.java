package edu.upc.fib.inlab.imp.kse.logics.schema;

import java.util.List;

/**
 * Mutable implementation of a logic predicate.
 * The logic predicate is mutable in the sense that we can add derivation rules to it at runtime.
 */
public class MutablePredicate extends Predicate {

    public MutablePredicate(String name, int arity) {
        super(name, arity);
    }

    public MutablePredicate(String name, int arity, List<Query> definitionQueries) {
        super(name, arity, definitionQueries);
    }

    public void addDerivationRule(Query definitionRule) {
        derivationRules.add(new DerivationRule(
                new Atom(this, definitionRule.getHeadTerms()),
                definitionRule.getBody()));
    }
}
