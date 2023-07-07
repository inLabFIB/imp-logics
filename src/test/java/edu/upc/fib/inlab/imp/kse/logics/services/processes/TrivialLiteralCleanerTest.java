package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TrivialLiteralCleanerTest {

    @Nested
    class InputValidation {

        @Test
        void should_throwException_whenLogicSchemaIsNull() {
            TrivialLiteralCleaner builtInLiteralCleaner = new TrivialLiteralCleaner();
            assertThatThrownBy(() -> builtInLiteralCleaner.clean(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("LogicSchema cannot be null");
        }

        @Test
        void should_returnEmptySchema_whenInputLogicSchemaIsEmpty() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("");
            TrivialLiteralCleaner builtInLiteralCleaner = new TrivialLiteralCleaner();
            LogicSchema logicSchemaTransformed = builtInLiteralCleaner.clean(logicSchema);
            assertThat(logicSchemaTransformed).isEmpty();
        }
    }

    @Nested
    class Traceability {

        @Test
        public void should_maintainTraceabilityMap_when_transformLogicSchemaCreatesSeveralConstraints() {
            LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                         @1 :- P(X), TRUE()
                         P(x) :- Q(x), TRUE()
                    """);

            TrivialLiteralCleaner builtInLiteralCleaner = new TrivialLiteralCleaner();
            SchemaTransformation schemaTransformation = builtInLiteralCleaner.executeTransformation(originalSchema);
            assertThat(schemaTransformation).constraintIDComesFrom("1", "1");
        }

    }


    @Nested
    class Process {

        @Test
        public void should_canExecuteProcess() {
            LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                         @1 :- P(X), TRUE()
                         P(x) :- Q(x), TRUE()
                    """);

            TrivialLiteralCleaner builtInLiteralCleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = builtInLiteralCleaner.execute(originalSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                         @1 :- P(X)
                         P(x) :- Q(x)
                    """);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }

    @Nested
    class BooleanBuiltinLiteralsCase {

        public static Stream<Arguments> provideLogicConstraintsAndBooleanBuiltinLiterals() {

            return Stream.of(
                    Arguments.of(
                            "@1 :- A(x), TRUE()",
                            "@1 :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- A(x), FALSE()",
                            "@1 :- FALSE()",
                            List.of(new PredicateSpec("A", 1))
                    ),
                    Arguments.of(
                            "@1 :- TRUE()",
                            "@1 :- TRUE()",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- FALSE()",
                            "@1 :- FALSE()",
                            List.of()
                    )
            );
        }


        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicConstraintsAndBooleanBuiltinLiterals")
        public void should_clean_when_schemaContainsLogicConstraintsAndBooleanBuiltinLiterals(
                String schemaString,
                String expectedSchemaString,
                List<PredicateSpec> expectedPredicateSpecs
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchemaString, expectedPredicateSpecs);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        public static Stream<Arguments> provideDerivationRulesAndBooleanBuiltinLiterals() {

            return Stream.of(
                    Arguments.of(
                            "B(x) :- A(x), TRUE()",
                            "B(x) :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "B(x) :- A(x), FALSE()",
                            "B(x) :- FALSE()",
                            List.of(new PredicateSpec("A", 1))
                    ),
                    Arguments.of(
                            "B(x) :- TRUE()",
                            "B(x):- TRUE()",
                            List.of()
                    ),
                    Arguments.of(
                            "B(x) :- FALSE()",
                            "B(x) :- FALSE()",
                            List.of()
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideDerivationRulesAndBooleanBuiltinLiterals")
        public void should_clean_when_schemaContainsDerivationRulesAndBooleanBuiltinLiterals(
                String schemaString,
                String expectedSchemaString,
                List<PredicateSpec> expectedPredicateSpecs
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(schemaString);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchemaString, expectedPredicateSpecs);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }


        public static Stream<Arguments> provideDerivationRulesWithBuiltinLiterals() {
            return Stream.of(
                    Arguments.of(
                            """
                                    A(x) :- B(x)
                                    A(x) :- FALSE()
                                    """,
                            """
                                    A(x) :- B(x)
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    A(x) :- B(x)
                                    A(x) :- TRUE()
                                    """,
                            """
                                    A(x) :- TRUE()
                                    """,
                            List.of(new PredicateSpec("B", 1))
                    )

            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideDerivationRulesWithBuiltinLiterals")
        public void should_clean_when_schemaContainsSeveralDerivationRulesWithBuiltinLiterals(
                String schemaString,
                String expectedSchemaString,
                List<PredicateSpec> expectedPredicateSpecs
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchemaString, expectedPredicateSpecs);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        public static Stream<Arguments> provideDerivationRulesWithBuiltinLiterals_WithRepeatedVariablesAndConstantsInHeads() {
            return Stream.of(
                    Arguments.of(
                            """
                                    A(x, 1) :- B(x, y)
                                    A(x, y) :- TRUE()
                                    """,
                            """
                                    A(x, y) :- TRUE()
                                    """,
                            List.of(new PredicateSpec("B", 2))
                    ),
                    Arguments.of(
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, 1) :- TRUE()
                                    """,
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, 1) :- TRUE()
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    A(x, x) :- B(x, y)
                                    A(x, y) :- TRUE()
                                    """,
                            """
                                    A(x, y) :- TRUE()
                                    """,
                            List.of(new PredicateSpec("B", 2))
                    ),
                    Arguments.of(
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, x) :- TRUE()
                                    """,
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, x) :- TRUE()
                                    """,
                            List.of(new PredicateSpec("B", 2))
                    ),
                    // FALSE
                    Arguments.of(
                            """
                                    A(x, 1) :- B(x, y)
                                    A(x, y) :- FALSE()
                                    """,
                            """
                                    A(x, 1) :- B(x, y)
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, 1) :- FALSE()
                                    """,
                            """
                                    A(x, y) :- B(x, y)
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    A(x, x) :- B(x, y)
                                    A(x, y) :- FALSE()
                                    """,
                            """
                                    A(x, x) :- B(x, y)
                                    """,
                            List.of(new PredicateSpec("B", 2))
                    ),
                    Arguments.of(
                            """
                                    A(x, y) :- B(x, y)
                                    A(x, x) :- FALSE()
                                    """,
                            """
                                    A(x, y) :- B(x, y)
                                    """,
                            List.of(new PredicateSpec("B", 2))
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideDerivationRulesWithBuiltinLiterals_WithRepeatedVariablesAndConstantsInHeads")
        public void should_clean_when_schemaContainsDerivationRulesWithBuiltinLiterals_WithRepeatedVariablesAndConstantsInHeads(
                String schemaString,
                String expectedSchemaString,
                List<PredicateSpec> expectedPredicateSpecs
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchemaString, expectedPredicateSpecs);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        public static Stream<Arguments> provideLogicSchemas() {
            return Stream.of(
                    Arguments.of(
                            """
                                    @1 :- A(x)
                                    A(x) :- TRUE()
                                    """,
                            """
                                    @1 :- TRUE()
                                    A(x) :- TRUE()
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    @1 :- A(x)
                                    A(x) :- FALSE()
                                    """,
                            """
                                    @1 :- FALSE()
                                    A(x) :- FALSE()
                                    """,
                            List.of()
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemas")
        void integrationTests(
                String schemaString,
                String expectedSchemaString,
                List<PredicateSpec> expectedPredicateSpecs
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchemaString, expectedPredicateSpecs);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        public static Stream<Arguments> provideLogicSchemasWithNegation() {
            return Stream.of(
                    Arguments.of(
                            """
                                    @1 :- not(A(x))
                                    A(x) :- TRUE()
                                    """,
                            """
                                    @1 :- FALSE()
                                    A(x) :- TRUE()
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    @1 :- not(A(x))
                                    A(x) :- FALSE()
                                    """,
                            """
                                    @1 :- TRUE()
                                    A(x) :- FALSE()
                                    """,
                            List.of()),
                    Arguments.of(
                            """
                                    A(x) :- not(B(x))
                                    B(x) :- not(C(x))
                                    C(x) :- FALSE()
                                    """,
                            """
                                    A(x) :- FALSE()
                                    B(x) :- TRUE()
                                    C(x) :- FALSE()
                                    """,
                            List.of()
                    ),
                    Arguments.of(
                            """
                                    A(x) :- not(B(x))
                                    B(x) :- not(C(x))
                                    C(x) :- FALSE()
                                    C(x) :- TRUE()
                                    """,
                            """
                                    A(x) :- TRUE()
                                    B(x) :- FALSE()
                                    C(x) :- TRUE()
                                    """,
                            List.of()
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemasWithNegation")
        public void should_clean_when_schemaIncludesNegatedDerivedLiterals(String inputSchema,
                                                                           String expectedSchema,
                                                                           List<PredicateSpec> additionalExpectedPredicates) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(inputSchema);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchema, additionalExpectedPredicates);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }

    @Nested
    class ComparisonBuiltinLiteralsCase {

        public static Stream<Arguments> provideLogicSchemasWithTrueEqualityGroundLiterals() {
            return Stream.of(
                    Arguments.of(
                            "@1 :- A(x), 1=1",
                            "@1 :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- A(x), 1=1",
                            "P(x) :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- 1=1",
                            "@1 :- TRUE()",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- 1=1",
                            "P(x) :- TRUE()",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- A(x), 'X'='X'",
                            "@1 :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- A(x), 'X'='X'",
                            "P(x) :- A(x)",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- 'X'='X'",
                            "@1 :- TRUE()",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- 'X'='X'",
                            "P(x) :- TRUE()",
                            List.of()
                    )
            );
        }

        public static Stream<Arguments> provideLogicSchemasWithFalseEqualityGroundLiterals() {
            return Stream.of(

                    Arguments.of(
                            "@1 :- A(x), 1=2",
                            "@1 :- A(x), 1=2",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- A(x), 1=2",
                            "P(x) :- A(x), 1=2",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- 1=2",
                            "@1 :- 1=2",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- 1=2",
                            "P(x) :- 1=2",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- A(x), 'X'=2",
                            "@1 :- A(x), 'X'=2",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- A(x), 'X'=2",
                            "P(x) :- A(x), 'X'=2",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- 'X'=2",
                            "@1 :- 'X'=2",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- 'X'=2",
                            "P(x) :- 'X'=2",
                            List.of()
                    ),

                    Arguments.of(
                            "@1 :- A(x), 'X'='Y'",
                            "@1 :- A(x), 'X'='Y'",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- A(x), 'X'='Y'",
                            "P(x) :- A(x), 'X'='Y'",
                            List.of()
                    ),
                    Arguments.of(
                            "@1 :- 'X'='Y'",
                            "@1 :- 'X'='Y'",
                            List.of()
                    ),
                    Arguments.of(
                            "P(x) :- 'X'='Y'",
                            "P(x) :- 'X'='Y'",
                            List.of()
                    )
            );
        }

        public static Stream<Arguments> provideLogicSchemasWithEqualityNonGroundLiterals() {
            return Stream.of(

                    Arguments.of(
                            "@1 :- A(x), x=2",
                            "@1 :- A(x), x=2",
                            List.of()
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemasWithTrueEqualityGroundLiterals")
        public void should_clean_when_schemaIncludesTrueEqualityGroundLiterals(
                String inputSchema,
                String expectedSchema,
                List<PredicateSpec> additionalExpectedPredicates
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(inputSchema);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchema, additionalExpectedPredicates);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }


        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemasWithFalseEqualityGroundLiterals")
        public void should_clean_when_schemaIncludesFalseEqualityGroundLiterals(
                String inputSchema,
                String expectedSchema,
                List<PredicateSpec> additionalExpectedPredicates
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(inputSchema);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchema, additionalExpectedPredicates);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemasWithEqualityNonGroundLiterals")
        public void should_clean_when_schemaIncludesEqualityNonGroundLiterals(
                String inputSchema,
                String expectedSchema,
                List<PredicateSpec> additionalExpectedPredicates
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(inputSchema);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchema, additionalExpectedPredicates);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }

    @Nested
    class IntegrationTest {

        public static Stream<Arguments> provideLogicSchemasWithIntegrationCases() {
            return Stream.of(
                    Arguments.of(
                            """
                                    P(x) :- not(Q(x)), R(x)
                                    Q(x) :- 1=1
                                    """,
                            """
                                    P(x) :- FALSE()
                                    Q(x) :- TRUE()
                                    """,
                            List.of(new PredicateSpec("R", 1))
                    )
            );
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @MethodSource("provideLogicSchemasWithIntegrationCases")
        public void should_clean_when_schemaWithIntegrationCases(
                String inputSchema,
                String expectedSchema,
                List<PredicateSpec> additionalExpectedPredicates
        ) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(inputSchema);

            TrivialLiteralCleaner cleaner = new TrivialLiteralCleaner();
            LogicSchema actualLogicSchema = cleaner.clean(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDsAndPredicates(expectedSchema, additionalExpectedPredicates);
            assertThat(actualLogicSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }
    }
}