package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivationRuleMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DerivationRuleTest {

    @Nested
    class UniversalAndExistencialVariables {
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

            Set<Variable> universalVariables = derivationRule.getExistentialVariables();

            assertThat(universalVariables).containsExactlyInAnyOrderElementsOf(expectedExistencialVariables);
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
        public void should_ReturnTrue_WhenCallingIsSafe_WithSafeDerivationRule(String derivationRuleString) {
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
        public void should_ReturnFalse_WhenCallingIsSafe_WithUnsafeDerivationRule(String derivationRuleString) {
            DerivationRule derivationRule = DerivationRuleMother.create(derivationRuleString);
            assertThat(derivationRule.isSafe()).isFalse();
        }

    }

}