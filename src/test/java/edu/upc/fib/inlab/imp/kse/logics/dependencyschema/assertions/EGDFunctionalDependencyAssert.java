package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.FunctionalDependencyWithEGDs;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.List;


public class EGDFunctionalDependencyAssert extends AbstractAssert<EGDFunctionalDependencyAssert, FunctionalDependencyWithEGDs> {

    public EGDFunctionalDependencyAssert(FunctionalDependencyWithEGDs actual) {
        super(actual, EGDFunctionalDependencyAssert.class);
    }

    public static EGDFunctionalDependencyAssert assertThat(FunctionalDependencyWithEGDs actual) {
        return new EGDFunctionalDependencyAssert(actual);
    }

    public EGDFunctionalDependencyAssert affectsPredicate(String predicateName) {
        Assertions.assertThat(actual.getPredicateName())
                .as("Should be same predicate name")
                .isEqualTo(predicateName);
        return this;
    }

    public EGDFunctionalDependencyAssert containsExactlyEGDs(EGD... egd) {
        return containsExactlyEGDs(Arrays.stream(egd).toList());
    }

    public EGDFunctionalDependencyAssert containsExactlyEGDs(List<EGD> egdList) {
        Assertions.assertThat(actual.egdList())
                .as("Should contain exactly the same EGDs")
                .hasSameSizeAs(egdList)
                .containsExactlyElementsOf(egdList);
        return this;
    }

    public EGDFunctionalDependencyAssert containsExactlyKeyPositions(Integer... keyPosition) {
        return containsExactlyKeyPositions(Arrays.stream(keyPosition).toList());
    }


    public EGDFunctionalDependencyAssert containsExactlyKeyPositions(List<Integer> keyPositions) {
        Assertions.assertThat(actual.functionalDependency().keyPositions())
                .as("Should contain exactly the same key positions")
                .containsExactlyElementsOf(keyPositions);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public EGDFunctionalDependencyAssert containsExactlyDeterminedPositions(Integer... determinedPosition) {
        return containsExactlyDeterminedPositions(Arrays.stream(determinedPosition).toList());
    }

    public EGDFunctionalDependencyAssert containsExactlyDeterminedPositions(List<Integer> determinedPositions) {
        Assertions.assertThat(actual.functionalDependency().determinedPositions())
                .as("Should contain exactly the same determined positions")
                .containsExactlyElementsOf(determinedPositions);
        return this;
    }
}
