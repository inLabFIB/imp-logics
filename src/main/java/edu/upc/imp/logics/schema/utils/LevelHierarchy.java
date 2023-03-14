package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.schema.Predicate;
import edu.upc.imp.logics.schema.exceptions.LevelHierarchyException;
import edu.upc.imp.logics.schema.exceptions.PredicateNotInLevel;

import java.util.*;

/**
 * <p> This class implements the definition of hierarchical database as defined in "Basis for Deductive Database Systems"
 * by J. W. Lloyd AND R. W. Topor.</p>
 *
 * <p>A database is called hierarchical if its predicates can be partitioned into
 * levels so that the definitions of level 0 predicates consist solely of base predicates
 * and the bodies of the derived predicates of level j contain only level i predicates, where i < j. </p>
 */
public class LevelHierarchy {
    private final List<Level> levels;
    /*
     * Invariants:
     *  - levels cannot be null
     *  - levels cannot be empty
     *  - levels cannot contain nulls
     *  - derived predicates cannot be defined in terms of higher predicates
     */

    public LevelHierarchy(List<Level> levels) {
        if (Objects.isNull(levels)) throw new IllegalArgumentException("Levels cannot be null");
        if (levels.isEmpty()) throw new IllegalArgumentException("Levels cannot be empty");
        if (levels.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Levels cannot contain null");
        checkLevelsCorrectness(levels);
        checkNoBasePredicateAppearsInHighLevel(levels);

        this.levels = Collections.unmodifiableList(levels);
    }

    private void checkNoBasePredicateAppearsInHighLevel(List<Level> levels) {
        for (int index = 1; index < levels.size(); ++index) {
            boolean derivedPredicateFound = levels.get(index).getAllPredicates().stream().anyMatch(Predicate::isBase);
            if (derivedPredicateFound) {
                throw new LevelHierarchyException("Cannot put base predicate in level " + index);
            }
        }
    }

    private void checkLevelsCorrectness(List<Level> levels) {
        for (int index = 0; index < levels.size(); ++index) {
            Set<Predicate> predicatesInLowerLevels = computePredicatesInLowerLevels(index, levels);
            Set<Predicate> predicatesInCurrentLevel = levels.get(index).getAllPredicates();
            checkLevelCorrectess(predicatesInCurrentLevel, predicatesInLowerLevels);
        }
    }

    private void checkLevelCorrectess(Set<Predicate> predicatesInCurrentLevel, Set<Predicate> predicatesInLowerLevels) {
        for (Predicate predicate : predicatesInCurrentLevel) {
            checkPredicatdIsDefinedInTermsOfOtherPredicates(predicate, predicatesInLowerLevels);
        }
    }

    private void checkPredicatdIsDefinedInTermsOfOtherPredicates(Predicate predicate, Set<Predicate> predicatesInLowerLevels) {
        Set<Predicate> predicatesUsedInDefinition = computePredicatesUsedInDefinition(predicate);
        if (!predicatesInLowerLevels.containsAll(predicatesUsedInDefinition)) {
            throw new LevelHierarchyException("Predicate " + predicate.getName() + "uses predicates from higher levels of the hierarchy");
        }

    }

    private Set<Predicate> computePredicatesUsedInDefinition(Predicate predicate) {
        Set<Predicate> predicatesUsed = new HashSet<>();
        for (DerivationRule rule : predicate.getDerivationRules()) {
            for (Literal literal : rule.getBody()) {
                if (literal instanceof OrdinaryLiteral ol) {
                    predicatesUsed.add(ol.getAtom().getPredicate());
                }
            }
        }
        return predicatesUsed;
    }

    private Set<Predicate> computePredicatesInLowerLevels(int currentLevelIndex, List<Level> levels) {
        Set<Predicate> result = new HashSet<>();
        for (int index = 0; index < currentLevelIndex; ++index) {
            result.addAll(levels.get(index).getAllPredicates());
        }
        return result;
    }

    public int getNumberOfLevels() {
        return levels.size();
    }

    public Level getLevel(int index) {
        return levels.get(index);
    }

    /**
     * May throw exception if p is not contained in the hierarchy
     *
     * @param p a predicate
     * @return the index of the predicate p
     */
    public int getLevelIndexOfPredicate(Predicate p) {
        for (int index = 0; index < this.getNumberOfLevels(); ++index) {
            Level level = getLevel(index);
            if (level.getAllPredicates().contains(p)) {
                return index;
            }
        }
        throw new PredicateNotInLevel(p.getName());
    }

    /**
     * May throw exception if p is not contained in the hierarchy
     *
     * @param p a predicate
     * @return the level of the predicate p
     */
    public Level getLevelOfPredicate(Predicate p) {
        int index = this.getLevelIndexOfPredicate(p);
        return this.getLevel(index);
    }

}
