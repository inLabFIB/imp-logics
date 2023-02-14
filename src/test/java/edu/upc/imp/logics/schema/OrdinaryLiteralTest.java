package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.AtomMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class OrdinaryLiteralTest {

    @Test
    public void should_ThrowException_WhenCreatingOrdinaryLiteral_WithNullAtom() {
        assertThatThrownBy(() -> new OrdinaryLiteral(null, true)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_CreatePositiveOrdinaryLiteral_WhenCreatingOrdinaryLiteral_WithNoSign() {
        OrdinaryLiteral oLiteral = new OrdinaryLiteral(AtomMother.createAtomWithVariableNames("P", List.of("x")));
        assertThat(oLiteral.isPositive()).isTrue();
    }
}
