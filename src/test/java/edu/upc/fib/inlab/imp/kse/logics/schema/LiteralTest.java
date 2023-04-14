package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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


    @ParameterizedTest
    @MethodSource("providedTermsAndArity")
    public void should_returnArity_whenLiteralContainTerms(List<String> terms, int expectedArity) {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("P", terms);

        int arity = literal.getArity();

        assertThat(arity)
                .describedAs("Literal Arity")
                .isEqualTo(expectedArity);
    }

    public static Stream<Arguments> providedTermsAndArity() {
        return Stream.of(
                Arguments.of(List.of("x", "y", "z"), 3),
                Arguments.of(List.of("x", "y"), 2),
                Arguments.of(List.of("x"), 1),
                Arguments.of(List.of(), 0)
        );
    }
}
