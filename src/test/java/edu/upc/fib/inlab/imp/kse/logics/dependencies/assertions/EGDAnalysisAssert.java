package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.EGDAnalysis;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Set;

public class EGDAnalysisAssert extends AbstractAssert<EGDAnalysisAssert, EGDAnalysis> {

    public EGDAnalysisAssert(EGDAnalysis egdAnalysis) {
        super(egdAnalysis, EGDAnalysisAssert.class);
    }


    public static EGDAnalysisAssert assertThat(EGDAnalysis actual) {
        return new EGDAnalysisAssert(actual);
    }

    public EGDAnalysisAssert containsFunctionalDependency(String predicateName, Set<Integer> keyPositions, Set<Integer> determinedPositions) {
        Assertions.assertThat(actual.functionalDependenciesEGDs())
                .anyMatch(fd -> fd.getPredicateName().equals(predicateName) &&
                        fd.functionalDependency().keyPositions().equals(keyPositions) &&
                        fd.functionalDependency().determinedPositions().equals(determinedPositions));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public EGDAnalysisAssert containsNonFunctionalEGD(List<EGD> egds) {
        Assertions.assertThat(actual.nonFunctionalDependencyEGDs()).containsAll(egds);
        return this;
    }
}
