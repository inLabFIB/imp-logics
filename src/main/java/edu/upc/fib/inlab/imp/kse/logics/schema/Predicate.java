package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateHasNoDerivationRules;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of a logic predicate. E.g. Predicate "Emp" with arity 2.
 * A Predicate is a weak entity w.r.t. LogicSchema. That is:
 * - One Predicate can only belong to one LogicSchema
 * - A LogicSchema cannot contain two predicates with the same name
 * To instantiate a derived predicate, we use Queries, that is, a list of terms together a body
 * For instance, if we have a mutable Predicate P, we can include some Queries to make it derived:
 * (x, y) :- R(x, y)
 * (x, w) :- S(x, w), T(w)
 * Do note that the queries do not require to include the predicate "P" on the head, since it would be redundant.
 */
public class Predicate {
    /**
     * Invariants:
     * - name cannot be null
     * - name cannot be empty
     * - arity >= 0
     * - the list of derivationRules is not null
     * - the list of derivationRules it not empty
     * - derivationRules head terms size matches with arity
     * - derivationRules are immutable when retrieved
     */

    protected final List<DerivationRule> derivationRules;

    private final String name;
    private final int arity;

    public Predicate(String name, int arity) {
        checkPredicateInfo(name, arity);
        this.name = name;
        this.arity = arity;
        this.derivationRules = new LinkedList<>();
    }

    public Predicate(String name, int arity, List<Query> definitionQueries) {
        this(name, arity);
        checkQueries(definitionQueries);
        List<DerivationRule> derivationRuleList = createDerivationRules(definitionQueries);
        derivationRules.addAll(derivationRuleList);
    }

    private static void checkPredicateInfo(String name, int arity) {
        if (Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (arity < 0) throw new IllegalArgumentException("Arity cannot be negative");
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

    public int getArity() {
        return arity;
    }

    public String getName() {
        return name;
    }

    public List<DerivationRule> getDerivationRules() {
        return Collections.unmodifiableList(derivationRules);
    }

    public DerivationRule getFirstDerivationRule() {
        if (isBase()) throw new PredicateHasNoDerivationRules(this);
        return getDerivationRules().get(0);
    }

    public boolean isDerived() {
        return !isBase();
    }

    public boolean isBase() {
        return derivationRules.isEmpty();
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
