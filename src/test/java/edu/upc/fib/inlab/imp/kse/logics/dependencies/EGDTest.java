package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class EGDTest {

    @Nested
    class CreationTests {

        private final List<Literal> defaultBody = List.of(LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")));

        @Test
        void should_throwException_whenCreatingATGD_withNullHead() {
            assertThatThrownBy(() -> new EGD(defaultBody, null)).isInstanceOf(IllegalArgumentException.class);
        }

    }

}
