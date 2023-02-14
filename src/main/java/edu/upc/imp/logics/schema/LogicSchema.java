package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.*;

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
    private final Map<String, Predicate> predicates = new HashMap<>();
    private final Map<ConstraintID, LogicConstraint> constraints = new HashMap<>();

    public LogicSchema(Set<Predicate> predicates, Set<LogicConstraint> constraints) {
        predicates.forEach(predicate -> {
            if (this.predicates.containsKey(predicate.getName())) throw new RepeatedPredicateName(predicate.getName());
            this.predicates.put(predicate.getName(), predicate);
        });

        constraints.forEach(c -> {
            if (this.constraints.containsKey(c.getID())) throw new RepeatedConstraintID(c.getID());
            checkPredicatesBelongsToSchema(c);

            this.constraints.put(c.getID(), c);
        });

        checkDerivedPredicatesUsesPredicatesFromSchema();
    }

    private void checkDerivedPredicatesUsesPredicatesFromSchema() {
        for (Predicate p : this.predicates.values()) {
            if (p instanceof DerivedPredicate derivedPredicate) {
                derivedPredicate.getDerivationRules().forEach(this::checkPredicatesBelongsToSchema);
            }
        }
    }

    private void checkPredicatesBelongsToSchema(NormalClause c) {
        //TODO: replace by a visitor that obtains Predicates
        for (Literal l : c.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = this.predicates.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    public Predicate getPredicate(String predicateName) {
        if (!this.predicates.containsKey(predicateName)) {
            throw new PredicateNotExists(predicateName);
        }
        return this.predicates.get(predicateName);
    }

    public LogicConstraint getLogicConstraintByID(ConstraintID constraintID) {
        if (!constraints.containsKey(constraintID)) throw new LogicConstraintNotExists(constraintID);
        return constraints.get(constraintID);
    }

    public List<DerivationRule> getDerivationRules(String derivedPredicateName) {
        if (!predicates.containsKey(derivedPredicateName)) throw new PredicateNotExists(derivedPredicateName);

        Predicate predicate = predicates.get(derivedPredicateName);
        if (predicate instanceof DerivedPredicate derivedPredicate) {
            return derivedPredicate.getDerivationRules();
        } else throw new PredicateIsNotDerived(derivedPredicateName);
    }

    public Set<Predicate> getAllPredicates() {
        return new HashSet<>(predicates.values());
    }

    public Set<LogicConstraint> getAllLogicConstraints() {
        return new HashSet<>(constraints.values());
    }
}
