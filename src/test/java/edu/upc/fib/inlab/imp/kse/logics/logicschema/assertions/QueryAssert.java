package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Query;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class QueryAssert extends AbstractAssert<QueryAssert, Query> {

    public QueryAssert(Query query) {
        super(query, QueryAssert.class);
    }

    public static QueryAssert assertThat(Query actual) {
        return new QueryAssert(actual);
    }

    @SuppressWarnings("unused")
    public ImmutableTermListAssert head() {
        return ImmutableTermListAssert.assertThat(actual.getHeadTerms());
    }

    public ImmutableLiteralsListAssert body() {
        return ImmutableLiteralsListAssert.assertThat(actual.getBody());
    }

    @SuppressWarnings("unused")
    public QueryAssert headVariablesAppearInBody() {
        Assertions.assertThat(actual.getBody().getUsedVariables())
                .containsAll(actual.getHeadTerms().getUsedVariables());
        return this;
    }

    @SuppressWarnings("unused")
    public QueryAssert hasEmptyHead() {
        Assertions.assertThat(actual.getHeadTerms()).isEmpty();
        return this;
    }

    @SuppressWarnings("unused")
    public QueryAssert hasNonEmptyHead() {
        Assertions.assertThat(actual.getHeadTerms()).isNotEmpty();
        return this;
    }

    @SuppressWarnings("unused")
    public QueryAssert hasBodySize(int expected) {
        Assertions.assertThat(actual.getBody()).hasSize(expected);
        return this;
    }

    public QueryAssert isIsomorphicTo(Query expected) {
        ImmutableLiteralsListAssert.assertThat(expected.getBody())
                .isIsomorphicTo(expected.getBody());
        return this;
    }

}
