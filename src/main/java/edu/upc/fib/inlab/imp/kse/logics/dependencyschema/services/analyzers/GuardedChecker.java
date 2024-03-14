package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;

public class GuardedChecker extends DatalogPlusMinusLanguageChecker {

    public boolean isGuarded(DependencySchema dependencySchema) {
        return satisfies(dependencySchema);
    }

    @Override
    public boolean satisfies(DependencySchema dependencySchema) {
        if (someDependencyContainsBuiltInOrNegatedLiteralInBody(dependencySchema)) {
            throw new UnsupportedOperationException("Guarded analysis does not currently support negated nor built-in literals");
        }
        if (!areEGDsNonConflictingWithTGDs(dependencySchema)) return false;

        return dependencySchema.getAllTGDs().stream().allMatch(TGD::isGuarded);
    }

    @Override
    public DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName() {
        return DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.GUARDED;
    }
}
