package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;

public class BooleanBuiltInLiteralTest {

    @Nested
    class CreationTests {
        @Test
        void should_beTrue_when_createTrueBooleanBuiltInLiteral() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(true);

            assertThat(booleanBuiltInLiteral).hasOperationName("TRUE");
        }

        @Test
        void should_beFalse_when_createFalseBooleanBuiltInLiteral() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(false);

            assertThat(booleanBuiltInLiteral).hasOperationName("FALSE");
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void should_haveEmptyTerms_when_createAnyBooleanBuiltInLiteral(boolean value) {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(value);

            assertThat(booleanBuiltInLiteral).hasNoTerms();
        }
    }

    @Nested
    class ApplySubstitution {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void should_haveEmptyTerms_when_applyingSubstitution(boolean value) {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(value);

            BooleanBuiltInLiteral substitutedLiteral = booleanBuiltInLiteral.applySubstitution(new Substitution());

            assertThat(substitutedLiteral)
                    .isSameAs(booleanBuiltInLiteral);
        }
    }

    @Nested
    class BuildNegatedLiteralTest {
        @Test
        void should_ReturnTrue_WhenIsFalse() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(false);

            BooleanBuiltInLiteral negatedBol = booleanBuiltInLiteral.buildNegatedLiteral();

            assertThat(negatedBol).hasOperationName("TRUE");
        }

        @Test
        void should_ReturnFalse_WhenIsTrue() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(true);

            BooleanBuiltInLiteral negatedBol = booleanBuiltInLiteral.buildNegatedLiteral();

            assertThat(negatedBol).hasOperationName("FALSE");
        }

    }

}