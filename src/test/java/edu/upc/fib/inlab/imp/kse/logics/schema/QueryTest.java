package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.QueryMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class QueryTest {

    @Test
    public void should_ThrowException_WhenCreatingQueryWithNullTerms() {
        Literal l = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatThrownBy(() -> new Query(null, List.of(l)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingQueryWithNullBody() {
        assertThatThrownBy(() -> new Query(List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingQueryWithEmptyBody() {
        assertThatThrownBy(() -> new Query(List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_NotThrowException_WhenCreatingQueryWithEmptyTerms() {
        Literal l = LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"));
        assertThatNoException().isThrownBy(() -> new Query(List.of(), List.of(l)));
    }

    @Test
    public void should_MakeHeadTermsImmutable_WhenCreatingQuery() {
        List<Term> terms = new LinkedList<>();
        terms.add(new Variable("x"));
        Query q = new Query(terms, List.of(LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x"))));
        assertThat(q.getHeadTerms()).isUnmodifiable();
    }

    @Test
    public void should_MakeBodyImmutable_WhenCreatingQuery_WithMutableListInput() {
        List<Literal> body = createMutableListOfLiterals();
        Query q = new Query(List.of(), body);
        assertThat(q.getBody()).isUnmodifiable();
    }

    private static List<Literal> createMutableListOfLiterals() {
        List<Literal> body = new LinkedList<>();
        body.add(LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x")));
        return body;
    }

    @Nested
    class IsConjunctiveQuery {

        @Test
        public void should_ReturnTrue_When_QueryIsConjunctive() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), Q(x)");
            assertThat(query.isConjunctiveQuery()).isTrue();
        }

        @Test
        public void should_ReturnFalse_When_QueryContainsNegation() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), not(Q(x))");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }

        @Test
        public void should_ReturnFalse_When_QueryContainsBuiltinLiteral() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), x > 0");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }

        @Test
        public void should_ReturnFalse_When_QueryContainsDerivedLiteral() {
            Query query = QueryMother.createQuery(List.of("x"), "P(x), R(x)", "R(x) :- Q(x)");
            assertThat(query.isConjunctiveQuery()).isFalse();
        }
    }
}
