package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencyMother;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.TGDMother;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes.SingleHeadTGDTransformerTest.dependenciesComparator;
import static edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes.SingleHeadTGDTransformerTest.tgdComparator;
import static org.assertj.core.api.Assertions.assertThat;

class SingleExistentialVarTGDTransformerTest {

    static Stream<Arguments> provideTGDsTONormalizeExistentiallyQuantifiedVariables() {
        return Stream.of(
                Arguments.of(
                        "P(x) -> Q(x, y)",
                        Set.of(
                                "P(x) -> Q(x, y)"
                        )
                ),
                Arguments.of(
                        "P(x), Q(y) -> R(x, y, z)",
                        Set.of(
                                "P(x), Q(y) -> R(x, y, z)"
                        )
                ),
                Arguments.of(
                        "P(x) -> Q(x, y, z)",
                        Set.of(
                                "P(x) -> Q_WithOneExistentialVar(x, y)",
                                "Q_WithOneExistentialVar(x, y) -> Q_WithOneExistentialVar2(x, y, z)",
                                "Q_WithOneExistentialVar2(x, y, z) -> Q(x, y, z)"
                        )
                ),
                Arguments.of(
                        "P(x) -> Q(x, y), R(y)",
                        Set.of(
                                "P(x) -> Q(x, y), R(y)"
                        )
                ),
                Arguments.of(
                        "P(x) -> Q(x, y), R(y, z)",
                        Set.of(
                                "P(x) -> Q_R_WithOneExistentialVar(x, y)",
                                "Q_R_WithOneExistentialVar(x, y) -> Q_R_WithOneExistentialVar2(x, y, z)",
                                "Q_R_WithOneExistentialVar2(x, y, z) -> Q(x, y), R(y, z)"
                        )
                )
        );
    }

    static Stream<Arguments> provideSchemasTONormalizeExistentiallyQuantifiedVariables() {
        return Stream.of(
                Arguments.of(
                        """
                                P(x) -> Q(x, y)
                                T(x,y) -> x=y
                                """,
                        Set.of(
                                "P(x) -> Q(x, y)",
                                "T(x,y) -> x=y"
                        )
                ),
                Arguments.of(
                        """
                                 P(x) -> R1(x, y, z)
                                 P(x) -> R2(x, y, z)
                                """,
                        Set.of(
                                "P(x) -> R1_WithOneExistentialVar(x, y)",
                                "R1_WithOneExistentialVar(x, y) -> R1_WithOneExistentialVar2(x, y, z)",
                                "R1_WithOneExistentialVar2(x, y, z) -> R1(x, y, z)",
                                "P(x) -> R2_WithOneExistentialVar(x, y)",
                                "R2_WithOneExistentialVar(x,y) -> R2_WithOneExistentialVar2(x, y, z)",
                                "R2_WithOneExistentialVar2(x,y,z) -> R2(x, y, z)"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTGDsTONormalizeExistentiallyQuantifiedVariables")
    void should_return_tgdConvertedToSingleExistentialVarTGD(String tgdString, Set<String> expectedTGDsString) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(tgdString);
        SingleExistentialVarTGDTransformer transformer = new SingleExistentialVarTGDTransformer();

        DependencySchema transformedSchema = transformer.execute(schema);

        Set<TGD> expectedTGDs = expectedTGDsString.stream().map(TGDMother::createTGD).collect(Collectors.toSet());

        assertThat(transformedSchema.getAllTGDs())
                .usingElementComparator(tgdComparator())
                .containsExactlyInAnyOrderElementsOf(expectedTGDs);
    }

    @ParameterizedTest
    @MethodSource("provideSchemasTONormalizeExistentiallyQuantifiedVariables")
    void should_return_schemasConvertedToSingleExistentialVarTGD(String tgdString, Set<String> expectedTGDsString) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(tgdString);
        SingleExistentialVarTGDTransformer transformer = new SingleExistentialVarTGDTransformer();

        DependencySchema transformedSchema = transformer.execute(schema);

        Set<Dependency> expectedDependencies = expectedTGDsString.stream().map(DependencyMother::buildDependency).collect(Collectors.toSet());

        assertThat(transformedSchema.getAllDependencies())
                .usingElementComparator(dependenciesComparator())
                .containsExactlyInAnyOrderElementsOf(expectedDependencies);
    }
}