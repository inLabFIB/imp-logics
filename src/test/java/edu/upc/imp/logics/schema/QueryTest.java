package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.LiteralMother;
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
    public void should_MakeBodyImmutable_WhenCreatingQuery() {
        List<Literal> body = new LinkedList<>();
        body.add(LiteralMother.createOrdinaryLiteralWithVariableNames("p", List.of("x")));
        Query q = new Query(List.of(), body);
        assertThat(q.getBody()).isUnmodifiable();
    }
}
