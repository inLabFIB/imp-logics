package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class DerivationRuleSpecTest {

    @Test
    void should_beAbleToCreateADerivationRuleSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        List<TermSpec> termList = List.of(new ConstantSpec("a"));
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpec("a", termList, bodySpec);

        assertThat(derivationRuleSpec.getPredicateName()).isEqualTo("a");
        assertThat(derivationRuleSpec.getTermSpecList()).containsExactly(new ConstantSpec("a"));
        assertThat(derivationRuleSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    void should_throwException_when_predicateNameIsNull() {
        List<TermSpec> head = List.of(new ConstantSpec("head"));
        BodySpec body = new BodySpec(List.of(new OrdinaryLiteralSpec("head", head, true)));
        assertThatThrownBy(() -> new DerivationRuleSpec(null, head, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_termSpecListIsNull() {
        List<TermSpec> head = null;
        BodySpec body = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        assertThatThrownBy(() -> new DerivationRuleSpec("a", head, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_notThrowException_when_termSpecListIsEmpty() {
        List<TermSpec> head = List.of();
        BodySpec body = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        assertThatCode(() -> new DerivationRuleSpec("a", head, body))
                .doesNotThrowAnyException();
    }

    @Test
    void should_throwException_when_bodySpecIsNull() {
        List<TermSpec> head = List.of(new ConstantSpec("a"));
        BodySpec body = null;
        assertThatThrownBy(() -> new DerivationRuleSpec("a", head, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_listSpecIsNull() {
        List<TermSpec> head = List.of(new ConstantSpec("a"));
        List<LiteralSpec> body = null;
        assertThatThrownBy(() -> new DerivationRuleSpec("a", head, body))
                .isInstanceOf(IllegalArgumentException.class);
    }
}