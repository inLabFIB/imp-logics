package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EqualityReplacerTest {

    @Test
    public void should_throwException_whenExecuteWithNullSchema() {
        assertThatThrownBy(() -> new EqualityReplacer().executeTransformation(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public static Stream<Arguments> provideLogicConstraintWithEqualityLiterals() {
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
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideLogicConstraintWithEqualityLiterals")
    public void should_replaceEquality_inLogicConstraints(String logicConstraintString, String expectedLogicConstraintString) {
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(logicConstraintString);

        SchemaTransformation schemaTransformation = new EqualityReplacer().executeTransformation(logicSchema);

        LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(expectedLogicConstraintString);
        assertThat(schemaTransformation.transformed()).isLogicallyEquivalentTo(expectedLogicSchema);
    }

    public static Stream<Arguments> provideDerivationRulesWithEqualityLiterals() {
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
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDerivationRulesWithEqualityLiterals")
    public void should_replaceEquality_inDerivationRules(String logicConstraintString, String expectedLogicConstraintString) {
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(logicConstraintString);

        SchemaTransformation schemaTransformation = new EqualityReplacer().executeTransformation(logicSchema);

        LogicSchema expectedLogicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(expectedLogicConstraintString);
        assertThat(schemaTransformation.transformed()).isLogicallyEquivalentTo(expectedLogicSchema);
    }

}