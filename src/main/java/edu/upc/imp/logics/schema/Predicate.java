package edu.upc.imp.logics.schema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of a logic predicate. E.g. Predicate "Emp" with arity 2.
 * A Predicate is a weak entity w.r.t. LogicSchema. That is:
 * - One Predicate can only belong to one LogicSchema
 * - A LogicSchema cannot contain two predicates with the same name
 * <p>
 * To instantiate a derived prediacte, we use Queries, that is, a list of terms together a body:
 * (x, y) :- R(x, y)
 * (x, w) :- S(x, w), T(w)
 */
public class Predicate {
    /**
     * Invariants:
     * - name cannot be null
     * - arity cannot be null
     * - the list of derivationRules is not null
     * - the list of derivationRules it not empty
     * - derivationRules head terms size matches with arity
     * - derivationRules are immutable
     */

    protected final List<DerivationRule> derivationRules;

    private final String name;
    private final Arity arity;

    public Predicate(String name, Arity arity) {
        checkPredicateInfo(name, arity);
        this.name = name;
        this.arity = arity;
        this.derivationRules = new LinkedList<>();
    }

    public Predicate(String name, Arity arity, List<Query> definitionQueries) {
        this(name, arity);
        checkQueries(definitionQueries);
        List<DerivationRule> derivationRuleList = createDerivationRules(definitionQueries);
        derivationRules.addAll(derivationRuleList);
    }

    private static void checkPredicateInfo(String name, Arity arity) {
        if (Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        if (Objects.isNull(arity)) throw new IllegalArgumentException("Arity cannot be null");
    }

    private static void checkQueries(List<Query> definitionQueries) {
        if (Objects.isNull(definitionQueries)) throw new IllegalArgumentException("Definition rules cannot be null");
        if (definitionQueries.isEmpty()) throw new IllegalArgumentException("Definition rules cannot be empty");
    }

    private List<DerivationRule> createDerivationRules(List<Query> definitionQueries) {
        return definitionQueries.stream().map(q ->
                new DerivationRule(
                        new Atom(this, q.getHeadTerms()),
                        q.getBody())
        ).toList();
    }

    public Arity getArity() {
        return arity;
    }

    public String getName() {
        return name;
    }

    public List<DerivationRule> getDerivationRules() {
        return Collections.unmodifiableList(derivationRules);
    }

    public boolean isDerived() {
        return !derivationRules.isEmpty();
    }
}
