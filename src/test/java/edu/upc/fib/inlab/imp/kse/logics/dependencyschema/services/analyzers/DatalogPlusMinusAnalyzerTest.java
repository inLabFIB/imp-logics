package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


class DatalogPlusMinusAnalyzerTest {

    @Test
    void should_returnLinearGuardedAndWeaklyGuarde_whenSchemaIsLinear() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p(x,y,y) -> q(x)
                r(x) -> s(x), t(y)
                """);

        Set<DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage> datalogPlusMinusLanguages =
                new DatalogPlusMinusAnalyzer().getDatalogPlusMinusLanguages(dependencySchema);

        Assertions.assertThat(datalogPlusMinusLanguages)
                .containsExactly(
                        DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.LINEAR,
                        DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.GUARDED,
                        DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.WEAKLY_GUARDED);
    }

    @Test
    void should_returnSticky_whenSchemaIsSticky() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(x,y) -> p(y,z)
                    p(x,y) -> q(x)
                    q(x), q(y) -> r(x,y)
                    p(x,y), p(z,x) -> q(x)
                """);

        Set<DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage> datalogPlusMinusLanguages =
                new DatalogPlusMinusAnalyzer().getDatalogPlusMinusLanguages(dependencySchema);

        Assertions.assertThat(datalogPlusMinusLanguages)
                .containsExactly(
                        DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.STICKY);
    }

}