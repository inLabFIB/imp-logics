package edu.upc.fib.inlab.imp.kse.logics.schema;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ConstraintIDTest {

    @Test
    public void should_ThrowIllegalArgumentException_WhenCreatingConstraintID_WithNullId() {
        assertThatThrownBy(() -> new ConstraintID(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}