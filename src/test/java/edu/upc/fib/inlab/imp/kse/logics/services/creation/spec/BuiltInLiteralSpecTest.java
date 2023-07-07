package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class BuiltInLiteralSpecTest {

    @Test
    public void should_beAbleToCreateABuiltInLiteralSpec() {
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", List.of(new ConstantSpec("a")));

        assertThat(builtInLiteralSpec.getOperator()).isEqualTo("=");
        assertThat(builtInLiteralSpec.getTermSpecList()).containsExactly(new ConstantSpec("a"));
    }

    @Test
    public void should_throwException_when_operatorIsNull() {
        assertThatThrownBy(() -> new BuiltInLiteralSpec(null, List.of(new ConstantSpec("a"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_termSpecsListIsNull() {
        assertThatThrownBy(() -> new BuiltInLiteralSpec("=", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_notThrowAnyException_when_termSpecsListIsEmpty() {
        assertThatCode(() -> new BuiltInLiteralSpec("f", List.of()))
                .doesNotThrowAnyException();
    }
}