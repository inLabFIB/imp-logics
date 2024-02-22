package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BuiltInLiteralSpecTest {

    @Test
    void should_beAbleToCreateABuiltInLiteralSpec() {
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", List.of(new ConstantSpec("a")));

        assertThat(builtInLiteralSpec.getOperator()).isEqualTo("=");
        assertThat(builtInLiteralSpec.getTermSpecList()).containsExactly(new ConstantSpec("a"));
    }

    @Test
    void should_throwException_when_operatorIsNull() {
        List<TermSpec> terms = List.of(new ConstantSpec("a"));
        assertThatThrownBy(() -> new BuiltInLiteralSpec(null, terms))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_termSpecsListIsNull() {
        assertThatThrownBy(() -> new BuiltInLiteralSpec("=", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_notThrowAnyException_when_termSpecsListIsEmpty() {
        assertThatCode(() -> new BuiltInLiteralSpec("f", List.of()))
                .doesNotThrowAnyException();
    }
}