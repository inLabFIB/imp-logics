package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.mothers.DependencySchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateIsNotDerived;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateOutsideSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivedPredicateMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableAtomListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.QueryMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.PredicateAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencySchemaTest {

    @Nested
    class CreationTests {
        @Test
        void should_throwException_whenCreatingADependencySchema_withRepeatedPredicateNames() {
            Set<Predicate> predicates = Set.of(
                    new MutablePredicate("p", 1),
                    new MutablePredicate("p", 1)
            );
            Set<Dependency> dependencies = Set.of();

            assertThatThrownBy(() -> new DependencySchema(predicates, dependencies))
                    .isInstanceOf(RepeatedPredicateName.class);
        }

        @Test
        void should_throwException_whenCreatingADependencySchema_withADependency_usingPredicateNotFromSchema_inBody() {
            Predicate Q = new MutablePredicate("Q", 0);
            Set<Predicate> predicates = Set.of();
            Set<Dependency> dependencies = Set.of(
                    new TGD(
                            ImmutableLiteralsListMother.create("P(x)"),
                            List.of(new Atom(Q, List.of()))
                    )
            );

            assertThatThrownBy(() -> new DependencySchema(predicates, dependencies))
                    .isInstanceOf(PredicateOutsideSchema.class);
        }

        @Test
        void should_throwException_whenCreatingADependencySchema_withADependency_usingPredicateNotFromSchema_inHead() {
            Predicate Q = new MutablePredicate("Q", 0);
            Set<Predicate> predicates = Set.of();
            Set<Dependency> dependencies = Set.of(
                    new TGD(
                            List.of(new OrdinaryLiteral(new Atom(Q, List.of()))),
                            ImmutableAtomListMother.create("P(x)")
                    )
            );

            assertThatThrownBy(() -> new DependencySchema(predicates, dependencies))
                    .isInstanceOf(PredicateOutsideSchema.class);
        }

        @Test
        void should_throwException_whenCreatingADependencySchema_withADerivedPredicate_usingPredicateNotFromSchema() {
            Query query = QueryMother.createTrivialQuery(1, "predicateNotInSchemaName");
            Predicate derivedPredicate = new MutablePredicate("p", 1, List.of(query));
            Set<Predicate> predicates = Set.of(derivedPredicate);
            Set<Dependency> dependencies = Set.of();

            assertThatThrownBy(() -> new DependencySchema(predicates, dependencies))
                    .isInstanceOf(PredicateOutsideSchema.class);
        }

        @Test
        void should_notThrowException_whenCreatingADependencySchema_bringingFirstDerivedPredicates_andThenBasePredicates() {
            Predicate basePredicate = new MutablePredicate("q", 1);
            String derivedPredicateName = "p";
            Predicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate));
            Set<Predicate> predicates = Set.of(derivedPredicate, basePredicate);
            Set<Dependency> dependencies = Set.of();

            assertThatNoException().isThrownBy(() -> new DependencySchema(predicates, dependencies));
        }
    }

    @Nested
    class RetrievePredicateTests {
        @Test
        void should_retrievePredicate_WhenGivingTheirName() {
            String predicateName = "p";
            Predicate p = new MutablePredicate(predicateName, 1);
            DependencySchema dependencySchema = new DependencySchema(Set.of(p), Set.of());
            assertThat(dependencySchema.getPredicateByName(predicateName)).isSameAs(p);
        }

        @Test
        void should_throwException_WhenRetrievingNonExistentPredicate() {
            DependencySchema dependencySchema = new DependencySchema(Set.of(), Set.of());
            assertThatThrownBy(() -> dependencySchema.getPredicateByName("p"))
                    .isInstanceOf(PredicateNotExists.class);
        }
    }

    @Nested
    class RetrieveDependenciesTests {

        @Test
        void should_retrieveEmptySet_whenNoDependencyIsPresent() {
            DependencySchema dependencySchema = new DependencySchema(Set.of(), Set.of());

            Assertions.assertThat(dependencySchema.getAllDependencies()).isEmpty();
        }

        @Test
        void should_retrieveDependencies_whenFound() {
            Predicate P = new Predicate("P", 0);
            Dependency dependency = new TGD(
                    List.of(new BooleanBuiltInLiteral(true)),
                    List.of(new Atom(P, List.of()))
            );
            DependencySchema dependencySchema = new DependencySchema(Set.of(P), Set.of(dependency));

            Assertions.assertThat(dependencySchema.getAllDependencies())
                    .containsExactlyInAnyOrder(dependency);
        }
    }

    @Nested
    class RetrieveDerivationRuleTests {
        @Test
        void should_retrieveDerivationRules_WhenGivingTheirPredicateName() {
            MutablePredicate basePredicate1 = new MutablePredicate("q", 1);
            MutablePredicate basePredicate2 = new MutablePredicate("r", 1);
            String derivedPredicateName = "p";
            MutablePredicate derivedPredicate = DerivedPredicateMother.createTrivialDerivedPredicate(derivedPredicateName, 1, List.of(basePredicate1, basePredicate2));
            List<DerivationRule> derivationRules = derivedPredicate.getDerivationRules();
            DependencySchema dependencySchema = new DependencySchema(Set.of(basePredicate1, basePredicate2, derivedPredicate), Set.of());

            Assertions.assertThat(dependencySchema.getDerivationRulesByPredicateName(derivedPredicateName))
                    .containsExactlyInAnyOrderElementsOf(derivationRules);
        }

        @Test
        void should_throwException_WhenRetrievingDerivationRules_WithNonExistentPredicateName() {
            DependencySchema dependencySchema = new DependencySchema(Set.of(), Set.of());
            assertThatThrownBy(() -> dependencySchema.getDerivationRulesByPredicateName("nonExistentPredicateName"))
                    .isInstanceOf(PredicateNotExists.class);
        }

        @Test
        void should_throwException_WhenRetrievingDerivationRules_WithNonDerivedPredicateName() {
            String basePredicateName = "p";
            Predicate basePredicate = new MutablePredicate(basePredicateName, 2);
            DependencySchema dependencySchema = new DependencySchema(Set.of(basePredicate), Set.of());
            assertThatThrownBy(() -> dependencySchema.getDerivationRulesByPredicateName(basePredicateName))
                    .isInstanceOf(PredicateIsNotDerived.class);
        }
    }

    @Nested
    class EmptinessTests {
        @Test
        void should_beEmpty_whenDependencySchemaIsEmpty() {
            DependencySchema dependencySchema = new DependencySchema(Collections.emptySet(), Collections.emptySet());
            Assertions.assertThat(dependencySchema.isEmpty()).isTrue();
        }

        @Test
        void should_beNotEmpty_whenDependencySchemaOnlyContainsBasePredicates() {
            Set<Predicate> predicates = Set.of(new Predicate("P", 2));
            DependencySchema dependencySchema = new DependencySchema(predicates, Collections.emptySet());
            Assertions.assertThat(dependencySchema.isEmpty()).isFalse();
        }

        @Test
        void should_beNotEmpty_whenDependencySchemaContainsDependency() {
            Predicate P = new Predicate("P", 0);
            DependencySchema dependencySchema = new DependencySchema(
                    Set.of(P),
                    Set.of(new TGD(
                            List.of(new BooleanBuiltInLiteral(true)),
                            List.of(new Atom(P, List.of()))
                    ))
            );
            Assertions.assertThat(dependencySchema.isEmpty()).isFalse();
        }
    }

    @Nested
    class LinearTests {

        @Test
        void shouldReturnTrue_whenCheckingIfLinear_withEmptySchema() {
            DependencySchema dependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

            boolean isLinear = dependencySchema.isLinear();

            Assertions.assertThat(isLinear).isTrue();
        }

        @Test
        void shouldReturnTrue_whenCheckingIfLinear_withSchemaWithLinearTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p() -> q()
                    r() -> s(), t()
                    """);
            Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(0)).isLinear()).isTrue();
            Assertions.assertThat(((TGD) dependencySchema.getAllDependencies().stream().toList().get(1)).isLinear()).isTrue();

            boolean isLinear = dependencySchema.isLinear();

            Assertions.assertThat(isLinear).isTrue();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfLinear_withSchemaWithNonLinearTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(), q() -> r()
                    p() -> r()
                    """);

            boolean isLinear = dependencySchema.isLinear();

            Assertions.assertThat(isLinear).isFalse();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfLinear_withSchemaWithEGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p() -> r()
                    s(x) -> x = y
                    """);

            boolean isLinear = dependencySchema.isLinear();

            Assertions.assertThat(isLinear).isFalse();
        }
    }

    @Nested
    class GuardedTests {

        @Test
        void shouldReturnTrue_whenCheckingIfGuarded_withEmptySchema() {
            DependencySchema dependencySchema = DependencySchemaMother.buildEmptyDependencySchema();

            boolean isGuarded = dependencySchema.isGuarded();

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

            boolean isGuarded = dependencySchema.isGuarded();

            Assertions.assertThat(isGuarded).isTrue();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfGuarded_withSchemaWithNonLinearTGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(x), q(y) -> r(s)
                    """);

            boolean isGuarded = dependencySchema.isGuarded();

            Assertions.assertThat(isGuarded).isFalse();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfGuarded_withSchemaWithEGDs() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(x) -> r(x)
                    s(x) -> x = y
                    """);

            boolean isGuarded = dependencySchema.isGuarded();

            Assertions.assertThat(isGuarded).isFalse();
        }
    }

    @Nested
    class WeaklyGuardedTests {
        @Nested
        class AffectedPositionsTests {

            @Test
            void shouldReturnPredicatePosition_whenItContainsExistentialVariable() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                        p(x) -> r(x, y)
                        """);

                Set<PredicatePosition> affectedPositions = dependencySchema.getAffectedPositions();

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

                Set<PredicatePosition> affectedPositions = dependencySchema.getAffectedPositions();

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

                Set<PredicatePosition> affectedPositions = dependencySchema.getAffectedPositions();

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

                Set<PredicatePosition> affectedPositions = dependencySchema.getAffectedPositions();

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

                Set<PredicatePosition> affectedPositions = dependencySchema.getAffectedPositions();

                Assertions.assertThat(affectedPositions)
                        .hasSize(5)
                        .anyMatch(p -> p.getPredicateName().equals("r") && p.position() == 1)
                        .anyMatch(p -> p.getPredicateName().equals("s") && p.position() == 1)
                        .anyMatch(p -> p.getPredicateName().equals("q") && p.position() == 0)
                        .anyMatch(p -> p.getPredicateName().equals("t") && p.position() == 0)
                        .anyMatch(p -> p.getPredicateName().equals("t") && p.position() == 1);
            }
        }

        @Disabled("WIP - IMPL-588")
        @Nested
        class WeaklyGuardedTGDTests {

            @Test
            void shouldReturnWeaklyGuarded_whenTGDIsGuarded() {
                DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                        p() -> q()
                        r(x,y), s(x) -> t(x,w)
                        """);
                List<TGD> tgds = dependencySchema.getAllTGDs();

                boolean isWeaklyGuarded = dependencySchema.isWeaklyGuarded(tgds.get(1));

                Assertions.assertThat(isWeaklyGuarded).isTrue();
            }

            //TODO: add more tests

        }

        @Disabled("WIP - IMPL-588")
        @Test
        void shouldReturnWeaklyGuarded_whenDependenciesAreGuarded() {
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p() -> q()
                    r(x,y), s(x) -> t(x,w)
                    """);

            boolean isWeaklyGuarded = dependencySchema.isWeaklyGuarded();

            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Disabled("WIP - IMPL-588")
        @Test
        void shouldReturnWeaklyGuarded_whenAllDependenciesAreWeaklyGuarded() {
            //TODO: finish dependency schema
            DependencySchema dependencySchema = DependencySchemaMother.buildDependencySchema("""
                    p(x,y), q(y, z) -> r1(y, w1)
                    p(x,y), q(y, z) -> r2(y, w2)
                    r1(y, w1), t(w1, u), r2(y,w2) -> s1()
                    r1(y, w1), t(w1, u) -> s1()
                    """);

            boolean isWeaklyGuarded = dependencySchema.isWeaklyGuarded();
            Assertions.assertThat(isWeaklyGuarded).isTrue();
        }

        @Disabled("WIP - IMPL-588")
        @Test
        void shouldReturnNotWeaklyGuarded_whenSomeDependencyIsNotWeaklyGuarded() {

        }

        //TODO: write more tests
    }
}