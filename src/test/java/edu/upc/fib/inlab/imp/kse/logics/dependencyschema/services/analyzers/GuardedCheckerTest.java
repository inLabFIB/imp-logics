package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GuardedCheckerTest {

    @Test
    void shouldReturnTrue_whenCheckingIfGuarded_withEmptySchema() {
        DependencySchema dependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

        boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

        Assertions.assertThat(isGuarded).isTrue();
    }

    @Test
    void shouldReturnTrue_whenCheckingIfGuarded_withSchemaWithLinearTGDs() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p() -> q()
                r(x,y), s(x) -> t(x,w)
                """);
        Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(0)).isGuarded()).isTrue();
        Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(1)).isGuarded()).isTrue();

        boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

        Assertions.assertThat(isGuarded).isTrue();
    }

    @Test
    void shouldReturnFalse_whenCheckingIfGuarded_withSchemaWithNonLinearTGDs() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p(x), q(y) -> r(s)
                """);

        boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

        Assertions.assertThat(isGuarded).isFalse();
    }

    @Test
    void shouldReturnFalse_whenCheckingIfGuarded_withSchemaWithEGDs() {
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                p(x) -> r(x)
                s(x) -> x = y
                """);

        boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

        Assertions.assertThat(isGuarded).isFalse();
    }

    @Nested
    class SchemaWithEGDsTest {
        @Test
        void shouldReturnTrue_whenEGDsAreNonConflicting_withGuardedTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept), Dept(dept) -> Person(name, age)
                    Person(name, age), Person(name, age2) -> age = age2
                    """);

            boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

            Assertions.assertThat(isGuarded).isTrue();
        }

        @Test
        void shouldReturnFalse_whenEGDsAreConflicting_withGuardedTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept), Dept(dept) -> Person(name, age)
                    Child(name, age), Person(name, age2) -> age = age2
                    """);

            boolean isGuarded = new GuardedChecker().isGuarded(dependencySchema);

            Assertions.assertThat(isGuarded).isFalse();
        }
    }

}