package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ComparisonBuiltInLiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ComparisonBuiltInLiteralTest {

    @Nested
    class CreationTests {
        @Test
        public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullLeftTerm() {
            assertThatThrownBy(() -> new ComparisonBuiltInLiteral(null, new Constant("1"), ComparisonOperator.EQUALS))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullRightTerm() {
            assertThatThrownBy(() -> new ComparisonBuiltInLiteral(new Constant("1"), null, ComparisonOperator.EQUALS))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullOperator() {
            assertThatThrownBy(() -> new ComparisonBuiltInLiteral(new Constant("1"), new Constant("1"), null))
                    .isInstanceOf(IllegalArgumentException.class);

        }

        @Test
        public void should_NotThrowException_WhenCreatingBuiltInLiteral_WithCorrectParameters() {
            assertThatNoException()
                    .isThrownBy(() -> new ComparisonBuiltInLiteral(new Constant("1"), new Constant("1"), ComparisonOperator.EQUALS));

        }
    }

    @Nested
    class ApplySubstitution {
        @Test
        public void should_ReturnNewComparisonBuiltInLiteral_WithSubstitutedTerms_WhenApplyingSubstitution() {
            ComparisonBuiltInLiteral builtInLiteral = new ComparisonBuiltInLiteral(new Variable("x"), new Variable("y"), ComparisonOperator.LESS_THAN);
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "a")
                    .addMapping("y", "b")
                    .build();

            ComparisonBuiltInLiteral actualBuiltInLiteral = builtInLiteral.applySubstitution(substitution);
            assertThat(actualBuiltInLiteral)
                    .isNotSameAs(builtInLiteral)
                    .hasComparisonOperation(ComparisonOperator.LESS_THAN.getSymbol())
                    .hasLeftVariable("a")
                    .hasRightVariable("b");
        }
    }

    @Nested
    class BuildNegatedLiteralTest {

        public static Stream<Arguments> provideOperatorsAndItsNegation() {
            return Stream.of(
                    Arguments.of(ComparisonOperator.LESS_THAN, ComparisonOperator.GREATER_OR_EQUALS),
                    Arguments.of(ComparisonOperator.LESS_OR_EQUALS, ComparisonOperator.GREATER_THAN),
                    Arguments.of(ComparisonOperator.EQUALS, ComparisonOperator.NOT_EQUALS),
                    Arguments.of(ComparisonOperator.NOT_EQUALS, ComparisonOperator.EQUALS),
                    Arguments.of(ComparisonOperator.GREATER_OR_EQUALS, ComparisonOperator.LESS_THAN),
                    Arguments.of(ComparisonOperator.GREATER_THAN, ComparisonOperator.LESS_OR_EQUALS)
            );
        }

        @ParameterizedTest
        @MethodSource("provideOperatorsAndItsNegation")
        public void should_ReturnNewNegatedComparison(ComparisonOperator operator, ComparisonOperator negatedOperator) {
            Term leftTerm = new Variable("x");
            Term rightTerm = new Variable("y");
            ComparisonBuiltInLiteral col = new ComparisonBuiltInLiteral(leftTerm, rightTerm, operator);

            ComparisonBuiltInLiteral negatedCol = col.buildNegatedLiteral();

            ComparisonBuiltInLiteralAssert.assertThat(negatedCol)
                    .hasLeftVariable("x")
                    .hasRightVariable("y")
                    .hasComparisonOperation(negatedOperator.getSymbol());
        }
    }
}
