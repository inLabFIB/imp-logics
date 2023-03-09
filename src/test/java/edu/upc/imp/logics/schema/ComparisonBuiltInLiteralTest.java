package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.assertions.ComparisonBuiltInLiteralAssert;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

    @Test
    public void should_ReturnNewComparisonBuiltInLiteral_WithSubstitutedTerms_WhenApplyingSubstitution() {
        ComparisonBuiltInLiteral builtInLiteral = new ComparisonBuiltInLiteral(new Variable("x"), new Variable("y"), ComparisonOperator.LESS_THAN);
        Substitution substitution = new SubstitutionBuilder()
                .addMapping("x", "a")
                .addMapping("y", "b")
                .build();

        ComparisonBuiltInLiteral actualBuiltInLiteral = builtInLiteral.applySubstitution(substitution);
        ComparisonBuiltInLiteralAssert.assertThat(actualBuiltInLiteral)
                .isNotSameAs(builtInLiteral)
                .hasComparisonOperation(ComparisonOperator.LESS_THAN.getSymbol())
                .hasLeftVariable("a")
                .hasRightVariable("b");
    }
}
