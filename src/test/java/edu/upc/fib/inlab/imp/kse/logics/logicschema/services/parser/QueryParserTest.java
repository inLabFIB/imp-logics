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

class QueryParserTest {

    private final InstanceOfAssertFactory<Query, QueryAssert> instanceOfQueryAssert = new InstanceOfAssertFactory<>(Query.class, QueryAssert::assertThat);

    @Test
    void shouldParse_simpleQuery_withExtraComments() {
        String queriesString = """
                % comment 1
                (x) :- p(x) % comment 2
                % comment 3
                """;

        QueryParser parser = new QueryParser();

        List<Query> queries = parser.parse(queriesString);

        assertThat(queries).hasSize(1);
    }

    @Nested
    class SingleQueries {

        @Test
        void shouldParse_simpleQuery_withoutHeadVariables() {
            String queriesString = "() :- p(x)";

            QueryParser parser = new QueryParser();

            List<Query> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasEmptyHead()
                    .hasBodySize(1);

            Query query = queries.get(0);
            Assertions.assertThat(query.isConjunctiveQuery()).isTrue();
            assertThat(((ConjunctiveQuery) query).getBodyAtoms())
                    .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x)"));
        }

        @Test
        void shouldParse_simpleQuery_withHeadVariables() {
            String queriesString = "(x, y) :- p(x), q(y)";

            QueryParser parser = new QueryParser();

            List<Query> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasNonEmptyHead()
                    .hasBodySize(2);
            Query query = queries.get(0);
            Assertions.assertThat(query.isConjunctiveQuery()).isTrue();
            assertThat(((ConjunctiveQuery) query).getBodyAtoms())
                    .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x), q(y)"));
        }

        @Test
        void shouldParse_simpleQuery_withNegatedLiteral() {
            String queriesString = "() :- not(p(x))";
            QueryParser parser = new QueryParser();

            List<Query> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasBodySize(1);
            Query query = queries.get(0);
            Assertions.assertThat(query.isConjunctiveQuery()).isFalse();
            assertThat((query).getBody())
                    .containsExactlyLiteralsOf(List.of("not(p(x))"));
        }

        @Test
        void shouldParse_simpleQuery_withComparisonBuiltInLiteral() {
            String queriesString = "(x) :- p(x), x<1";
            QueryParser parser = new QueryParser();

            List<Query> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(1)
                    .first(instanceOfQueryAssert)
                    .hasNonEmptyHead()
                    .hasBodySize(2);
            Query query = queries.get(0);
            Assertions.assertThat(query.isConjunctiveQuery()).isFalse();
            assertThat((query).getBody())
                    .containsExactlyLiteralsOf(List.of("p(x)", "x<1"));
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

            QueryParser parser = new QueryParser();

            List<Query> queries = parser.parse(queriesString);

            assertThat(queries)
                    .hasSize(2)
                    .satisfiesOnlyOnce(q -> {
                                assertThat(q)
                                        .hasEmptyHead()
                                        .hasBodySize(1);
                                assertThat(q.getHeadTerms())
                                        .isEmpty();
                        Assertions.assertThat(q.isConjunctiveQuery()).isTrue();
                        assertThat(((ConjunctiveQuery) q).getBodyAtoms())
                                        .containsAtomsByPredicateName(ImmutableAtomListMother.create("p(x)"));
                            }
                    )
                    .satisfiesOnlyOnce(q -> {
                                assertThat(q)
                                        .hasNonEmptyHead()
                                        .hasBodySize(2);
                                assertThat(q.getHeadTerms())
                                        .containsOnly(new Variable("x"), new Variable("y"));
                        Assertions.assertThat(q.isConjunctiveQuery()).isTrue();
                        assertThat(((ConjunctiveQuery) q).getBodyAtoms())
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
            QueryParser queryParser = new QueryParser();
            List<Query> queries = queryParser.parse(queryString, relationalSchema);

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
            QueryParser queryParser = new QueryParser();

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
            QueryParser queryParser = new QueryParser();
            List<Query> queries = queryParser.parse(queryString, relationalSchema);

            Assertions.assertThat(queries).hasSize(1);
        }
    }


}