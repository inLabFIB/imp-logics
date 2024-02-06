package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateIsNotDerived;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateOutsideSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedPredicateName;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A dependency schema bounds several predicates and dependencies together guaranteeing their consistency.
 */
public class DependencySchema {

    /**
     * Invariants:
     * <ul>
     *     <li>We cannot have two predicates with the same name</li>
     *     <li>We cannot have two logicConstraints with the same ConstraintID</li>
     *     <li>All the predicates used in the constraints appear in the predicates</li>
     *     <li>All the constraints are defined through predicates of the logicSchema</li>
     *     <li>All the derivation rules defining the predicates are included in this logicSchema</li>
     * </ul>
     */
    private final Map<String, Predicate> predicatesByName = new HashMap<>();
    private final Set<Dependency> dependencies = new HashSet<>();

    public DependencySchema(Set<Predicate> predicates, Set<Dependency> dependencies) {
        predicates.forEach(predicate -> {
            if (predicatesByName.containsKey(predicate.getName())) throw new RepeatedPredicateName(predicate.getName());
            predicatesByName.put(predicate.getName(), predicate);
        });

        dependencies.forEach(d -> {
            checkPredicatesBelongsToSchema(d);
            dependencies.add(d);
        });

        checkDerivedPredicatesUsesPredicatesFromSchema();
    }

    private void checkDerivedPredicatesUsesPredicatesFromSchema() {
        for (Predicate p : predicatesByName.values()) {
            if (p.isDerived()) {
                p.getDerivationRules().forEach(this::checkPredicatesBelongsToSchema);
            }
        }
    }

    private void checkPredicatesBelongsToSchema(NormalClause c) {
        for (Literal l : c.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    private void checkPredicatesBelongsToSchema(Dependency d) {
        for (Literal l : d.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
        if (d instanceof TGD tgd) {
            for (Atom atom : tgd.getHead()) {
                Predicate predicateFromConstraint = atom.getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    public List<DerivationRule> getDerivationRulesByPredicateName(String derivedPredicateName) {
        if (!predicatesByName.containsKey(derivedPredicateName)) throw new PredicateNotExists(derivedPredicateName);

        Predicate predicate = predicatesByName.get(derivedPredicateName);
        if (predicate.isDerived()) {
            return predicate.getDerivationRules();
        } else throw new PredicateIsNotDerived(derivedPredicateName);
    }

    public Set<Predicate> getAllDerivedPredicates() {
        return predicatesByName.values().stream()
                .filter(Predicate::isDerived)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Predicate getPredicateByName(String predicateName) {
        if (!predicatesByName.containsKey(predicateName)) {
            throw new PredicateNotExists(predicateName);
        }
        return predicatesByName.get(predicateName);
    }

    public Set<Predicate> getAllPredicates() {
        return new LinkedHashSet<>(predicatesByName.values());
    }

    public Set<Dependency> getDependencies() {
        return new HashSet<>(dependencies);
    }

    //TODO: IMPR-189 Implement sticky check
    public boolean isSticky() {
        return false;
    }

    //TODO: IMPR-188 Implement weakly guarded check
    public boolean isWeaklyGuarded() {
        return false;
    }
}
