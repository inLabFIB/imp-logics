package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class QueryFactoryTest {

    @Test
    void should_ThrowException_WhenCreatingQueryWithNullTerms() {
        Literal l = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatThrownBy(() -> QueryFactory.createQuery(null, List.of(l)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowException_WhenCreatingQueryWithNullBody() {
        assertThatThrownBy(() -> QueryFactory.createQuery(List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_ThrowException_WhenCreatingQueryWithEmptyBody() {
        assertThatThrownBy(() -> QueryFactory.createQuery(List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_NotThrowException_WhenCreatingQueryWithEmptyTerms() {
        Literal l = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatNoException().isThrownBy(() -> QueryFactory.createQuery(List.of(), List.of(l)));
    }

    @Test
    void should_MakeHeadTermsImmutable_WhenCreatingQuery() {
        List<Term> terms = new LinkedList<>();
        terms.add(new Variable("x"));
        Query q = QueryFactory.createQuery(terms, List.of(LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"))));
        assertThat(q.getHeadTerms()).isUnmodifiable();
    }

    @Test
    void should_MakeBodyImmutable_WhenCreatingQuery_WithMutableListInput() {
        List<Literal> body = createMutableListOfLiterals();
        Query q = QueryFactory.createQuery(List.of(), body);
        assertThat(q.getBody()).isUnmodifiable();
    }

    private static List<Literal> createMutableListOfLiterals() {
        List<Literal> body = new LinkedList<>();
        body.add(LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x")));
        return body;
    }

    @Nested
    class CreateConjunctiveQuery {
        @Test
        void should_ReturnConjunctiveQuery_When_CreateConjunctiveQuery() {
            Query query = QueryFactory.createConjunctiveQuery(
                    List.of(new Variable("x")),
                    List.of(
                            LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")),
                            LiteralMother.createOrdinaryLiteralWithVariableNames("Q", List.of("x")))
            );
            assertThat(query).isInstanceOf(ConjunctiveQuery.class);
            assertThat(query.isConjunctiveQuery()).isTrue();
        }

        @Test
        void should_ReturnConjunctiveQuery_When_CreateQuery() {
            Query query = QueryFactory.createQuery(
                    List.of(new Variable("x")),
                    List.of(
                            LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")),
                            LiteralMother.createOrdinaryLiteralWithVariableNames("Q", List.of("x")))
            );
            assertThat(query).isInstanceOf(ConjunctiveQuery.class);
            assertThat(query.isConjunctiveQuery()).isTrue();
        }

    }

}
