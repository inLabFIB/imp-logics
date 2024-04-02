package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.TGDMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.MutablePredicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class NonConflictingFDsAnalyzerTest {

    @Nested
    class SingleTGDAndSingleFDisConflictTests {

        @Nested
        class ArgumentsTests {
            @Test
            void shouldThrowIllegalArgumentException_whenTGDIsNull() {
                NonConflictingFDsAnalyzer analyzer = new NonConflictingFDsAnalyzer();
                FunctionalDependency fooFD = createFooFD();
                assertThatThrownBy(() -> analyzer.isConflicting(null,
                        fooFD))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void shouldThrowIllegalArgumentException_whenFDIsNull() {
                NonConflictingFDsAnalyzer analyzer = new NonConflictingFDsAnalyzer();
                TGD fooTGD = createFooTGD();
                assertThatThrownBy(() -> analyzer.isConflicting(fooTGD,
                        null))
                        .isInstanceOf(IllegalArgumentException.class);
            }


        }

        @Nested
        class SingleHeadedTGDTests {
            @Test
            void shouldIdentifyNonConflicting_whenFDAffectsDifferentPredicate() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age)");
                Predicate personPred = new Predicate("TotallyDifferentPredicate", 2);
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isFalse();
            }

            @Test
            void shouldIdentifyNonConflicting_whenFDIsNonConflictingKey() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isFalse();
            }

            @Test
            void shouldIdentifyNonConflicting_whenFDIsNonConflictingKey_andKeyHasSeveralPositions() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, surname, deptName) -> Person(name, surname, age)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0, 1));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isFalse();
            }

            @Test
            void shouldIdentifyConflicting_whenFDIsNonKey() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, city, state)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency nonKeyFD = new FunctionalDependency(personPred, Set.of(1), Set.of(2));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, nonKeyFD);

                assertThat(isConflicting).isTrue();
            }

            public static Stream<Arguments> provideConflictingTGD() {
                return Stream.of(
                        Arguments.of("TGD contains repeated existential variable",
                                "WorksIn(name, deptName) -> Person(name, age, x, x)"),
                        Arguments.of("TGD propagates values for key and some determined position",
                                "Child(name, age) -> Person(name, age)"),
                        Arguments.of("TGD contains constant in head",
                                "Child(name, age) -> Person(name, 18)")
                );
            }

            @ParameterizedTest(name = "Test case {0}")
            @MethodSource("provideConflictingTGD")
            void shouldIdentifyConflicting_whenFDIsKey_butConflictsWithTGD(@SuppressWarnings("unused") String title, String conflicting) {
                /*
                 * The paper  "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for
                 * New Applications" published in 2010 25th Annual IEEE Symposium on Logic in Computer Science
                 * does not clarify whether a TGD with constants in the head can get in conflict with a key
                 * dependency.
                 * <br>
                 * However, it is easy to see that key dependency cannot be "separated" (in the sense of the paper)
                 * with a TGD involving constants because the TGD might violate the EGD when fired.
                 * <br>
                 * E.g. Suppose the TGD:
                 * "WorksIn(name, deptName) -> Person(name, 18)"
                 * with an initial database:
                 * "WorksIn(John, Marketing), Person(John, 20)"
                 * and key dependency:
                 * "Person is identified by name"
                 * <br>
                 * In such case, there is no direct violation of the key dependency, but there is a violation of the
                 * key dependency when firing the TGD.
                 * <br>
                 * Hence, the key dependency is not separable from the TGD, and since non-conflicting guarantees
                 * separability, non-conflicting cannot deal with constants.
                 */
                TGD tgd = TGDMother.createTGD(conflicting);
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isTrue();
            }

        }

        @Nested
        class GeneralTGDTests {
            @Test
            void shouldIdentifyConflicting_whenFDIsKey_existentialVarsAreRepeatedAlongTwoAtoms() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age), Age(age)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isTrue();
            }

            @Test
            void shouldIdentifyConflicting_whenOneHeadOfTheTGDIsConflicting() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age), Person(name, name)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isTrue();
            }

            @Test
            void shouldIdentifyNonConflicting_whenAllHeadsOfTGDAreNotConflicting() {
                TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age), Child(name, name)");
                Predicate personPred = tgd.getHead().get(0).getPredicate();
                FunctionalDependency fd = createKeyDependency(personPred, Set.of(0));

                NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
                boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

                assertThat(isConflicting).isFalse();
            }
        }
    }

    @Nested
    class ListOfTGDAndListOfFDisConflictTest {
        @Nested
        class ArgumentsTests {
            @Test
            void shouldThrowIllegalArgumentException_whenTGDListIsNull() {
                NonConflictingFDsAnalyzer analyzer = new NonConflictingFDsAnalyzer();
                List<FunctionalDependency> fooFDs = List.of(createFooFD());

                assertThatThrownBy(() -> analyzer.isConflicting(null,
                        fooFDs))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void shouldThrowIllegalArgumentException_whenFDListIsNull() {
                NonConflictingFDsAnalyzer analyzer = new NonConflictingFDsAnalyzer();
                List<TGD> fooTGDs = List.of(createFooTGD());
                assertThatThrownBy(() -> analyzer.isConflicting(fooTGDs,
                        null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        void shouldIdentifyNonConflicting_whenNoTGD_conflictsWithNoFD() {
            DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept) -> Dept(dept, year)
                    WorksIn(name, dept) -> Person(name, age)
                    """);
            Predicate personPred = schema.getPredicateByName("Person");
            Predicate deptPred = schema.getPredicateByName("Dept");
            FunctionalDependency fd1 = createKeyDependency(personPred, Set.of(0));
            FunctionalDependency fd2 = createKeyDependency(deptPred, Set.of(0));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(schema.getAllTGDs(), List.of(fd1, fd2));

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyNonConflicting_whenTGDList_isEmpty() {
            Predicate personPred = new MutablePredicate("Person", 2);
            Predicate deptPred = new MutablePredicate("Dept", 2);
            FunctionalDependency fd1 = createKeyDependency(personPred, Set.of(0));
            FunctionalDependency fd2 = createKeyDependency(deptPred, Set.of(0));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(List.of(), List.of(fd1, fd2));

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyNonConflicting_whenFDList_isEmpty() {
            DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept) -> Dept(dept, year)
                    WorksIn(name, dept) -> Person(name, age)
                    """);

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(schema.getAllTGDs(), List.of());

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyConflicting_whenSomeTGD_conflictsWithSomeFD() {
            DependencySchema schema = DependencySchemaMother.buildDependencySchema("""
                    WorksIn(name, dept) -> Dept(dept, year)
                    WorksIn(name, dept) -> Person(name, age)
                    Child(name, age) -> Person(name, age)
                    """);
            Predicate personPred = schema.getPredicateByName("Person");
            Predicate deptPred = schema.getPredicateByName("Dept");
            FunctionalDependency fd1 = createKeyDependency(personPred, Set.of(0));
            FunctionalDependency fd2 = createKeyDependency(deptPred, Set.of(0));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(schema.getAllTGDs(), List.of(fd1, fd2));

            assertThat(isConflicting).isTrue();
        }
    }

    private static TGD createFooTGD() {
        return TGDMother.createTGD("P(x) -> Q(x)");
    }

    private static FunctionalDependency createFooFD() {
        return new FunctionalDependency(new MutablePredicate("P", 2), Set.of(0), Set.of(1));
    }

    private static FunctionalDependency createKeyDependency(Predicate personPred, Set<Integer> keyPositions) {
        Set<Integer> determinedPositions = new LinkedHashSet<>();
        for (int position = 0; position < personPred.getArity(); ++position) {
            if (!keyPositions.contains(position)) {
                determinedPositions.add(position);
            }
        }
        return new FunctionalDependency(personPred, keyPositions, determinedPositions);
    }
}