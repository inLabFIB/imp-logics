package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.BooleanBuiltInLiteral;

public class BooleanBuiltInPredicateNameChecker implements BuiltInPredicateNameChecker {
    public boolean isBuiltInPredicateName(String predicateName) {
        return BooleanBuiltInLiteral.fromOperator(predicateName).isPresent();
    }

}
