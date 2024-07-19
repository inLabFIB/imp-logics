package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;

import java.util.List;

/**
 * Class that stores the functionalDependency encoded by some egdList
 */
public record FunctionalDependencyWithEGDs(List<EGD> egdList, FunctionalDependency functionalDependency) {
    public String getPredicateName() {
        return functionalDependency.getPredicateName();
    }
}
