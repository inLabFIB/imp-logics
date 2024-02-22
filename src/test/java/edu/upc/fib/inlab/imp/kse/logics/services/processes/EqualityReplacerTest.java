package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EqualityReplacerTest {

    @Nested
    class InputValidationTests {
        @Test
        void should_throwException_whenExecuteWithNullSchema() {
            EqualityReplacer equalityReplacer = new EqualityReplacer();
            assertThatThrownBy(() -> equalityReplacer.executeTransformation(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class EqualityReplacerTests {

        static Stream<Arguments> provideLogicConstraintWithEqualityLiterals() {
            return Stream.of(
                    Arguments.of(
                            "@1 :- R(x, y), x=1",
                            "@1 :- R(1, y)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y), 1=x",
                            "@1 :- R(1, y)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y), x=y",
                            "@1 :- R(x, x)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z), x=y, y=z, z=y",
                            "@1 :- R(x, x, x)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z), x=y, y=z, z=x",
                            "@1 :- R(x, x, x)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z), a=1",
                            "@1 :- R(x, y, z)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z), x=y, y=1, x=z, z=2",
                            "@1 :- R(x, y, z), x=y, y=1, x=z, z=2"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z, u), x=y, z=u, y=z",
                            "@1 :- R(x, x, x, x)"
                    ),
                    Arguments.of(
                            "@1 :- R(x, y, z, u), x=y, z=u",
                            "@1 :- R(x, x, z, z)"
                    )
            );
        }

        static Stream<Arguments> provideDerivationRulesWithEqualityLiterals() {
            return Stream.of(
                    Arguments.of(
                            "P(x) :- R(x, y), x=1",
                            "P(1) :- R(1, y)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y), 1=x",
                            "P(1) :- R(1, y)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y), x=y",
                            "P(x) :- R(x, x)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z), x=y, y=z, z=y",
                            "P(x) :- R(x, x, x)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z), x=y, y=z, z=x",
                            "P(x) :- R(x, x, x)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z), a=1",
                            "P(x) :- R(x, y, z)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z), x=y, y=1, x=z, z=2",
                            "P(x) :- R(x, y, z), x=y, y=1, x=z, z=2"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z, u), x=y, z=u, y=z",
                            "P(x) :- R(x, x, x, x)"
                    ),
                    Arguments.of(
                            "P(x) :- R(x, y, z, u), x=y, z=u",
                            "P(x) :- R(x, x, z, z)"
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("provideLogicConstraintWithEqualityLiterals")
        void should_replaceEquality_inLogicConstraints(String logicConstraintString, String expectedLogicConstraintString) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(logicConstraintString);

            SchemaTransformation schemaTransformation = new EqualityReplacer().executeTransformation(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(expectedLogicConstraintString);
            assertThat(schemaTransformation.transformed())
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);

        }

        @ParameterizedTest
        @MethodSource("provideDerivationRulesWithEqualityLiterals")
        void should_replaceEquality_inDerivationRules(String logicConstraintString, String expectedLogicConstraintString) {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(logicConstraintString);

            SchemaTransformation schemaTransformation = new EqualityReplacer().executeTransformation(logicSchema);

            LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(expectedLogicConstraintString);
            assertThat(schemaTransformation.transformed())
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }

    @Nested
    class TraceabilityMapTest {


        @Test
        void should_maintainConstraintID_when_executeEqualityReplacer() {
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                @2 :- Q(x, y), Y(y)
                                @3 :- P(x, y), A(y)
                                A(x) :- B(x, y), C(y)
                            """
            );

            SchemaTransformation schemaTransformation = new EqualityReplacer().executeTransformation(logicSchema);

            SchemaTransformationAssert.assertThat(schemaTransformation)
                    .constraintIDComesFrom("1", "1")
                    .constraintIDComesFrom("2", "2")
                    .constraintIDComesFrom("3", "3");
        }

    }

}