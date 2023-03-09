package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.utils.LiteralMother;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LiteralTest {

    @Test
    public void should_ReturnUsedVariables() {
        Literal literal = LiteralMother.createOrdinaryLiteral("P", "x", "1", "y");

        Set<Variable> usedVariables = literal.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }
}
