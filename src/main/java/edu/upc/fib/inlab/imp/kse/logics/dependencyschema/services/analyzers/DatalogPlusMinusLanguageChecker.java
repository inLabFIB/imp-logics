package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;

public abstract class DatalogPlusMinusLanguageChecker {


    protected static boolean someDependencyContainsBuiltInOrNegatedLiteralInBody(DependencySchema dependencySchema) {
        return dependencySchema.getAllDependencies().stream().anyMatch(Dependency::containsBuiltInOrNegatedLiteralInBody);
    }

    public abstract boolean satisfies(DependencySchema dependencySchema);

    public abstract DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName();


}
