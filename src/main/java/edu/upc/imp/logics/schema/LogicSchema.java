package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.*;
import edu.upc.imp.logics.schema.utils.LevelHierarchy;

import java.util.*;

/**
 * This class implements the representation of a logic schema.
 * A logic schema bounds several predicates and constraints together guaranteeing their consistency.
 */
public class LogicSchema {
    /**
     * Invariants:
     * - We cannot have two predicates with the same name
     * - We cannot have two logicConstraints with the same ConstraintID
     * - All the predicates used in the constraints appear in the predicates
     * - All the constraints are defined through predicates of the logicSchema
     * - All the derivation rules defining the predicates are included in this logicSchema
     */
    private final Map<String, Predicate> predicatesByName = new HashMap<>();
    private final Map<ConstraintID, LogicConstraint> constraintsByID = new HashMap<>();

    public LogicSchema(Set<Predicate> predicates, Set<LogicConstraint> constraints) {
        predicates.forEach(predicate -> {
            if (predicatesByName.containsKey(predicate.getName())) throw new RepeatedPredicateName(predicate.getName());
            predicatesByName.put(predicate.getName(), predicate);
        });

        constraints.forEach(c -> {
            if (constraintsByID.containsKey(c.getID())) throw new RepeatedConstraintID(c.getID());
            checkPredicatesBelongsToSchema(c);

            constraintsByID.put(c.getID(), c);
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
        //TODO: replace by a visitor that obtains Predicates
        for (Literal l : c.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    public Predicate getPredicateByName(String predicateName) {
        if (!predicatesByName.containsKey(predicateName)) {
            throw new PredicateNotExists(predicateName);
        }
        return predicatesByName.get(predicateName);
    }

    public LogicConstraint getLogicConstraintByID(ConstraintID constraintID) {
        if (!constraintsByID.containsKey(constraintID)) throw new LogicConstraintNotExists(constraintID);
        return constraintsByID.get(constraintID);
    }

    public List<DerivationRule> getDerivationRulesByPredicateName(String derivedPredicateName) {
        if (!predicatesByName.containsKey(derivedPredicateName)) throw new PredicateNotExists(derivedPredicateName);

        Predicate predicate = predicatesByName.get(derivedPredicateName);
        if (predicate.isDerived()) {
            return predicate.getDerivationRules();
        } else throw new PredicateIsNotDerived(derivedPredicateName);
    }

    public Set<Predicate> getAllPredicates() {
        return new HashSet<>(predicatesByName.values());
    }

    public Set<LogicConstraint> getAllLogicConstraints() {
        return new HashSet<>(constraintsByID.values());
    }

    public Set<DerivationRule> getAllDerivationRules() {
        return this.getAllPredicates().stream()
                .map(Predicate::getDerivationRules)
                .map(HashSet::new)
                .reduce(new HashSet<>(), (subtotal, element) ->
                {
                    subtotal.addAll(element);
                    return subtotal;
                });
    }

    public LevelHierarchy computeLevelHierarchy() {
        return null;
    }
}
