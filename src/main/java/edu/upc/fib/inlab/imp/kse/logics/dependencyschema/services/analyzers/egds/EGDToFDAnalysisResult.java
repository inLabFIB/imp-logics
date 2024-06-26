package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;

import java.util.List;

public record EGDToFDAnalysisResult(List<FunctionalDependencyWithEGDs> functionalDependenciesEGDs,
                                    List<EGD> nonFunctionalDependencyEGDs) {
    public List<FunctionalDependency> getFunctionalDependencies() {
        return functionalDependenciesEGDs.stream().map(FunctionalDependencyWithEGDs::functionalDependency).toList();
    }

    public boolean allEGDsDefinesKeyDependencies() {
        return nonFunctionalDependencyEGDs.isEmpty() &&
                functionalDependenciesEGDs.stream().allMatch(fd -> fd.functionalDependency().isKeyDependency());
    }
}






