package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateIsNotDerivedException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateNotExistsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateOutsideSchemaException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateNameException;

import java.util.*;

/**
 * A dependency schema bounds several predicates and dependencies together guaranteeing their consistency.
 */
public class DependencySchema {

    /**
     * Invariants:
     * <ul>
     *     <li>We cannot have two predicates with the same name</li>
     *     <li>All predicates used in the dependencies appear in the predicates set</li>
     *     <li>All dependencies are defined through predicates of the dependencySchema</li>
     * </ul>
     */
    private final Map<String, Predicate> predicatesByName = new HashMap<>();
    private final Set<Dependency> dependencies = new LinkedHashSet<>();

    public DependencySchema(Set<Predicate> predicates, Set<Dependency> dependencies) {
        predicates.forEach(predicate -> {
            if (predicatesByName.containsKey(predicate.getName())) throw new RepeatedPredicateNameException(predicate.getName());
            this.predicatesByName.put(predicate.getName(), predicate);
        });

        dependencies.forEach(d -> {
            checkPredicatesBelongsToSchema(d);
            this.dependencies.add(d);
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
                    throw new PredicateOutsideSchemaException(predicateFromConstraint.getName());
            }
        }
    }

    private void checkPredicatesBelongsToSchema(Dependency d) {
        for (Literal l : d.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchemaException(predicateFromConstraint.getName());
            }
        }
        if (d instanceof TGD tgd) {
            for (Atom atom : tgd.getHead()) {
                Predicate predicateFromConstraint = atom.getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchemaException(predicateFromConstraint.getName());
            }
        }
    }

    public Set<Predicate> getAllPredicates() {
        return new LinkedHashSet<>(predicatesByName.values());
    }

    public Predicate getPredicateByName(String predicateName) {
        if (!predicatesByName.containsKey(predicateName)) {
            throw new PredicateNotExistsException(predicateName);
        }
        return predicatesByName.get(predicateName);
    }

    public List<DerivationRule> getDerivationRulesByPredicateName(String derivedPredicateName) {
        if (!predicatesByName.containsKey(derivedPredicateName)) throw new PredicateNotExistsException(derivedPredicateName);

        Predicate predicate = predicatesByName.get(derivedPredicateName);
        if (predicate.isDerived()) {
            return predicate.getDerivationRules();
        } else throw new PredicateIsNotDerivedException(derivedPredicateName);
    }

    public Set<Dependency> getAllDependencies() {
        return new LinkedHashSet<>(dependencies);
    }

    public List<TGD> getAllTGDs() {
        List<TGD> result = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (dependency instanceof TGD tgd) {
                result.add(tgd);
            }
        }
        return result;
    }

    public List<EGD> getAllEGDs() {
        return dependencies.stream()
                .filter(EGD.class::isInstance)
                .map(d -> (EGD) d)
                .toList();
    }


    public <T> T accept(DependencySchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isEmpty() {
        return predicatesByName.isEmpty() && dependencies.isEmpty();
    }

}
