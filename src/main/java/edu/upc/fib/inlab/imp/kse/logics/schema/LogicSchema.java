package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.Level;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LevelHierarchy;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the representation of a logic schema.
 * <br>
 * A logic schema bounds several predicates and constraints together guaranteeing their consistency.
 */
public class LogicSchema {
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

    public boolean isEmpty() {
        return predicatesByName.isEmpty() && constraintsByID.isEmpty();
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
        return new LinkedHashSet<>(predicatesByName.values());
    }

    public Set<NormalClause> getAllNormalClauses() {
        Set<NormalClause> normalClauses = new LinkedHashSet<>(getAllLogicConstraints());
        normalClauses.addAll(getAllDerivationRules());
        return normalClauses;
    }


    public Set<LogicConstraint> getAllLogicConstraints() {
        return new LinkedHashSet<>(constraintsByID.values());
    }

    public Set<DerivationRule> getAllDerivationRules() {
        return predicatesByName.values().stream()
                .filter(Predicate::isDerived)
                .flatMap(p -> p.getDerivationRules().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Predicate> getAllDerivedPredicates() {
        return predicatesByName.values().stream()
                .filter(Predicate::isDerived)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * A LevelHierarchy is a partition of the predicates of a schema into several levels,
     * where level 0 contains the base predicates, and each derived predicate from level i
     * is defined through predicates from levels j < i.
     *
     * @return a LevelHierarchy for this schema
     */
    public LevelHierarchy computeLevelHierarchy() {
        Map<Predicate, Integer> predicateToLevelMap = new HashMap<>();
        for (Predicate predicate : this.getAllPredicates()) {
            fillPredicateIntoALevel(predicate, predicateToLevelMap);
        }

        List<Level> levels = createLevels(predicateToLevelMap);
        return new LevelHierarchy(levels);

    }

    private void fillPredicateIntoALevel(Predicate predicate, Map<Predicate, Integer> predicateToLevelMap) {
        if (predicateToLevelMap.containsKey(predicate)) return;
        if (predicate.isBase()) {
            predicateToLevelMap.put(predicate, 0);
        } else {
            int maxLevelOfUsedPredicate = 0;
            for (DerivationRule rule : predicate.getDerivationRules()) {
                for (Literal literal : rule.getBody()) {
                    if (literal instanceof OrdinaryLiteral ordinaryLiteral) {
                        Predicate usedPredicate = ordinaryLiteral.getAtom().getPredicate();
                        fillPredicateIntoALevel(usedPredicate, predicateToLevelMap);
                        int levelOfUsedPredicate = predicateToLevelMap.get(usedPredicate);
                        maxLevelOfUsedPredicate = Math.max(maxLevelOfUsedPredicate, levelOfUsedPredicate);
                    }
                }
            }
            predicateToLevelMap.put(predicate, maxLevelOfUsedPredicate + 1);
        }
    }

    private List<Level> createLevels(Map<Predicate, Integer> predicateToLevelMap) {
        List<Level> levels = new LinkedList<>();
        Optional<Integer> max = predicateToLevelMap.values().stream().max(Integer::compareTo);
        if (max.isEmpty()) return levels;

        for (int index = 0; index <= max.get(); ++index) {
            int finalIndex = index;
            Set<Predicate> predicates = predicateToLevelMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(finalIndex))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            levels.add(new Level(predicates));
        }
        return levels;
    }

    public boolean isSafe() {
        return getAllLogicConstraints().stream().allMatch(LogicConstraint::isSafe)
                && getAllDerivationRules().stream().allMatch(DerivationRule::isSafe);
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
