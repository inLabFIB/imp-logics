package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StickyCheckerTest {

    @Nested
    class StickySchemaTests {
        @Test
        void shouldReturnTrue_WithEmptySchema() {
            DependencySchema emptyDependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

            boolean isSticky = new StickyChecker().isSticky(emptyDependencySchema);

            Assertions.assertThat(isSticky).isTrue();
        }

        @Test
        void shouldReturnTrue_withStickySchema() {
            DependencySchema emptyDependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(x,y) -> p(y,z)
                    p(x,y) -> q(x)
                    q(x), q(y) -> r(x,y)
                    p(x,y), p(z,x) -> q(x)
                    """);

            boolean isSticky = new StickyChecker().isSticky(emptyDependencySchema);

            Assertions.assertThat(isSticky).isTrue();
        }

        @Test
        void shouldReturnFalse_withNonStickySchema() {
            DependencySchema emptyDependencySchema = DependencySchemaMother.buildDependencySchema("""
                    q(x), q(y) -> r(x)
                    p(y,x), p(x, z) -> q(x)
                    """);

            boolean isSticky = new StickyChecker().isSticky(emptyDependencySchema);

            Assertions.assertThat(isSticky).isFalse();
        }

        @Nested
        class SchemaWithEGDsTest {
            @Test
            void shouldReturnTrue_whenEGDsAreNonConflicting_withStickyTGDs() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                        p(x,y) -> p(y,z)
                        p(a, b), p(a, b2) -> b = b2
                        """);

                boolean isGuarded = new StickyChecker().isSticky(dependencySchema);

                Assertions.assertThat(isGuarded).isTrue();
            }

            @Test
            void shouldReturnFalse_whenEGDsAreConflicting_withStickyTGDs() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                        p(x,y) -> p(y,z)
                        p(a, b), q(a, b2) -> b = b2
                        """);

                boolean isGuarded = new StickyChecker().isSticky(dependencySchema);

                Assertions.assertThat(isGuarded).isFalse();
            }
        }
    }

}