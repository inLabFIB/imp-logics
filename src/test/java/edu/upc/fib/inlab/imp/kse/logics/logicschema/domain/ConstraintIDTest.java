package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ConstraintIDTest {

    @Test
    void should_ThrowIllegalArgumentException_WhenCreatingConstraintID_WithNullId() {
        assertThatThrownBy(() -> new ConstraintID(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}