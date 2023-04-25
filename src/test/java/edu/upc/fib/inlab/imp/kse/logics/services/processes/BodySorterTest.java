package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BodySorterTest {

    @Test
    public void should_throwException_whenSortingNullSchema() {
        assertThatThrownBy(() -> new BodySorter().sort(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public static Stream<Arguments> provideLogicSchemasAndExpectedSortedBodies() {
        return Stream.of(
                Arguments.of(
                        "Case 1",
                        "not(Adult(x)), Emp(x)",
                        List.of("Emp(x)", "not(Adult(x))")
                ),
                Arguments.of(
                        "Case 2",
                        "not(Adult(x)), Emp(x), not(Adult(y))",
                        List.of("Emp(x)", "not(Adult(x))", "not(Adult(y))")
                ),
                Arguments.of(
                        "Case 3",
                        "not(Adult(x)), Emp(x), not(Adult(y)), Emp(y)",
                        List.of("Emp(x)", "Emp(y)", "not(Adult(x))", "not(Adult(y))")
                ),
                Arguments.of(
                        "Case 4",
                        "not(Adult(x)), x=1, Emp(x)",
                        List.of("Emp(x)", "not(Adult(x))", "x=1")
                ),
                Arguments.of(
                        "Case 5",
                        "not(Adult(x)), x=1, Emp(x), not(Adult(y)), y=2",
                        List.of("Emp(x)", "not(Adult(x))", "not(Adult(y))", "x=1", "y=2")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideLogicSchemasAndExpectedSortedBodies")
    public void should_sortLogicSchemaBodies(String caseName, String bodyString, List<String> sortedLiterals) {
        String constraintId = "1";
        String schemaString = String.format("@%s :- %s", constraintId, bodyString);
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        LogicSchema sortedSchema = new BodySorter().sort(logicSchema);

        LogicConstraint logicConstraint = sortedSchema.getLogicConstraintByID(new ConstraintID(constraintId));
        assertThat(logicConstraint.getBody())
                .describedAs(caseName)
                .containsExactlyLiteralsOf(sortedLiterals);
    }

    @ParameterizedTest
    @MethodSource("provideLogicSchemasAndExpectedSortedBodies")
    public void should_sortDerivationRuleBodies(String caseName, String bodyString, List<String> sortedLiterals) {
        String schemaString = String.format("q() :- %s", bodyString);
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        LogicSchema sortedSchema = new BodySorter().sort(logicSchema);

        DerivationRule rule = sortedSchema.getDerivationRulesByPredicateName("q").get(0);
        assertThat(rule.getBody())
                .describedAs(caseName)
                .containsExactlyLiteralsOf(sortedLiterals);
    }

    @Test
    public void should_maintainUnusedPredicatesInLogicSchema_when_ItContainsUnusedPredicates() {
        PredicateSpec unusedPredicateSpec = new PredicateSpec("Z", 2);

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addPredicate(unusedPredicateSpec)
                .build();

        LogicSchema sortedSchema = new BodySorter().sort(logicSchema);

        assertThat(sortedSchema).containsExactlyThesePredicateNames("Z");
    }

    @Test
    public void should_returnOriginalConstraintID_when_SchemaHasSeveralConstraints() {
        LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                @1 :- P(x)
                @2 :- Q(x)
                @3 :- R(x)
                """);

        SchemaTransformation schemaTransformation = new BodySorter().executeTransformation(originalSchema);

        SchemaTransformationAssert.assertThat(schemaTransformation)
                .constraintIDComesFrom("1", "1")
                .constraintIDComesFrom("2", "2")
                .constraintIDComesFrom("3", "3");
    }
}