package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BooleanBuiltInLiteralTest {

    @Nested
    class CreationTests {
        @Test
        void should_beTrue_when_createTrueBooleanBuiltInLiteral() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(true);

            LogicSchemaAssertions.assertThat(booleanBuiltInLiteral).hasOperationName("TRUE");
        }

        @Test
        void should_beFalse_when_createFalseBooleanBuiltInLiteral() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(false);

            LogicSchemaAssertions.assertThat(booleanBuiltInLiteral).hasOperationName("FALSE");
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void should_haveEmptyTerms_when_createAnyBooleanBuiltInLiteral(boolean value) {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(value);

            LogicSchemaAssertions.assertThat(booleanBuiltInLiteral).hasNoTerms();
        }
    }

    @Nested
    class ApplySubstitution {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void should_haveEmptyTerms_when_applyingSubstitution(boolean value) {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(value);

            BooleanBuiltInLiteral substitutedLiteral = booleanBuiltInLiteral.applySubstitution(new Substitution());

            LogicSchemaAssertions.assertThat(substitutedLiteral)
                    .isSameAs(booleanBuiltInLiteral);
        }
    }

    @Nested
    class BuildNegatedLiteralTest {
        @Test
        void should_ReturnTrue_WhenIsFalse() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(false);

            BooleanBuiltInLiteral negatedBol = booleanBuiltInLiteral.buildNegatedLiteral();

            LogicSchemaAssertions.assertThat(negatedBol).hasOperationName("TRUE");
        }

        @Test
        void should_ReturnFalse_WhenIsTrue() {
            BooleanBuiltInLiteral booleanBuiltInLiteral = new BooleanBuiltInLiteral(true);

            BooleanBuiltInLiteral negatedBol = booleanBuiltInLiteral.buildNegatedLiteral();

            LogicSchemaAssertions.assertThat(negatedBol).hasOperationName("FALSE");
        }

    }

}