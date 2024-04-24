package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.dependencyschema.assertions.DependencySchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class EGDDependencyAnalyzerTest {

    private static Stream<Arguments> providesFunctionalDependencyEGDsTestCases() {
        return Stream.of(
                Arguments.of(
                        "One-to-one functional dependency",
                        "Person(name, age), Person(name, age2) -> age=age2",
                        "Person",
                        List.of(0),
                        List.of(1)
                ),
                Arguments.of(
                        "Two-to-one functional dependency",
                        "Person(name, surname, age), Person(name, surname, age2) -> age=age2",
                        "Person",
                        List.of(0, 1),
                        List.of(2)
                ),
                Arguments.of(
                        "One-to-two functional dependency",
                        """
                                Person(name, age, weight), Person(name, age2, weight2) -> age=age2
                                Person(name, age, weight), Person(name, age2, weight2) -> weight=weight2
                                """,
                        "Person",
                        List.of(0),
                        List.of(1, 2)
                ),
                Arguments.of(
                        "Two-to-two functional dependency",
                        """
                                Person(name, surname, age, weight), Person(name, surname, age2, weight2) -> age=age2
                                Person(name, surname, age, weight), Person(name, surname, age2, weight2) -> weight=weight2
                                """,
                        "Person",
                        List.of(0, 1),
                        List.of(2, 3)
                )
        );
    }

    private static Stream<Arguments> providesNonFunctionalDependencyEGDsTestCases() {
        return Stream.of(
                Arguments.of(
                        "EGD is defined over different predicates",
                        "Person(name, age), Child(name, age2) -> age=age2",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD contains built-in literal",
                        "Person(name, age), Person(name, age2), age>18 -> age=age2",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD contains 3 literals",
                        "Person(name, age), Person(name, age2), Person(name, 3) -> age=age2",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD contains some constant",
                        "Person(name, age, 0), Person(name, age2, weight) -> age=age2",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD equates different positions",
                        "Person(name, age, weight), Person(name, age2, weight2) -> age=weight2",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD equates same positions of same literal",
                        "Person(name, age, weight), Person(name, age2, weight2) -> age=age",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD equates position with constant",
                        "Person(name, age, weight), Person(name, age2, weight2) -> age=1",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD equates constants",
                        "Person(name, age, weight), Person(name, age2, weight2) -> 1=1",
                        List.of(0)
                ),
                Arguments.of(
                        "EGD equates two variables of same literal",
                        "Person(name, age, age, x), Person(name, age2, weight2, y) -> x=y",
                        List.of(0)
                ),
                Arguments.of(
                        "Unsafe EGD",
                        "Person(name, age), Person(name, age2) -> x=y",
                        List.of(0)
                )
        );
    }

    @ParameterizedTest(name = "Test Case: {0}")
    @MethodSource("providesFunctionalDependencyEGDsTestCases")
    void shouldIdentifyFunctionalDependencyEGDs(@SuppressWarnings("unused") String testTitle, String schemaDefinition, String entity, List<Integer> keyPositions, List<Integer> determinedPositions) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(schemaDefinition);

        EGDToFDAnalyzer analyzer = new EGDToFDAnalyzer();
        EGDToFDAnalysisResult result = analyzer.analyze(schema.getAllEGDs());
        assertThat(result.functionalDependenciesEGDs()).anySatisfy(
                fd -> assertThat(fd)
                        //TODO: .containsExactlyEGDs(schema.getAllEGDs().get(0), schema.getAllEGDs().get(1))
                        .affectsPredicate(entity)
                        .containsExactlyKeyPositions(keyPositions)
                        .containsExactlyDeterminedPositions(determinedPositions));
    }

    @ParameterizedTest(name = "Test case: {0}")
    @MethodSource("providesNonFunctionalDependencyEGDsTestCases")
    void shouldIdentifyNonFunctionalDependencyEGDs(@SuppressWarnings("unused") String testTitle, String schemaDefinition, List<Integer> egdIndexes) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(schemaDefinition);

        EGDToFDAnalyzer analyzer = new EGDToFDAnalyzer();
        EGDToFDAnalysisResult result = analyzer.analyze(schema.getAllEGDs());
        List<EGD> indexedEGDList = schema.getAllEGDs().stream()
                .filter(egd -> egdIndexes.contains(schema.getAllEGDs().indexOf(egd)))
                .toList();
        assertThat(result).containsNonFunctionalEGD(indexedEGDList);
    }

    @Test
    void shouldIdentifyFunctionalAndNonFunctionalDependencyEGD() {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(
                """
                                Person(name, age, weight), Person(name, age2, weight2) -> age=age2
                                Person(name, age, weight), Person(name, age2, weight2) -> weight=weight2
                                Child(name, age), Person(name, age2, weight) -> age = age2
                                P(x, y, z), P(x, y2, z2) -> y=y2
                        """);
        EGDToFDAnalyzer analyzer = new EGDToFDAnalyzer();
        EGDToFDAnalysisResult result = analyzer.analyze(schema.getAllEGDs());

        assertThat(result.functionalDependenciesEGDs())
                .anySatisfy(
                        fd -> assertThat(fd)
                                .containsExactlyEGDs(schema.getAllEGDs().get(0), schema.getAllEGDs().get(1))
                                .affectsPredicate("Person")
                                .containsExactlyKeyPositions(0)
                                .containsExactlyDeterminedPositions(1, 2))
                .anySatisfy(
                        fd -> assertThat(fd)
                                .containsExactlyEGDs(schema.getAllEGDs().get(3))      //P(x, y, z), P(x, y2, z2) -> y=y2
                                .affectsPredicate("P")
                                .containsExactlyKeyPositions(0)
                                .containsExactlyDeterminedPositions(1)
                );

        List<EGD> indexedEGDList = List.of(schema.getAllEGDs().get(2)); //Child(name, age), Person(name, age2) -> age = age2
        assertThat(result).containsNonFunctionalEGD(indexedEGDList);
    }

    @Test
    void shouldIdentifyFunctionalAndNonFunctionalDependencyEGD_evenUnderRepetitions() {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(
                """
                                Person(name, age, weight), Person(name, age2, weight2) -> age=age2
                                Person(name, age, weight), Person(name, age2, weight2) -> weight=weight2
                                Person(name, age, weight), Person(name, age2, weight3) -> weight=weight3
                        """);
        EGDToFDAnalyzer analyzer = new EGDToFDAnalyzer();
        EGDToFDAnalysisResult result = analyzer.analyze(schema.getAllEGDs());

        assertThat(result.functionalDependenciesEGDs()).anySatisfy(
                fd -> assertThat(fd)
                        .containsExactlyEGDs(schema.getAllEGDs().get(0), schema.getAllEGDs().get(1), schema.getAllEGDs().get(2))
                        .affectsPredicate("Person")
                        .containsExactlyKeyPositions(0)
                        .containsExactlyDeterminedPositions(1, 2)
        );

        assertThat(result).containsNonFunctionalEGD(List.of());
    }

}