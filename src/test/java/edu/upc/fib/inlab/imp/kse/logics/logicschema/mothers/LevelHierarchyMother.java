package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Level;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LevelHierarchy;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LevelHierarchyMother {
    @SafeVarargs
    public static LevelHierarchy createLevelHierarchy(List<String>... predicateNamesLevels) {
        List<Level> hierarchyLevels = new LinkedList<>();
        hierarchyLevels.add(createBasePredicates(predicateNamesLevels[0]));

        for (int index = 1; index < predicateNamesLevels.length; ++index) {
            Predicate predicateFromPreviousLevel = hierarchyLevels.get(index - 1).getAllPredicates().iterator().next();
            hierarchyLevels.add(createDerivedPredicate(predicateNamesLevels[index], predicateFromPreviousLevel));
        }
        return new LevelHierarchy(hierarchyLevels);
    }

    private static Level createBasePredicates(List<String> predicateNamesLevel) {
        Set<Predicate> predicates = predicateNamesLevel.stream()
                .map(name -> new Predicate(name, 0))
                .collect(Collectors.toSet());
        return new Level(predicates);
    }

    private static Level createDerivedPredicate(List<String> predicateNamesLevel, Predicate predicateFromPreviousLevel) {
        Set<Predicate> predicates = predicateNamesLevel.stream()
                .map(name -> DerivedPredicateMother.createOArityDerivedPredicate(name, predicateFromPreviousLevel))
                .collect(Collectors.toSet());
        return new Level(predicates);
    }

    @SafeVarargs
    public static LevelHierarchy createLevelHierarchy(Set<Predicate>... predicateLevels) {
        List<Level> listOfLevels = Arrays.stream(predicateLevels)
                .map(Level::new)
                .toList();

        return new LevelHierarchy(listOfLevels);
    }
}
