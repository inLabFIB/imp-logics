package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


class DatalogPlusMinusLanguageCheckerTest {

    @Nested
    class SeparableEGDsTest {

        static class DatalogPlusMinusLanguageCheckerDummy extends DatalogPlusMinusLanguageChecker {

            @Override
            public boolean satisfies(DependencySchema dependencySchema) {
                return false;
            }

            @Override
            public DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName() {
                return null;
            }
        }

        @Test
        void shouldIdentifyAsSeparable_whenEGDsAreKeyDependencies_notConflictingWithTGDs() {
            DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept) -> Person(name, age)
                    Person(name, age), Person(name, age2) -> age=age2
                    """);

            boolean separable = new DatalogPlusMinusLanguageCheckerDummy().areEGDsNonConflictingWithTGDs(schema);

            Assertions.assertThat(separable).isTrue();
        }

        private static Stream<Arguments> provideConflictingSchema() {
            return Stream.of(
                    Arguments.of("EGD is conflicting KeyDependency",
                            """
                                        Child(name, age) -> Person(name, age)
                                        Person(name, age), Person(name, age2) -> age=age2
                                    """),
                    Arguments.of("EGD is not Functional Dependency",
                            """
                                    WorksIn(name, dept) -> Person(name, age)
                                    Person(name, age), Child(name, age2) -> age=age2
                                            """
                    ),
                    Arguments.of("EGD is Functional Dependency but not Key Dependency",
                            """
                                     WorksIn(name, dept) -> Person(name, city, state)
                                    Person(name, city, state), Person(name2, city, state2) -> state=state2
                                                    """
                    )
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideConflictingSchema")
        void shouldIdentifyAsNonSeparable_whenEGDsAreConflictingWithTGDs(@SuppressWarnings("unused") String title, String schemaString) {
            DependencySchema schema = DependencySchemaMother.buildDependencySchema(schemaString);

            boolean separable = new DatalogPlusMinusLanguageCheckerDummy().areEGDsNonConflictingWithTGDs(schema);

            Assertions.assertThat(separable).isFalse();
        }
    }
}