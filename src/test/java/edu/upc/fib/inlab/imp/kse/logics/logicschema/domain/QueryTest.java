package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.QueryAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {

    @Nested
    class IsConjunctiveQuery {

        @Test
        void should_ReturnTrue_When_QueryIsConjunctive_withoutTerms() {
            Query query = QueryMother.createBooleanConjunctiveQuery("P(x), Q(x)");
            assertThat(query.isConjunctiveQuery()).isTrue();
        }

        @Test
        void should_ReturnTrue_When_QueryIsConjunctive_usingQueryMother_createQuery() {
            Query query = QueryMother.createConjunctiveQuery(List.of("x"), "P(x), Q(x)");
            assertThat(query.isConjunctiveQuery()).isTrue();
        }

        @Test
        void should_ReturnFalse_When_QueryContainsNegation() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), not(Q(x))");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }

        @Test
        void should_ReturnFalse_When_QueryContainsBuiltinLiteral() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), x > 0");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }

        @Test
        void should_ReturnFalse_When_QueryContainsDerivedLiteral() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)", "R(x) :- Q(x)");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }

        @Test
        void test() {
            Query query1 = QueryMother.createQuery(List.of("x"), "P(x), R(x)", "R(x) :- Q(x)");
            Query query2 = QueryMother.createQuery(List.of("y"), "P(y), R(y)", "R(y) :- Q(y)");
            QueryAssert.assertThat(query1).isIsomorphicTo(query2);
        }
    }

}
