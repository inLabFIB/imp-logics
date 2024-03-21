package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateHasNoDerivationRules;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Representation of a logic predicate. E.g. Predicate "Emp" with arity 2.
 * A Predicate is a weak entity w.r.t. LogicSchema. That is:
 * <ul>
 * <li> One Predicate can only belong to one LogicSchema </li>
 * <li> A LogicSchema cannot contain two predicates with the same name </li>
 * </ul>
 * <p> To instantiate a derived predicate, we use Queries, that is, a list of terms together a body
 * For instance, if we have a mutable Predicate P, we can include some Queries to make it derived:
 * <p> (x, y) :- R(x, y)
 * <p> (x, w) :- S(x, w), T(w)
 * <p> Do note that the queries do not require to include the predicate "P" on the head, since it would be redundant.
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

    /**
     * A predicate is recursive if it is derived and it appears in the body of its derivation rules,
     * or in the body of some derivation rule it depends on.
     * E.g.:
     * <p>R(x, y) :- R(x, z), R(z, y)
     * <p>R(x, y) :- T(x, y)
     * <p>P(x, y) :- R(x, z)
     * <p>Q(x, y) :- S(x, y)
     * <p>S(x, y) :- Q(x, y)
     * <p> In this example, R, Q and S are recurisve predicate, but P and T are not.
     *
     * @return whether this predicate is recursive
     */
    public boolean isRecursive() {
        return predicateAppearInDerivationRuleBody(new LinkedList<>(), this.getDerivationRules());
    }

    /**
     * @param visitedDerivationRules not null, might be empty
     * @param derivationRules        not null, might be empty
     * @return whether this predicate appears in the given derivation rules, ignoring those appearing in visitedDerivationRules
     */
    private boolean predicateAppearInDerivationRuleBody(List<DerivationRule> visitedDerivationRules, List<DerivationRule> derivationRules) {
        if (derivationRules.isEmpty()) return false;
        DerivationRule rule = derivationRules.get(0);
        List<DerivationRule> rulesStillToVisit = new LinkedList<>(derivationRules.subList(1, derivationRules.size()));
        if (visitedDerivationRules.contains(rule))
            return predicateAppearInDerivationRuleBody(visitedDerivationRules, rulesStillToVisit);
        else {
            visitedDerivationRules.add(rule);
            if (predicateAppearInDerivationRuleBody(rule)) return true;
            else {
                for (Literal lit : rule.getBody()) {
                    if (lit instanceof OrdinaryLiteral oLit) {
                        rulesStillToVisit.addAll(oLit.getPredicate().getDerivationRules());
                    }
                }
                return predicateAppearInDerivationRuleBody(visitedDerivationRules, rulesStillToVisit);
            }
        }

    }

    private boolean predicateAppearInDerivationRuleBody(DerivationRule rule) {
        return rule.getBody().stream().anyMatch(l -> l instanceof OrdinaryLiteral oLit && oLit.getPredicate() == this);
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
