package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencyMother;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.TGDMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class SingleHeadTGDTransformerTest {

    static Stream<Arguments> provideTGDsToNormalizeToOneHeadAtom() {
        return Stream.of(
                Arguments.of(
                        "P(x) -> A1(x)",
                        Set.of(
                                "P(x) -> A1(x)"
                        )
                ),
                Arguments.of(
                        "P(x) -> A1(x), A2(x)",
                        Set.of(
                                "P(x) -> A1_A2_SingleHead(x)",
                                "A1_A2_SingleHead(x) -> A1(x)",
                                "A1_A2_SingleHead(x) -> A2(x)"
                        )
                ),
                Arguments.of(
                        "P(x), Q(x) -> A1(x), A2(y)",
                        Set.of(
                                "P(x), Q(x) -> A1_A2_SingleHead(x, y)",
                                "A1_A2_SingleHead(x, y) -> A1(x)",
                                "A1_A2_SingleHead(x, y) -> A2(y)"
                        )
                ),
                Arguments.of(
                        "P(x), A1_A2_SingleHead(x) -> A1(x), A2(y)",
                        Set.of(
                                "P(x), A1_A2_SingleHead(x) -> A1_A2_SingleHead2(x, y)",
                                "A1_A2_SingleHead2(x, y) -> A1(x)",
                                "A1_A2_SingleHead2(x, y) -> A2(y)"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTGDsToNormalizeToOneHeadAtom")
    void should_return_SingleHeadedTGD_whenSchemaHasOneTGD(String tgdString, Set<String> expectedTGDsString) {
        DependencySchema tgd = DependencySchemaMother.buildDependencySchema(tgdString);
        SingleHeadTGDTransformer tgdNormalizer = new SingleHeadTGDTransformer();

        DependencySchema normalizedTGD = tgdNormalizer.execute(tgd);

        Set<TGD> expectedTGDs = expectedTGDsString.stream().map(TGDMother::createTGD).collect(Collectors.toSet());

        Assertions.assertThat(normalizedTGD.getAllTGDs())
                .usingElementComparator(tgdComparator())
                .containsExactlyInAnyOrderElementsOf(expectedTGDs);
    }

    static Stream<Arguments> provideSchemasToNormalizeToOneHeadAtom() {
        return Stream.of(
                Arguments.of(
                        """
                                P(x) -> A1(x)
                                T(x,y) -> x=y
                                """,
                        Set.of(
                                "P(x) -> A1(x)",
                                "T(x,y) -> x=y"
                        )
                ),
                Arguments.of(
                        """
                                P(x) -> A1(x), A2(x)
                                P(x), Q(x) -> A1(x), A2(y)
                                T(x,y) -> x=y
                                """,
                        Set.of(
                                "P(x) -> A1_A2_SingleHead(x)",
                                "A1_A2_SingleHead(x) -> A1(x)",
                                "A1_A2_SingleHead(x) -> A2(x)",
                                "P(x), Q(x) -> A1_A2_SingleHead2(x, y)",
                                "A1_A2_SingleHead2(x, y) -> A1(x)",
                                "A1_A2_SingleHead2(x, y) -> A2(y)",
                                "T(x,y) -> x=y"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideSchemasToNormalizeToOneHeadAtom")
    void should_return_SingleHeadedTGD_whenSchemaHasSeveralDependencies(String dependencyString, Set<String> expectedTGDsString) {
        DependencySchema schema = DependencySchemaMother.buildDependencySchema(dependencyString);
        SingleHeadTGDTransformer tgdNormalizer = new SingleHeadTGDTransformer();

        DependencySchema normalizedTGD = tgdNormalizer.execute(schema);

        Set<Dependency> expectedDependencies = expectedTGDsString.stream().map(DependencyMother::buildDependency).collect(Collectors.toSet());

        Assertions.assertThat(normalizedTGD.getAllDependencies())
                .usingElementComparator(dependenciesComparator())
                .containsExactlyInAnyOrderElementsOf(expectedDependencies);
    }

    static Comparator<TGD> tgdComparator() {
        return (tgd1, tgd2) -> {
            try {
                LogicSchemaAssertions.assertThat(tgd1.getHead())
                        .containsAtomsByPredicateName(tgd2.getHead());
            } catch (AssertionError e) {
                return -1;
            }
            try {
                List<String> tgd2BodyLiteralAsStringList = tgd2.getBody().stream()
                        .map(Literal::toString)
                        .toList();
                LogicSchemaAssertions.assertThat(tgd1.getBody())
                        .hasSize(tgd2.getBody().size())
                        .containsExactlyLiteralsOf(tgd2BodyLiteralAsStringList);
            } catch (AssertionError e) {
                return 1;
            }
            return 0;
        };
    }

    static Comparator<Dependency> dependenciesComparator() {
        return (dep1, dep2) -> {
            if (dep1 instanceof TGD tgd1 && dep2 instanceof TGD tgd2) return tgdComparator().compare(tgd1, tgd2);
            else if (dep1 instanceof TGD || dep2 instanceof TGD) return -1;

            try {
                EGD egd1 = (EGD) dep1;
                EGD egd2 = (EGD) dep2;
                if (!egd1.getHead().getTerms().containsAll(egd2.getHead().getTerms())) {
                    return -1;
                }
                List<String> dep2BodyAsString = dep2.getBody().stream()
                        .map(Literal::toString)
                        .toList();
                LogicSchemaAssertions.assertThat(dep1.getBody())
                        .hasSize(dep2.getBody().size())
                        .containsExactlyLiteralsOf(dep2BodyAsString);
            } catch (ClassCastException | AssertionError ex2) {
                return -1;
            }
            return 0;
        };
    }
}