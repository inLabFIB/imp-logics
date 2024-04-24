package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.PredicatePosition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

class WeaklyGuardedCheckerTest {

    @Nested
    class AffectedPositionsTests {

        @Test
        void shouldReturnPredicatePosition_whenItContainsExistentialVariable() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x, y)
                                                                                                     """);

            Set<PredicatePosition> affectedPositions = WeaklyGuardedChecker.getAffectedPositions(dependencySchema);

            Assertions.assertThat(affectedPositions)
                    .hasSize(1)
                    .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1);
        }

        @Test
        void shouldReturnPredicatePosition_whenItContainsUniversalVar_butPropagatesExistentialVars() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x, y)
                                                                                                     r(x, y) -> q(y)
                                                                                                     """);

            Set<PredicatePosition> affectedPositions = WeaklyGuardedChecker.getAffectedPositions(dependencySchema);

            Assertions.assertThat(affectedPositions)
                    .hasSize(2)
                    .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1)
                    .anyMatch(p -> p.getPredicateName().equals("q") && p.position() == 0);
        }

        @Test
        void shouldNotReturnPredicatePosition_whenPropagatesExistentialVars_butJoinsNonAffectedPositions() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x, y)
                                                                                                     r(x, y), s(y) -> q(y)
                                                                                                     """);

            Set<PredicatePosition> affectedPositions = WeaklyGuardedChecker.getAffectedPositions(dependencySchema);

            Assertions.assertThat(affectedPositions)
                    .hasSize(1)
                    .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1);
        }

        @Test
        void shouldReturnPredicatePosition_whenExistentialVariable_AppearsTwice_inAffectedPositions() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x, y)
                                                                                                     t() -> s(y)
                                                                                                     r(x, y), s(y) -> q(y)
                                                                                                     """);

            Set<PredicatePosition> affectedPositions = WeaklyGuardedChecker.getAffectedPositions(dependencySchema);

            Assertions.assertThat(affectedPositions)
                    .hasSize(3)
                    .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1)
                    .anyMatch(p -> p.getPredicateName().equals("s") && p.position() == 0)
                    .anyMatch(p -> p.getPredicateName().equals("q") && p.position() == 0);
        }

        @Test
        void shouldReturnPredicatePosition_whenPropagatingThroughSeveralRules() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x, y)
                                                                                                     p(x) -> s(x, y)
                                                                                                     r(x, y), s(x,y) -> q(y)
                                                                                                     q(y) -> t(y, u)
                                                                                                     """);

            Set<PredicatePosition> affectedPositions = WeaklyGuardedChecker.getAffectedPositions(dependencySchema);

            Assertions.assertThat(affectedPositions)
                    .hasSize(5)
                    .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1)
                    .anyMatch(p -> p.getPredicateName().equals("s") && p.position() == 1)
                    .anyMatch(p -> p.getPredicateName().equals("q") && p.position() == 0)
                    .anyMatch(p -> p.getPredicateName().equals("t") && p.position() == 0)
                    .anyMatch(p -> p.getPredicateName().equals("t") && p.position() == 1);
        }
    }

    @Nested
    class WeaklyGuardedTGDTests {
        @Test
        void shouldReturnWeaklyGuarded_whenAffectedPositionsIsEmpty() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("r(x,y), s(u, v) -> t(x)");
            TGD tgd = dependencySchema.getAllTGDs().get(0);

            boolean isWeaklyGuarded = WeaklyGuardedChecker.isWeaklyGuarded(tgd, Set.of());

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnWeaklyGuarded_whenTGDIsGuardedButHaveNoUniversalVariable() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("p() -> r(x)");
            TGD tgd = dependencySchema.getAllTGDs().get(0);
            Predicate rPredicate = dependencySchema.getPredicateByName("r");
            Set<PredicatePosition> affectedPositions = Set.of(new PredicatePosition(rPredicate, 0));

            boolean isWeaklyGuarded = WeaklyGuardedChecker.isWeaklyGuarded(tgd, affectedPositions);

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnWeaklyGuarded_whenTGDIsGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("r(x,y), s(x) -> t(x,w)");
            TGD tgd = dependencySchema.getAllTGDs().get(0);
            Predicate tPredicate = dependencySchema.getPredicateByName("t");
            Set<PredicatePosition> affectedPositions = Set.of(new PredicatePosition(tPredicate, 1));

            boolean isWeaklyGuarded = WeaklyGuardedChecker.isWeaklyGuarded(tgd, affectedPositions);

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnWeaklyGuarded_whenTGDIsWeaklyGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     r(x,a), s(y,a) -> t(a)
                                                                                                     """);
            TGD tgd = dependencySchema.getAllTGDs().get(0);
            Predicate sPredicate = dependencySchema.getPredicateByName("s");
            Predicate rPredicate = dependencySchema.getPredicateByName("r");

            Set<PredicatePosition> affectedPositions = Set.of(new PredicatePosition(sPredicate, 1),
                                                              new PredicatePosition(rPredicate, 1));


            boolean isWeaklyGuarded = WeaklyGuardedChecker.isWeaklyGuarded(tgd, affectedPositions);

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnNonWeaklyGuarded_whenTGDIsNotWeaklyGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     r(x,a1), s(y,a2) -> t(a)
                                                                                                     """);
            TGD tgd = dependencySchema.getAllTGDs().get(0);
            Predicate sPredicate = dependencySchema.getPredicateByName("s");
            Predicate rPredicate = dependencySchema.getPredicateByName("r");

            Set<PredicatePosition> affectedPositions = Set.of(new PredicatePosition(sPredicate, 1),
                                                              new PredicatePosition(rPredicate, 1));


            boolean isWeaklyGuarded = WeaklyGuardedChecker.isWeaklyGuarded(tgd, affectedPositions);

            Assertions.assertThat(isWeaklyGuarded).isFalse();
        }

    }

    @Nested
    class WeaklyGuardedSchemaTests {
        @Test
        void shouldReturnTrue_whenCheckingIfWeaklyGuarded_withEmptySchema() {
            DependencySchema dependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

            boolean isGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);

            Assertions.assertThat(isGuarded).isTrue();
        }

        @Test
        void shouldReturnWeaklyGuarded_whenDependenciesAreGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p() -> q()
                                                                                                     r(x,y), s(x) -> t(x,w)
                                                                                                     """);

            boolean isWeaklyGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnWeaklyGuarded_whenAllDependenciesAreWeaklyGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x,a)
                                                                                                     p(x) -> s(x,a)
                                                                                                     r(x,a), s(y,a) -> t(a)
                                                                                                     """);

            boolean isWeaklyGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);
            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Test
        void shouldReturnNotWeaklyGuarded_whenSomeDependencyIsNotWeaklyGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                     p(x) -> r(x,a)
                                                                                                     p(x) -> s(x,a)
                                                                                                     r(x,a), s(y,a) -> t(a)
                                                                                                     r(x,a1), s(y,a2) -> t(a1)
                                                                                                     """);

            boolean isWeaklyGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);
            Assertions.assertThat(isWeaklyGuarded).isFalse();
        }

        @Nested
        class SchemaWithEGDsTest {
            @Test
            void shouldReturnTrue_whenEGDsAreNonConflicting_withWeaklyGuardedTGDs() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                         WorksIn(name, dept), Dept(dept) -> Person(name, age)
                                                                                                         Person(name, age), Person(name, age2) -> age = age2
                                                                                                         """);

                boolean isGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);

                Assertions.assertThat(isGuarded).isTrue();
            }

            @Test
            void shouldReturnFalse_whenEGDsAreConflicting_withWeaklyGuardedTGDs() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                                                                                                         WorksIn(name, dept), Dept(dept) -> Person(name, age)
                                                                                                         Child(name, age), Person(name, age2) -> age = age2
                                                                                                         """);

                boolean isGuarded = new WeaklyGuardedChecker().isWeaklyGuarded(dependencySchema);

                Assertions.assertThat(isGuarded).isFalse();
            }
        }
    }


}