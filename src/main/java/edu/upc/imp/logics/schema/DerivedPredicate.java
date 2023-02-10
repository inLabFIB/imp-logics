package edu.upc.imp.logics.schema;

import java.util.List;
import java.util.Objects;

public class DerivedPredicate extends Predicate{
    /**
     * Invariants:
     * - the list of derivationRules is not null
     * - the list of derivationRules it not empty
     * - derivationRules head terms size matches with arity
     * - derivationRules are inmutable
     */

    private final List<DerivationRule> derivationRules;

    public DerivedPredicate(String name, Arity arity, List<Query> definitionQueries) {
        super(name, arity);
        if(Objects.isNull(definitionQueries)) throw new IllegalArgumentException("Definition rules cannot be null");
        if(definitionQueries.isEmpty()) throw new IllegalArgumentException("Definition rules cannot be empty");
        checkArityOfDefinitionRules(arity, definitionQueries);

        this.derivationRules = createDerivationRules(definitionQueries);
    }

    private static void checkArityOfDefinitionRules(Arity arity, List<Query> definitionRules) {
        definitionRules.stream().forEach(q-> {
                arity.checkMatches(q.getHeadTerms().size());
            }
        );
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
}
