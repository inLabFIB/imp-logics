package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.EGDToFDAnalysisResult;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.FunctionalDependencyWithEGDs;

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

    public static EGDAnalysisAssert assertThat(EGDToFDAnalysisResult actual) {
        return EGDAnalysisAssert.assertThat(actual);
    }

    public static EGDFunctionalDependencyAssert assertThat(FunctionalDependencyWithEGDs actual) {
        return EGDFunctionalDependencyAssert.assertThat(actual);
    }

}
