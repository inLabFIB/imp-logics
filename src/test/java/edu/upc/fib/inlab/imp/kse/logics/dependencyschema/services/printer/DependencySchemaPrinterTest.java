package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencySchemaPrinterTest {

    @Test
    void should_printDependencySchema_withOneTGD() {
        String schemaString = "p() -> q()";
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

        String result = new DependencySchemaPrinter().print(dependencySchema);

        assertThat(result).isEqualToIgnoringWhitespace(schemaString);
    }

    @Test
    void should_printDependencySchema_withOneEGD() {
        String schemaString = "p(x,y) -> x=y";
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

        String result = new DependencySchemaPrinter().print(dependencySchema);

        assertThat(result).isEqualToIgnoringWhitespace(schemaString);
    }

    @Test
    void should_printDependencySchema_withMultipleDependencies() {
        String schemaString = """
                p() -> q()
                w(x,y) -> x=y
                r() -> s()
                t(x,y) -> x=y
                """;
        DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

        String result = new DependencySchemaPrinter().print(dependencySchema);

        assertThat(result).isEqualToIgnoringWhitespace(schemaString);
    }

    @Nested
    class DerivedPredicatesDependencySchemasTests {

        @Test
        void should_printDependencySchema_withOneTGD_andDerivedPredicates() {
            String schemaString = """
                    p() -> q()
                    p() :- p1(), p2()
                    """;
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

            String result = new DependencySchemaPrinter().print(dependencySchema);

            assertThat(result).isEqualToIgnoringWhitespace(schemaString);
        }

        @Test
        void should_printDependencySchema_withOneEGD_andDerivedPredicates() {
            String schemaString = """
                    p(x,y) -> x=y
                    p(a,b) :- p1(a), p2(b)
                    """;
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

            String result = new DependencySchemaPrinter().print(dependencySchema);

            assertThat(result).isEqualToIgnoringWhitespace(schemaString);
        }

        @Test
        void should_printDependencySchema_withOneEGD_andMultipleDerivedPredicates() {
            String schemaString = """
                    p(x,y) -> x=y
                    p(a,b) :- p1(a), p2(b)
                    p2(a) :- p22(a)
                    p1(a) :- p11(a)
                    p(a,b) :- q(a,a)
                    """;
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema(schemaString);

            String result = new DependencySchemaPrinter().print(dependencySchema);

            String expectedSchemaString = """
                    p(x,y) -> x=y
                    p(a,b) :- p1(a), p2(b)
                    p(a,b) :- q(a,a)
                    p1(a) :- p11(a)
                    p2(a) :- p22(a)
                    """;
            assertThat(result).isEqualToIgnoringWhitespace(expectedSchemaString);
        }
    }
}