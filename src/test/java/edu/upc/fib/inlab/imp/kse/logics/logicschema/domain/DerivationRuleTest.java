package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.DerivationRuleMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DerivationRuleTest {

    @Nested
    class UniversalAndExistentialVariables {
        static Stream<Arguments> provideDerivationRulesWithUniversalVariables() {
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

        static Stream<Arguments> provideDerivationRulesWithExistentialVariables() {
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
        @MethodSource("provideDerivationRulesWithUniversalVariables")
        void should_returnUniversalVariables_whenDerivationRuleHasUniversalVariables(String derivationRuleString, Set<Variable> expectedUniversalVariables) {
            DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);

            Set<Variable> universalVariables = derivationRule.getUniversalVariables();

            assertThat(universalVariables).containsExactlyInAnyOrderElementsOf(expectedUniversalVariables);
        }

        @ParameterizedTest
        @MethodSource("provideDerivationRulesWithExistentialVariables")
        void should_returnExistentialVariables_whenDerivationRuleHasExistentialVariables(String derivationRuleString, Set<Variable> expectedExistentialVariables) {
            DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);

            Set<Variable> universalVariables = derivationRule.getExistentialVariables();

            assertThat(universalVariables).containsExactlyInAnyOrderElementsOf(expectedExistentialVariables);
        }

    }

    @Nested
    class IsSafe {
        @ParameterizedTest
        @ValueSource(strings = {
                "P(x) :- Q(x)",
                "P(x) :- R(x, y), not(Q(x))",
                "P() :- R(), not(Q())",
                "P(x) :- R(x, y), not(Q(1))",
                "P(x) :- R(x), x = 1",
        })
        void should_ReturnTrue_WhenCallingIsSafe_WithSafeDerivationRule(String derivationRuleString) {
            DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);
            assertThat(derivationRule.isSafe()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "P(x) :- R(x, y), not(Q(z))",
                "P(x) :- not(Q(x))",
                "P(x) :- R(y)",
                "P(x) :- x = x",
                "P(1) :- x = x",
        })
        void should_ReturnFalse_WhenCallingIsSafe_WithUnsafeDerivationRule(String derivationRuleString) {
            DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);
            assertThat(derivationRule.isSafe()).isFalse();
        }

    }

}