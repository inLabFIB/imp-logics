package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.LevelHierarchyException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateNotInLevel;

import java.util.*;
import java.util.function.Consumer;

/**
 * <p> This class implements the definition of hierarchical database as defined in "Basis for Deductive Database Systems"
 * by J. W. Lloyd AND R. W. Topor.</p>
 *
 * <p>A database is called hierarchical if its predicates can be partitioned into
 * levels so that the definitions of level 0 predicates consist solely of base predicates
 * and the bodies of the derived predicates of level j contain only level i predicates, where {@code i < j}. </p>
 */
public class LevelHierarchy implements Iterable<Level> {
    private final List<Level> levels;
    /*
     * Invariants:
     *  - levels cannot be null
     *  - levels cannot contain nulls
     *  - derived predicates cannot be defined in terms of higher predicates
     *
     * levels can be empty since a LogicSchema can be empty too
     */

    public LevelHierarchy(List<Level> levels) {
        if (Objects.isNull(levels)) throw new IllegalArgumentException("Levels cannot be null");
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
            checkLevelCorrectness(predicatesInCurrentLevel, predicatesInLowerLevels);
        }
    }

    private void checkLevelCorrectness(Set<Predicate> predicatesInCurrentLevel, Set<Predicate> predicatesInLowerLevels) {
        for (Predicate predicate : predicatesInCurrentLevel) {
            checkPredicateIsDefinedInTermsOfOtherPredicates(predicate, predicatesInLowerLevels);
        }
    }

    private void checkPredicateIsDefinedInTermsOfOtherPredicates(Predicate predicate, Set<Predicate> predicatesInLowerLevels) {
        Set<Predicate> predicatesUsedInDefinition = computePredicatesUsedInDefinition(predicate);
        if (!predicatesInLowerLevels.containsAll(predicatesUsedInDefinition)) {
            throw new LevelHierarchyException("Predicate " + predicate.getName() + "uses predicates from higher levels of the hierarchy");
        }

    }

    private Set<Predicate> computePredicatesUsedInDefinition(Predicate predicate) {
        Set<Predicate> predicatesUsed = new LinkedHashSet<>();
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
        Set<Predicate> result = new LinkedHashSet<>();
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
     * May throw exception if predicate is not contained in the hierarchy
     *
     * @param predicate a non-null predicate
     * @return the index of the predicate
     */
    public int getLevelIndexOfPredicate(Predicate predicate) {
        if (Objects.isNull(predicate)) throw new IllegalArgumentException("Predicate cannot be null");
        for (int index = 0; index < this.getNumberOfLevels(); ++index) {
            Level level = getLevel(index);
            if (level.getAllPredicates().contains(predicate)) {
                return index;
            }
        }
        throw new PredicateNotInLevel(predicate.getName());
    }

    /**
     * May throw exception if predicate is not contained in the hierarchy
     *
     * @param predicate a non-null predicate
     * @return the level of the predicate
     */
    public Level getLevelOfPredicate(Predicate predicate) {
        int index = this.getLevelIndexOfPredicate(predicate);
        return this.getLevel(index);
    }

    /**
     * @return the 0-th level of the hierarchy, which contains the base predicates
     */
    public Level getBasePredicatesLevel() {
        return this.getLevel(0);
    }

    /**
     * @return a list with the levels representing derived predicates (i.e., i-th levels with i > 0)
     */
    public List<Level> getDerivedLevels() {
        return this.levels.subList(1, levels.size());
    }

    @Override
    public Iterator<Level> iterator() {
        return levels.iterator();
    }

    @Override
    public void forEach(Consumer<? super Level> action) {
        Iterable.super.forEach(action);
    }

}
