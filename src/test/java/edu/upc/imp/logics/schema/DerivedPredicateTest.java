package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.utils.QueryMother;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class DerivedPredicateTest {

    @Test
    public void should_ThrowException_WhenCreatingDerivedPredicate_WithNullDefinitionRules() {
        assertThatThrownBy(() -> new DerivedPredicate("p", new Arity(1), null)).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    public void should_ThrowException_WhenCreatingDerivedPredicate_WithEmptyDefinitionRules() {
        assertThatThrownBy(() -> new DerivedPredicate("p", new Arity(1), List.of())).isInstanceOf(
                IllegalArgumentException.class
        );
    }

    @Test
    public void should_ThrowException_WhenCreatingDerivedPredicate_WithQueriesNotMatchInArity() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        assertThatThrownBy(() -> new DerivedPredicate("p", new Arity(0), List.of(definitionRule))).isInstanceOf(
                ArityMismatch.class
        );
    }

    @Test
    public void should_MakeDefinitionRulesImmutable_WhenCreatingDerivedPredicate() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        List<Query> definitionRules = new LinkedList<>();
        definitionRules.add(definitionRule);
        DerivedPredicate derivedPredicate = new DerivedPredicate("p", new Arity(1), definitionRules);
        assertThat(derivedPredicate.getDerivationRules()).isUnmodifiable();
    }

    @Test
    public void should_ReturnOneDerivationRule_ForEachDefinedQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        Query definitionRule2 = QueryMother.createTrivialQuery(1, "q");
        DerivedPredicate derivedPredicate = new DerivedPredicate("p", new Arity(1), List.of(definitionRule1, definitionRule2));
        assertThat(derivedPredicate.getDerivationRules()).hasSize(2);
    }

    @Test
    public void should_ReturnDerivationRule_WithThisPredicateInHead() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        DerivedPredicate derivedPredicate = new DerivedPredicate("p", new Arity(1), List.of(definitionRule1));
        assertThat(derivedPredicate.getDerivationRules().get(0).getHead().getPredicate()).isSameAs(derivedPredicate);
    }

    @Test
    public void should_ReturnDerivationRule_WithTheHeadTermsDefinedInTheQuery() {
        Query definitionRule = QueryMother.createTrivialQuery(1, "p");
        DerivedPredicate derivedPredicate = new DerivedPredicate("p", new Arity(1), List.of(definitionRule));
        assertThat(derivedPredicate.getDerivationRules().get(0).getHead().getTerms()).containsExactlyElementsOf(definitionRule.getHeadTerms());
    }

    @Test
    public void should_ReturnDerivationRule_WithTheBodyLiteralsDefinedInTheQuery() {
        Query definitionRule1 = QueryMother.createTrivialQuery(1, "p");
        DerivedPredicate derivedPredicate = new DerivedPredicate("p", new Arity(1), List.of(definitionRule1));
        assertThat(derivedPredicate.getDerivationRules().get(0).getBody()).isSameAs(definitionRule1.getBody());
    }
}
