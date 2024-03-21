package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.QueryAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateName;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.ImmutableAtomListMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class ConjunctiveQueriesParserTest {

    private final InstanceOfAssertFactory<Query, QueryAssert> instanceOfQueryAssert = new InstanceOfAssertFactory<>(Query.class, QueryAssert::assertThat);

    @Nested
    class SingleQueries {

        @Test
        void shouldParse_simpleQuery_withoutHeadVariables() {
            String queriesString = "() :- p(x)";

            ConjunctiveQueriesParser parser = new ConjunctiveQueriesParser();

            List<ConjunctiveQuery> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasEmptyHead()
                    .hasBodySize(1);
            assertThat(queries.stream().findFirst().orElseThrow().getBodyAtoms())
                    .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x)"));
        }

        @Test
        void shouldParse_simpleQuery_withHeadVariables() {
            String queriesString = "(x, y) :- p(x), q(y)";

            ConjunctiveQueriesParser parser = new ConjunctiveQueriesParser();

            List<ConjunctiveQuery> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasNonEmptyHead()
                    .hasBodySize(2);
            assertThat(queries.stream().findFirst().orElseThrow().getBodyAtoms())
                    .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x), q(y)"));
        }

    }

    @Nested
    class MultipleQueries {

        @Test
        void shouldParse_twoQueries() {
            String queriesString = """
                    () :- p(x)
                    (x, y) :- p(x), q(y)
                    """;

            ConjunctiveQueriesParser parser = new ConjunctiveQueriesParser();

            List<ConjunctiveQuery> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(2)
                    .satisfiesOnlyOnce(q -> {
                                assertThat(q)
                                        .hasEmptyHead()
                                        .hasBodySize(1);
                                assertThat(q.getHeadTerms())
                                        .isEmpty();
                                assertThat(q.getBodyAtoms())
                                        .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x)"));
                            }
                    )
                    .satisfiesOnlyOnce(q -> {
                                assertThat(q)
                                        .hasNonEmptyHead()
                                        .hasBodySize(2);
                                assertThat(q.getHeadTerms())
                                        .containsOnly(new Variable("x"), new Variable("y"));
                                assertThat(q.getBodyAtoms())
                                        .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x), q(y)"));
                            }
                    );
        }
    }

    @Nested
    class ParsingWithAlreadyExistingRelationalSchema {

        @Test
        void shouldNotCreateNewPredicates_whenExistingRelationalSchemaIsPassed() {
            String logicSchemaString = "@1 :- q(x)";
            LogicSchemaParser<LogicConstraintWithIDSpec> logicSchemaParser = new LogicSchemaWithIDsParser();
            LogicSchema logicSchema = logicSchemaParser.parse(logicSchemaString);
            Set<Predicate> relationalSchema = logicSchema.getAllPredicates();

            String queryString = "() :- q(x)";
            ConjunctiveQueriesParser queryParser = new ConjunctiveQueriesParser();
            List<ConjunctiveQuery> queries = queryParser.parse(queryString, relationalSchema);

            Predicate expected = relationalSchema.stream().toList().get(0);

            Assertions.assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .satisfies(q -> Assertions.assertThat(((OrdinaryLiteral) q.getBody().get(0)).getPredicate()).isEqualTo(expected));
        }

        @Test
        void shouldThrowException_whenUsingAlreadyExistentPredicatesButWithDifferentArity() {
            String logicSchemaString = "@1 :- q(x)";
            LogicSchemaParser<LogicConstraintWithIDSpec> logicSchemaParser = new LogicSchemaWithIDsParser();
            LogicSchema logicSchema = logicSchemaParser.parse(logicSchemaString);
            Set<Predicate> relationalSchema = logicSchema.getAllPredicates();

            String queryString = "() :- q(x,y)";
            ConjunctiveQueriesParser queryParser = new ConjunctiveQueriesParser();

            Assertions.assertThatThrownBy(() -> queryParser.parse(queryString, relationalSchema))
                    .isInstanceOf(RepeatedPredicateName.class);
        }

        @Test
        void shouldAddNewPredicates_whenExistingRelationalSchemaIsPassed_andNewPredicatesUsed() {
            String logicSchemaString = "@1 :- q(x)";
            LogicSchemaParser<LogicConstraintWithIDSpec> logicSchemaParser = new LogicSchemaWithIDsParser();
            LogicSchema logicSchema = logicSchemaParser.parse(logicSchemaString);
            Set<Predicate> relationalSchema = logicSchema.getAllPredicates();

            String queryString = "() :- p(x)";
            ConjunctiveQueriesParser queryParser = new ConjunctiveQueriesParser();
            List<ConjunctiveQuery> queries = queryParser.parse(queryString, relationalSchema);

            Assertions.assertThat(queries).hasSize(1);
        }
    }

}