package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.NonConflictingEGDsAnalyzer;

public class LinearChecker extends DatalogPlusMinusLanguageChecker {

    public boolean isLinear(DependencySchema dependencySchema) {
        return satisfies(dependencySchema);
    }

    @Override
    public boolean satisfies(DependencySchema dependencySchema) {
        if (someDependencyContainsBuiltInOrNegatedLiteralInBody(dependencySchema)) {
            throw new UnsupportedOperationException("Linear analysis does not currently support negated nor built-in literals");
        }
        if (!new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(dependencySchema)) return false;

        return dependencySchema.getAllTGDs().stream().allMatch(TGD::isLinear);
    }

    @Override
    public DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName() {
        return DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.LINEAR;
    }

}
