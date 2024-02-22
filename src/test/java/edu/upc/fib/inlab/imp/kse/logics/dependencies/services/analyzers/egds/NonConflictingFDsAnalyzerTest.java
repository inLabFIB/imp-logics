package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.mothers.TGDMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.MutablePredicate;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class NonConflictingFDsAnalyzerTest {

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

        private TGD createFooTGD() {
            return TGDMother.createTGD("P(x) -> Q(x)");
        }

        private static FunctionalDependency createFooFD() {
            return new FunctionalDependency(new MutablePredicate("P", 1), Set.of(0), Set.of(1));
        }
    }

    @Nested
    class SingleHeadedTGDTests {
        @Test
        void shouldIdentifyNonConflicting_whenFDAffectsDifferentPredicate() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age)");
            Predicate personPred = new Predicate("TotallyDifferentPredicate", 2);
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyNonConflicting_whenFDIsNonConflictingKey() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyNonConflicting_whenFDIsNonConflictingKey_andKeyHasSeveralPositions() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, surname, deptName) -> Person(name, surname, age)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0, 1), Set.of(2));

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

        @Test
        void shouldIdentifyConflicting_whenFDIsKey_existentialVarsAreRepeated() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age, x, x)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1, 2, 3));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isTrue();
        }

        @Test
        void shouldIdentifyConflicting_whenFDIsKey_andUniversalVarsPositionsAreStrictSuperSetOfKeyPositions() {
            TGD tgd = TGDMother.createTGD("Child(name, age) -> Person(name, age)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isTrue();
        }

        @Test
        void shouldIdentifyNotConflicting_whenFDIsKey_andTGDContainsConstants() {
            TGD tgd = TGDMother.createTGD("Child(name, age) -> Person(name, 18)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1)); //KD Person -> name

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isFalse();
        }

        @Test
        void shouldIdentifyConflicting_whenTGDContainsConstant() {
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

            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, 18)");
            Predicate personPred = new Predicate("Person", 2);
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

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
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1, 2, 3));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isTrue();
        }

        @Test
        void shouldIdentifyConflicting_whenOneHeadOfTheTGDIsConflicting() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age), Person(name, name)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isTrue();
        }

        @Test
        void shouldIdentifyNonConflicting_whenAllHeadsOfTGDAreNotConflicting() {
            TGD tgd = TGDMother.createTGD("WorksIn(name, deptName) -> Person(name, age), Child(name, name)");
            Predicate personPred = tgd.getHead().get(0).getPredicate();
            FunctionalDependency fd = new FunctionalDependency(personPred, Set.of(0), Set.of(1));

            NonConflictingFDsAnalyzer nonConflictingFDsAnalyzer = new NonConflictingFDsAnalyzer();
            boolean isConflicting = nonConflictingFDsAnalyzer.isConflicting(tgd, fd);

            assertThat(isConflicting).isFalse();
        }
    }
}