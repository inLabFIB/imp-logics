package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivationRuleMother;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DerivationRuleTest {

    public static Stream<Arguments> provideDerivationRulesWithUniversalVariables() {
        return Stream.of(
                Arguments.of(
                        "A(x) :- B(x), C(x, y)",
                        Set.of(new Variable("x"))
                ),
                Arguments.of(
                        "A(x) :- B(x), C(x, 1)",
                        Set.of(new Variable("x"))
                ),
                Arguments.of(
                        "A(x) :- B(x), C(x)",
                        Set.of(new Variable("x"))
                ),
                Arguments.of(
                        "A(1) :- B(x)",
                        Set.of()
                ),
                Arguments.of(
                        "A(x) :- B(1)",
                        Set.of(new Variable("x"))
                ),
                Arguments.of(
                        "A() :- B(x)",
                        Set.of()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDerivationRulesWithUniversalVariables")
    public void should_returnUniversalVariables_whenDerivationRuleHasUniversalVariables(String derivationRuleString, Set<Variable> expectedUniversalVariables) {
        DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);

        Set<Variable> universalVariables = derivationRule.getUniversalVariables();

        assertThat(universalVariables).containsExactlyInAnyOrderElementsOf(expectedUniversalVariables);
    }


    public static Stream<Arguments> provideDerivationRulesWithExistencialVariables() {
        return Stream.of(
                Arguments.of(
                        "A(x) :- B(x), C(x, y)",
                        Set.of(new Variable("y"))
                ),
                Arguments.of(
                        "A(x) :- B(x), C(x, 1)",
                        Set.of()
                ),
                Arguments.of(
                        "A(x) :- B(x), C(x)",
                        Set.of()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDerivationRulesWithExistencialVariables")
    public void should_returnExistencialVariables_whenDerivationRuleHasExistencialVariables(String derivationRuleString, Set<Variable> expectedExistencialVariables) {
        DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);

        Set<Variable> universalVariables = derivationRule.getExistencialVariables();

        assertThat(universalVariables).containsExactlyInAnyOrderElementsOf(expectedExistencialVariables);
    }

}