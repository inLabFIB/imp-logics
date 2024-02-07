package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateOutsideSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivedPredicateMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableAtomListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.QueryMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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

//    @Nested
//    class RetrievePredicateTests {
//        @Test
//        void should_retrievePredicate_WhenGivingTheirName() {
//            String predicateName = "p";
//            Predicate p = new MutablePredicate(predicateName, 1);
//            LogicSchema logicSchema = new LogicSchema(Set.of(p), Set.of());
//            assertThat(logicSchema.getPredicateByName(predicateName)).isSameAs(p);
//        }
//
//        @Test
//        void should_throwException_WhenRetrievingNonExistentPredicate() {
//            LogicSchema logicSchema = new LogicSchema(Set.of(), Set.of());
//            assertThatThrownBy(() -> logicSchema.getPredicateByName("P"));
//        }
//    }

    //TODO: add tests!

}