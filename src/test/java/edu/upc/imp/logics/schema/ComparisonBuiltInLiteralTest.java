package edu.upc.imp.logics.schema;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ComparisonBuiltInLiteralTest {

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
