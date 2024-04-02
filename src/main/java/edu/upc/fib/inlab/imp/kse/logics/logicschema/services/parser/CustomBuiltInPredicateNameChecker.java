package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import java.util.Set;

public class CustomBuiltInPredicateNameChecker implements BuiltInPredicateNameChecker {
    private final Set<String> builtInPredicateNames;

    public CustomBuiltInPredicateNameChecker(Set<String> builtInPredicateNames) {
        this.builtInPredicateNames = builtInPredicateNames;
    }

    public boolean isBuiltInPredicateName(String predicateName) {
        return builtInPredicateNames.contains(predicateName);
    }

}
