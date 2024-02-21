package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;

import java.util.List;

public record EGDAnalysis(List<FunctionalDependencyWithEGDs> functionalDependenciesEGDs,
                          List<EGD> nonFunctionalDependencyEGDs) {
}






