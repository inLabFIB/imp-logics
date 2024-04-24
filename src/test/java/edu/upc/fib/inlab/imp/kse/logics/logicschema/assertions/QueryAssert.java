package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Query;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism.IsomorphismComparator;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.printer.QueryPrinter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;

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

    public QueryAssert isIsomorphicTo(List<String> headTerms, String expectedString) {
        Query expectedQuery = QueryMother.createQuery(headTerms, expectedString);
        return isIsomorphicTo(expectedQuery);
    }

    public QueryAssert isIsomorphicTo(Query expected) {
        boolean result = new IsomorphismComparator(new IsomorphismOptions())
                .areIsomorphic(actual, expected);
        QueryPrinter queryPrinter = new QueryPrinter();
        Assertions.assertThat(result)
                .describedAs("Actual query '" + queryPrinter.print(actual) + "' is not isomorphic to\nExpected query '" + queryPrinter.print(expected) + "'")
                .isTrue();
        return this;
    }

}
