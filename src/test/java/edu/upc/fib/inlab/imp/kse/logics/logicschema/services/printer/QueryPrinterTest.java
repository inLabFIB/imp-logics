package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Query;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.QueryMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryPrinterTest {
    @Test
    void should_print_BooleanConjunctiveQuery() {
        Query query = QueryMother.createBooleanConjunctiveQuery("P(E, D), Q()");
        String expectedString = "() :- P(E, D), Q()";

        String result = new QueryPrinter().print(query);

        assertThat(result).isEqualToIgnoringWhitespace(expectedString);
    }

    @Test
    void should_print_ConjunctiveQuery() {
        Query query = QueryMother.createConjunctiveQuery(List.of("E, D"), "P(E, D), Q()");
        String expectedString = "(E, D) :- P(E, D), Q()";

        String result = new QueryPrinter().print(query);

        assertThat(result).isEqualToIgnoringWhitespace(expectedString);
    }

    @Test
    void should_print_Query() {
        Query query = QueryMother.createQuery(List.of("E", "D"), "P(E, D), not(Q()), E < 5");
        String expectedString = "(E, D) :- P(E, D), not(Q()), E < 5";

        String result = new QueryPrinter().print(query);

        assertThat(result).isEqualToIgnoringWhitespace(expectedString);
    }

    @Test
    void should_print_BooleanQuery() {
        Query query = QueryMother.createBooleanQuery("P(E, D), not(Q()), E < 5");
        String expectedString = "() :- P(E, D), not(Q()), E < 5";

        String result = new QueryPrinter().print(query);

        assertThat(result).isEqualToIgnoringWhitespace(expectedString);
    }
}