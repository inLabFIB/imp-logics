package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.QueryAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.junit.jupiter.api.Disabled;
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

    @Nested
    class Unfold {
        @Test
        void should_returnTheSameQuery_ifAllLiteralsAreBase() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)");
            List<Query> unfolded = query.unfold();
            assertThat(unfolded)
                    .hasSize(1)
                    .allSatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), R(x)"));
        }

        @Test
        void should_returnOneQuery_ifItDependsOnSingleDerivationRule() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)", "R(x) :- S(x, y)");
            List<Query> unfolded = query.unfold();
            assertThat(unfolded)
                    .hasSize(1)
                    .allSatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), S(x, y)"));
        }

        @Test
        void should_returnSeveralQueries_ifItQueryDependsOnSeveralDerivationRule() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)",
                                                  """
                                                          R(x) :- S(x, y)
                                                          R(x) :- T(x, z)
                                                          """);
            List<Query> unfolded = query.unfold();
            assertThat(unfolded)
                    .hasSize(2)
                    .anySatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), S(x, y)"))
                    .anySatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), T(x, z)"));
        }

        @Test
        void should_returnSeveralQueries_ifItDependsOnSeveralDerivationRule_recursively() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)",
                                                  """
                                                          R(x) :- S(x, y)
                                                          S(x, y) :- A(x, y)
                                                          R(x) :- T(x, z)
                                                          T(x, z) :- B(x, z)
                                                          """);
            List<Query> unfolded = query.unfold();
            assertThat(unfolded)
                    .hasSize(2)
                    .anySatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), A(x, y)"))
                    .anySatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(List.of("x"), "P(x), B(x, z)"));
        }

        @Disabled("WIP - Fix Bug in IsomorphismComparator with recursive Predicates - Issue IMPL-654")
        @Test
        void should_notUnfold_recursiveLiterals() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x, y)", "R(x, y) :- R(x, z), R(z, y)");
            List<Query> unfolded = query.unfold();
            assertThat(unfolded)
                    .hasSize(1)
                    .allSatisfy(q -> QueryAssert.assertThat(q).isIsomorphicTo(query));
        }
    }

}
