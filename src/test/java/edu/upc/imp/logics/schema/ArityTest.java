package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ArityTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void should_CreateArity_WhenUsingPositiveOrZeroNumber(int arity){
        assertThatNoException().isThrownBy(() -> new Arity(arity));
    }
    @Test
    public void should_ThrowException_WhenCreatingNegativeArity(){
        assertThatThrownBy(() -> new Arity(-1));
    }

    @Test
    public void should_NotThrowException_WhenMatchingArity(){
        Arity arity = new Arity(1);
        assertThatNoException().isThrownBy(() -> arity.checkMatches(List.of(new Variable("x"))));
    }

    @Test
    public void should_ThrowException_WhenMatchingArity(){
        Arity arity = new Arity(1);
        assertThatThrownBy(() -> arity.checkMatches(List.of(new Variable("x"), new Variable("y")))).isInstanceOf(ArityMismatch.class);
    }
}
