package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LinearCheckerTest {

    @Test
    void shouldReturnTrue_whenCheckingIfLinear_withEmptySchema() {
        DependencySchema dependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

        boolean isLinear = new LinearChecker().isLinear(dependencySchema);

        Assertions.assertThat(isLinear).isTrue();
    }

    @Test
    void shouldReturnTrue_whenCheckingIfLinear_withSchemaWithLinearTGDs() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p() -> q()
                r() -> s(), t()
                """);
        Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(0)).isLinear()).isTrue();
        Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(1)).isLinear()).isTrue();

        boolean isLinear = new LinearChecker().isLinear(dependencySchema);

        Assertions.assertThat(isLinear).isTrue();
    }

    @Test
    void shouldReturnFalse_whenCheckingIfLinear_withSchemaWithNonLinearTGDs() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p(), q() -> r()
                p() -> r()
                """);

        boolean isLinear = new LinearChecker().isLinear(dependencySchema);

        Assertions.assertThat(isLinear).isFalse();
    }

    @Nested
    class SchemaWithEGDsTest {
        @Test
        void shouldReturnTrue_whenEGDsAreNonConflicting_withLinearTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept) -> Person(name, age)
                    Person(name, age), Person(name, age2) -> age = age2
                    """);

            boolean isLinear = new LinearChecker().isLinear(dependencySchema);

            Assertions.assertThat(isLinear).isTrue();
        }

        @Test
        void shouldReturnFalse_whenEGDsAreConflicting_withLinterTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p() -> r()
                    s(x) -> x = y
                    """);

            boolean isLinear = new LinearChecker().isLinear(dependencySchema);

            Assertions.assertThat(isLinear).isFalse();
        }
    }
}