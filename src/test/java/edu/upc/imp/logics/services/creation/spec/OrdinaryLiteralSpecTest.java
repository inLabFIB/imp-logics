package edu.upc.imp.logics.services.creation.spec;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrdinaryLiteralSpecTest {


    @Test
    public void should_beAbleToCreateAnOrdinaryLiteralSpec() {
        List<TermSpec> termList = List.of(new TermSpec("a") {
        });

        OrdinaryLiteralSpec ordinaryLiteralSpec = new OrdinaryLiteralSpec("a", termList, true);

        assertThat(ordinaryLiteralSpec.getPredicateName()).isEqualTo("a");
        assertThat(ordinaryLiteralSpec.getTermSpecList()).containsExactlyElementsOf(termList);
        assertThat(ordinaryLiteralSpec.isPositive()).isTrue();
    }

    @Test
    public void should_throwException_when_predicateNameIsNull() {
        List<TermSpec> termList = List.of(new TermSpec("a") {
        });

        assertThatThrownBy(() -> new OrdinaryLiteralSpec(null, termList, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_termsListIsNull() {
        assertThatThrownBy(() -> new OrdinaryLiteralSpec("a", null, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Disabled("Should we allow empty terms list?")
    @Test
    public void should_throwException_when_termsListIsEmpty() {
        assertThatThrownBy(() -> new OrdinaryLiteralSpec("a", List.of(), true))
                .isInstanceOf(IllegalArgumentException.class);
    }

}