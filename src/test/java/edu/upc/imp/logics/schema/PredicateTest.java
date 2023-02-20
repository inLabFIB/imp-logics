package edu.upc.imp.logics.schema;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PredicateTest {

    @Test
    public void should_ThrowException_WhenCreatingPredicateWithNullName() {
        assertThatThrownBy(() -> new MutablePredicate(null, new Arity(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingPredicateWithNullArity() {
        assertThatThrownBy(() -> new MutablePredicate("P", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
