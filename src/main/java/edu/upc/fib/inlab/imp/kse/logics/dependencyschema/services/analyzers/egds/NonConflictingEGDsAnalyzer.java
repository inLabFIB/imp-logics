package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;

public class NonConflictingEGDsAnalyzer {

    /**
     * Method responsible to check if the set of EGDs is non-conflicting
     *
     * @param dependencySchema not null
     * @return whether the egds of this schema are non-conflicting with the TGDs
     */
    public boolean areEGDsNonConflictingWithTGDs(DependencySchema dependencySchema) {
        EGDToFDAnalysisResult egdToFDAnalysisResult = new EGDToFDAnalyzer().analyze(dependencySchema.getAllEGDs());

        if (egdToFDAnalysisResult.allEGDsDefinesKeyDependencies()) {
            return new NonConflictingFDsAnalyzer().isNonConflicting(dependencySchema.getAllTGDs(), egdToFDAnalysisResult.getFunctionalDependencies());
        } else return false;
    }
}
