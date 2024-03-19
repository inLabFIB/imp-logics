package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.QueryAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConjunctiveQuery;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.QueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConjunctiveQueriesParserTest {

    @Nested
    class SingleQueries {

        @Test
        void shouldParse_simpleQuery_withoutHeadVariables() {
            String queriesString = "() <- p(x)";

            ConjunctiveQueriesParser parser = new ConjunctiveQueriesParser();

            Set<ConjunctiveQuery> queries =  parser.parse(queriesString);

            assertThat(queries).hasSize(1);
            LogicSchemaAssertions.assertThat(queries.stream().toList().get(0))
                    .hasEmptyHead()
                    .hasBodySize(1);
        }

        @Test
        void shouldParse_simpleQuery_withHeadVariables() {
            String queriesString = "(x, y) <- p(x), q(y)";

            ConjunctiveQueriesParser parser = new ConjunctiveQueriesParser();

            Set<ConjunctiveQuery> queries =  parser.parse(queriesString);

            assertThat(queries).hasSize(1);
            LogicSchemaAssertions.assertThat(queries.stream().toList().get(0))
                    .hasNonEmptyHead()
                    .hasBodySize(2);
        }

    }

}