package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.EGDAnalysis;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.FunctionalDependencyWithEGDs;

public class DependencySchemaAssertions {

    public static DependencySchemaAssert assertThat(DependencySchema actual) {
        return DependencySchemaAssert.assertThat(actual);
    }

    public static DependencyAssert assertThat(Dependency actual) {
        return DependencyAssert.assertThat(actual);
    }

    public static TGDAssert assertThat(TGD actual) {
        return TGDAssert.assertThat(actual);
    }

    public static EGDAssert assertThat(EGD actual) {
        return EGDAssert.assertThat(actual);
    }

    public static EGDAnalysisAssert assertThat(EGDAnalysis actual) {
        return EGDAnalysisAssert.assertThat(actual);
    }

    public static EGDFunctionalDependencyAssert assertThat(FunctionalDependencyWithEGDs actual) {
        return EGDFunctionalDependencyAssert.assertThat(actual);
    }

}
