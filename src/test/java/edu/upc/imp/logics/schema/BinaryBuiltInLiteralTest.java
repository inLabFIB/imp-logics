package edu.upc.imp.logics.schema;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class BinaryBuiltInLiteralTest {

    @Test
    public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullLeftTerm() {
        assertThatThrownBy(() -> new BinaryBuiltInLiteral(null, new Constant("1"), BinaryOperation.EQUALS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullRightTerm() {
        assertThatThrownBy(() -> new BinaryBuiltInLiteral(new Constant("1"), null, BinaryOperation.EQUALS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingBuiltInLiteral_WithNullOperator() {
        assertThatThrownBy(() -> new BinaryBuiltInLiteral(new Constant("1"), new Constant("1"), null))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
