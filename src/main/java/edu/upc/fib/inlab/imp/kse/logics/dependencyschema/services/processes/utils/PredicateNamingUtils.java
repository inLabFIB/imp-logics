package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes.utils;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableAtomList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PredicateNamingUtils {
    private PredicateNamingUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Set<String> obtainPredicateNames(DependencySchema schema) {
        return schema.getAllPredicates().stream().map(Predicate::getName).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static String createNewAuxPredicateName(ImmutableAtomList head, Set<String> usedPredicateNames, String auxPredicateNameSuffix) {
        String headName = head.stream().map(a -> upperCaseTheFirstCharacter(a.getPredicateName())).collect(Collectors.joining("_"));
        String newAuxPredicateNameWithoutNumber = headName + auxPredicateNameSuffix;

        String newAuxPredicate = newAuxPredicateNameWithoutNumber;
        int auxiliarNumber = 2; //Used to search for non-clashing names
        while (usedPredicateNames.contains(newAuxPredicate)) {
            newAuxPredicate = newAuxPredicateNameWithoutNumber + auxiliarNumber++;
        }
        return newAuxPredicate;
    }

    public static String upperCaseTheFirstCharacter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
