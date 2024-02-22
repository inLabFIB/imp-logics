package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.ArityMismatch;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.QueryMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MutablePredicateTest {

    @Test
    void should_ThrowException_WhenCreatingMutablePredicate_WithNullDefinitionRules() {
        assertThatThrownBy(() -> new MutablePredicate("p", 1, null)).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    void should_ThrowException_WhenCreatingMutablePredicate_WithQueriesNotMatchInArity() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        assertThatThrownBy(() -> new MutablePredicate("p", 0, List.of(definitionRule))).isInstanceOf(
                ArityMismatch.class
        );
    }

    @Test
    void should_createMutablePredicate_WithEmptyDefinitionRules() {
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1);
        assertThat(mutablePredicate.getDerivationRules()).isEmpty();
    }

    @Test
    void should_MakeDefinitionRulesImmutable_WhenCreatingMutablePredicate() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        List<Query> definitionRules = new LinkedList<>();
        definitionRules.add(definitionRule);
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, definitionRules);
        assertThat(mutablePredicate.getDerivationRules()).isUnmodifiable();
    }

    @Test
    void should_ReturnDefinitionRulesImmutable_WhenAddDefinitionRule() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        List<Query> definitionRules = new LinkedList<>();
        definitionRules.add(definitionRule1);
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, definitionRules);

        Query definitionRule2 = QueryMother.createTrivialQuery(1, "q");
        mutablePredicate.addDerivationRule(definitionRule2);

        assertThat(mutablePredicate.getDerivationRules()).hasSize(2);
        assertThat(mutablePredicate.getDerivationRules()).isUnmodifiable();
    }

    @Test
    void should_ReturnDerivationRule_WithThisPredicateInHead_WhenAddDefinitionRule() {
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1);
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        mutablePredicate.addDerivationRule(definitionRule);
        assertThat(mutablePredicate.getDerivationRules()).allSatisfy(dr -> assertThat(dr.getHead().getPredicate()).isSameAs(mutablePredicate));
    }


    @Test
    void should_ReturnOneDerivationRule_ForEachDefinedQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Query definitionRule2 = QueryMother.createTrivialQuery(1, "q");
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, List.of(definitionRule1, definitionRule2));
        assertThat(mutablePredicate.getDerivationRules()).hasSize(2);
    }

    @Test
    void should_ReturnDerivationRule_WithThisPredicateInHead() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, List.of(definitionRule1));
        assertThat(mutablePredicate.getFirstDerivationRule().getHead().getPredicate()).isSameAs(mutablePredicate);
    }

    @Test
    void should_ReturnDerivationRule_WithTheHeadTermsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, List.of(definitionRule));
        assertThat(mutablePredicate.getFirstDerivationRule().getHeadTerms()).containsExactlyElementsOf(definitionRule.getHeadTerms());
    }

    @Test
    void should_ReturnDerivationRule_WithTheBodyLiteralsDefinedInTheQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, List.of(definitionRule1));
        assertThat(mutablePredicate.getFirstDerivationRule().getBody()).containsExactlyElementsOf(definitionRule1.getBody());
    }

    @Test
    void should_BeDerived_WhenMutablePredicateHaveDerivationRules() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1, List.of(definitionRule));
        assertThat(mutablePredicate.isDerived()).isTrue();
    }

    @Test
    void should_NotBeDerived_WhenMutablePredicateHaveNotDerivationRules() {
        MutablePredicate mutablePredicate = new MutablePredicate("p", 1);
        assertThat(mutablePredicate.isDerived()).isFalse();
    }

}
