package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.ArityMismatchException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.DerivedPredicateMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PredicateTest {

    @Test
    void should_ThrowException_WhenCreatingPredicate_WithNullName() {
        assertThatThrownBy(() -> new Predicate(null, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowException_WhenCreatingPredicate_WithEmptyName() {
        assertThatThrownBy(() -> new Predicate("", 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void should_CreatePredicate_WhenUsingPositiveOrZeroArity(int arity) {
        assertThatNoException().isThrownBy(() -> new Predicate("name", arity));
    }

    @Test
    void should_ThrowException_WhenCreatingPredicate_withNegativeArity() {
        assertThatThrownBy(() -> new Predicate("name", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowException_WhenCreatingPredicate_WithNullDefinitionRules() {
        assertThatThrownBy(() -> new Predicate("p", 1, null)).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    void should_ThrowException_WhenCreatingPredicate_WithEmptyDefinitionRules() {
        assertThatThrownBy(() -> new Predicate("p", 1, List.of())).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    void should_ThrowException_WhenCreatingPredicate_WithQueriesNotMatchInArity() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        assertThatThrownBy(() -> new Predicate("p", 0, List.of(definitionRule))).isInstanceOf(
                ArityMismatchException.class
        );
    }

    @Test
    void should_MakeDefinitionRulesImmutable_WhenCreatingPredicate() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        List<Query> definitionRules = new LinkedList<>();
        definitionRules.add(definitionRule);
        Predicate predicate = new Predicate("p", 1, definitionRules);
        assertThat(predicate.getDerivationRules()).isUnmodifiable();
    }

    @Test
    void should_ReturnOneDerivationRule_ForEachDefinedQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Query definitionRule2 = QueryMother.createTrivialQuery(1, "q");
        Predicate predicate = new Predicate("p", 1, List.of(definitionRule1, definitionRule2));
        assertThat(predicate.getDerivationRules()).hasSize(2);
    }

    @Test
    void should_ReturnDerivationRule_WithThisPredicateInHead() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", 1, List.of(definitionRule1));
        assertThat(predicate.getFirstDerivationRule().getHead().getPredicate()).isSameAs(predicate);
    }

    @Test
    void should_ReturnDerivationRule_WithTheHeadTermsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", 1, List.of(definitionRule));
        assertThat(predicate.getFirstDerivationRule().getHeadTerms()).containsExactlyElementsOf(definitionRule.getHeadTerms());
    }

    @Test
    void should_ReturnDerivationRule_WithTheBodyLiteralsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", 1, List.of(definitionRule));
        assertThat(predicate.getFirstDerivationRule().getBody()).containsExactlyElementsOf(definitionRule.getBody());
    }

    @Test
    void should_ReturnDerived_WhenPredicateHasDerivationRule() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", 1, List.of(definitionRule));
        assertThat(predicate.isDerived()).isTrue();
    }

    @Test
    void should_ReturnNotDerived_WhenPredicateHasNoDerivationRule() {
        Predicate predicate = new Predicate("p", 1);
        assertThat(predicate.isDerived()).isFalse();
    }

    @Nested
    class IsRecursive {
        @Test
        void should_returnFalse_whenPredicateIsBase() {
            Predicate predicate = new Predicate("P", 2);
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isFalse();
        }

        @Test
        void should_returnFalse_whenPredicateIsDerived_andNotRecursive() {
            Predicate predicate = DerivedPredicateMother.createDerivedPredicate("P", "P(x) :- R(x, y)");
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isFalse();
        }

        @Test
        void should_returnFalse_whenPredicateIsDerived_definedOverDerivedPredicates_butNotRecursive() {
            Predicate predicate = DerivedPredicateMother.createDerivedPredicate("P",
                    """
                            P(x) :- R(x, y)
                            R(x, y) :- S(x, y)
                            """);
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isFalse();
        }

        @Test
        void should_returnFalse_whenPredicateIsDerived_definedOverRecurisvePredicates() {
            Predicate predicate = DerivedPredicateMother.createDerivedPredicate("P",
                    """
                            P(x) :- R(x, y)
                            R(x, y) :- R(x, z), R(z, y)
                            """);
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isFalse();
        }

        @Test
        void should_returTrue_whenPredicateIsDirectlyRecursive() {
            Predicate predicate = DerivedPredicateMother.createDerivedPredicate("P",
                    """
                            P(x) :- P(y)
                            """);
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isTrue();
        }

        @Test
        void should_returTrue_whenPredicateIsRecursiveThroughSeveralRules() {
            Predicate predicate = DerivedPredicateMother.createDerivedPredicate("P",
                    """
                            P(x) :- R(y)
                            P(x) :- Rec(y)
                            R(y) :- S(y)
                            Rec(y) :- P(y)
                            """);
            boolean isRecursive = predicate.isRecursive();
            assertThat(isRecursive).isTrue();
        }
    }
}
