package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

public class DerivationRuleSpecTest {

    @Test
    public void should_beAbleToCreateADerivationRuleSpec() {
        BodySpec bodySpec = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        List<TermSpec> termList = List.of(new ConstantSpec("a"));
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpec("a", termList, bodySpec);

        assertThat(derivationRuleSpec.getPredicateName()).isEqualTo("a");
        assertThat(derivationRuleSpec.getTermSpecList()).containsExactly(new ConstantSpec("a"));
        assertThat(derivationRuleSpec.getBody()).isEqualTo(bodySpec.literals());
    }

    @Test
    public void should_throwException_when_predicateNameIsNull() {
        assertThatThrownBy(() -> new DerivationRuleSpec(null, List.of(new ConstantSpec("a")), new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_termSpecListIsNull() {
        assertThatThrownBy(() -> new DerivationRuleSpec("a", null, new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_notThrowException_when_termSpecListIsEmpty() {
        assertThatCode(() -> new DerivationRuleSpec("a", List.of(), new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)))))
                .doesNotThrowAnyException();
    }

    @Test
    public void should_throwException_when_bodySpecIsNull() {
        assertThatThrownBy(() -> new DerivationRuleSpec("a", List.of(new ConstantSpec("a")), (BodySpec) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_when_listSpecIsNull() {
        assertThatThrownBy(() -> new DerivationRuleSpec("a", List.of(new ConstantSpec("a")), (List<LiteralSpec>) null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}