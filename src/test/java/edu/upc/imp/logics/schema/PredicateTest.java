package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.utils.QueryMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PredicateTest {

    @Test
    public void should_ThrowException_WhenCreatingPredicateWithNullName() {
        assertThatThrownBy(() -> new Predicate(null, new Arity(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingPredicateWithNullArity() {
        assertThatThrownBy(() -> new Predicate("P", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingPredicate_WithNullDefinitionRules() {
        assertThatThrownBy(() -> new Predicate("p", new Arity(1), null)).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    public void should_ThrowException_WhenCreatingPredicate_WithEmptyDefinitionRules() {
        assertThatThrownBy(() -> new Predicate("p", new Arity(1), List.of())).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    public void should_ThrowException_WhenCreatingPredicate_WithQueriesNotMatchInArity() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        assertThatThrownBy(() -> new Predicate("p", new Arity(0), List.of(definitionRule))).isInstanceOf(
                ArityMismatch.class
        );
    }

    @Test
    public void should_MakeDefinitionRulesImmutable_WhenCreatingPredicate() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        List<Query> definitionRules = new LinkedList<>();
        definitionRules.add(definitionRule);
        Predicate predicate = new Predicate("p", new Arity(1), definitionRules);
        assertThat(predicate.getDerivationRules()).isUnmodifiable();
    }

    @Test
    public void should_ReturnOneDerivationRule_ForEachDefinedQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Query definitionRule2 = QueryMother.createTrivialQuery(1, "q");
        Predicate predicate = new Predicate("p", new Arity(1), List.of(definitionRule1, definitionRule2));
        assertThat(predicate.getDerivationRules()).hasSize(2);
    }

    @Test
    public void should_ReturnDerivationRule_WithThisPredicateInHead() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", new Arity(1), List.of(definitionRule1));
        assertThat(predicate.getDerivationRules().get(0).getHead().getPredicate()).isSameAs(predicate);
    }

    @Test
    public void should_ReturnDerivationRule_WithTheHeadTermsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", new Arity(1), List.of(definitionRule));
        assertThat(predicate.getDerivationRules().get(0).getHead().getTerms()).containsExactlyElementsOf(definitionRule.getHeadTerms());
    }

    @Test
    public void should_ReturnDerivationRule_WithTheBodyLiteralsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", new Arity(1), List.of(definitionRule));
        assertThat(predicate.getDerivationRules().get(0).getBody()).isSameAs(definitionRule.getBody());
    }

    @Test
    public void should_ReturnDerived_WhenPredicateHasDerivationRule() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        Predicate predicate = new Predicate("p", new Arity(1), List.of(definitionRule));
        assertThat(predicate.isDerived()).isTrue();
    }

    @Test
    public void should_ReturnNotDerived_WhenPredicateHasNoDerivationRule() {
        Predicate predicate = new Predicate("p", new Arity(1));
        assertThat(predicate.isDerived()).isFalse();
    }

}
