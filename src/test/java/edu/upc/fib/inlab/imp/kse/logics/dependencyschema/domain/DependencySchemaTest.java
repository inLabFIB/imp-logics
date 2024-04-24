package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateIsNotDerivedException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateNotFoundException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateOutsideSchemaException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateNameException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.DerivedPredicateMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.ImmutableAtomListMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.PredicateAssert.assertThat;
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
                    .isInstanceOf(RepeatedPredicateNameException.class);
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
                    .isInstanceOf(PredicateOutsideSchemaException.class);
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
                    .isInstanceOf(PredicateOutsideSchemaException.class);
        }

        @Test
        void should_throwException_whenCreatingADependencySchema_withADerivedPredicate_usingPredicateNotFromSchema() {
            Query query = QueryMother.createTrivialQuery(1, "predicateNotInSchemaName");
            Predicate derivedPredicate = new MutablePredicate("p", 1, List.of(query));
            Set<Predicate> predicates = Set.of(derivedPredicate);
            Set<Dependency> dependencies = Set.of();

            assertThatThrownBy(() -> new DependencySchema(predicates, dependencies))
                    .isInstanceOf(PredicateOutsideSchemaException.class);
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
                    .isInstanceOf(PredicateNotFoundException.class);
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
                    .isInstanceOf(PredicateNotFoundException.class);
        }

        @Test
        void should_throwException_WhenRetrievingDerivationRules_WithNonDerivedPredicateName() {
            String basePredicateName = "p";
            Predicate basePredicate = new MutablePredicate(basePredicateName, 2);
            DependencySchema dependencySchema = new DependencySchema(Set.of(basePredicate), Set.of());
            assertThatThrownBy(() -> dependencySchema.getDerivationRulesByPredicateName(basePredicateName))
                    .isInstanceOf(PredicateIsNotDerivedException.class);
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


}