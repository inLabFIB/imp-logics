package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrdinaryLiteralSpecTest {


    @Test
    void should_beAbleToCreateAnOrdinaryLiteralSpec() {
        List<TermSpec> termList = List.of(new TermSpec("a") {
        });

        OrdinaryLiteralSpec ordinaryLiteralSpec = new OrdinaryLiteralSpec("a", termList, true);

        assertThat(ordinaryLiteralSpec.getPredicateName()).isEqualTo("a");
        assertThat(ordinaryLiteralSpec.getTermSpecList()).containsExactlyElementsOf(termList);
        assertThat(ordinaryLiteralSpec.isPositive()).isTrue();
    }

    @Test
    void should_throwException_when_predicateNameIsNull() {
        List<TermSpec> termList = List.of(new TermSpec("a") {
        });

        assertThatThrownBy(() -> new OrdinaryLiteralSpec(null, termList, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_termsListIsNull() {
        assertThatThrownBy(() -> new OrdinaryLiteralSpec("a", null, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_notThrowException_when_termsListIsEmpty() {
        assertThatCode(() -> new OrdinaryLiteralSpec("a", List.of(), true))
                .doesNotThrowAnyException();
    }

}