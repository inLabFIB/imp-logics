package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.utils.DerivationRuleMother;
import edu.upc.imp.logics.schema.utils.LogicConstraintMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicEquivalenceAnalyzerTest {
    @Nested
    class LiteralsListTest {
        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenFirstLiteralsList_isNull() {
                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, List.of()))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLiteralsList_isNull() {
                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(List.of(), null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_findEquivalence_whenLiteralsListAreTheSameUpToRenaming() {
                DerivationRule firstRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother.create("P(a, b) :- R(a, b), not(S(a))");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule.getBody(), secondRule.getBody());

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_notFindEquivalence_whenFirstLiteralsHaveHomomorphismToSecondLiterals_butNotViceversa() {
                DerivationRule firstRule = DerivationRuleMother
                        .create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother
                        .create("P(a, b) :- R(a, b), not(S(a)), R(b,b)");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule.getBody(), secondRule.getBody());

                assertThat(equivalence).isFalse();
            }

            @Test
            public void should_findEquivalence_whenThereAreHomomorphicDerivedLiterals() {
                DerivationRule firstRule = DerivationRuleMother
                        .create("""
                                P(x, y) :- R(x, y), not(S(x))
                                R(a, b) :- T(a, b)
                                """, "P");
                DerivationRule secondRule = DerivationRuleMother
                        .create("""
                                    P(x, y) :- R(x, y), not(S(x))
                                    R(a, b) :- T(a, b)
                                """, "P");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule.getBody(), secondRule.getBody());

                assertThat(equivalence).isTrue();
            }
        }
    }

    @Nested
    class LogicConstraintTest {
        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenFirstLogicConstraint_isNull() {
                LogicConstraint baseLogicConstraint = LogicConstraintMother.createWithID("@1 :- R(x, y), not(S(x))");

                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, baseLogicConstraint))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLogicConstraint_isNull() {
                LogicConstraint baseLogicConstraint = LogicConstraintMother.createWithID("@1 :- R(x, y), not(S(x))");

                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(baseLogicConstraint, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_findEquivalence_whenLogicConstraintsAreTheSameUpToRenaming() {
                LogicConstraint first = LogicConstraintMother.createWithID("@1 :- R(x, y), not(S(x))");
                LogicConstraint second = LogicConstraintMother.createWithID("@2 :- R(a, b), not(S(a))");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(first, second);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_notFindEquivalence_whenFirstLiteralsHaveHomomorphismToSecondLiterals_butNotViceversa() {
                LogicConstraint firstRule = LogicConstraintMother
                        .createWithID("@1 :- R(x, y), not(S(x))");
                LogicConstraint secondRule = LogicConstraintMother
                        .createWithID("@2 :- R(a, b), not(S(a)), R(b,b)");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule, secondRule);

                assertThat(equivalence).isFalse();
            }

            @Test
            public void should_findEquivalence_whenThereAreHomomorphicDerivedLiterals() {
                LogicConstraint firstRule = LogicConstraintMother
                        .createWithID("""
                                @1 :- R(x, y), not(S(x))
                                R(a, b) :- T(a, b)
                                """);
                LogicConstraint secondRule = LogicConstraintMother
                        .createWithID("""
                                    @2 :- R(x, y), not(S(x))
                                    R(a, b) :- T(a, b)
                                """);

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }
        }
    }

    @Nested
    class DerivationRuleTest {
        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenFirstDerivationRule_isNull() {
                DerivationRule derivationRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, derivationRule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondDerivationRule_isNull() {
                DerivationRule derivationRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                LogicEquivalenceAnalyzer logicAnalyzer = new LogicEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(derivationRule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_findEquivalence_whenLiteralsListAreTheSameUpToRenaming() {
                DerivationRule firstRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother.create("P(a, b) :- R(a, b), not(S(a))");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_notFindEquivalence_whenFirstLiteralsHaveHomomorphismToSecondLiterals_butNotViceversa() {
                DerivationRule firstRule = DerivationRuleMother
                        .create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother
                        .create("P(a, b) :- R(a, b), not(S(a)), R(b,b)");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule, secondRule);

                assertThat(equivalence).isFalse();
            }

            @Test
            public void should_findEquivalence_whenThereAreHomomorphicDerivedLiterals() {
                DerivationRule firstRule = DerivationRuleMother
                        .create("""
                                P(x, y) :- R(x, y), not(S(x))
                                R(a, b) :- T(a, b)
                                """, "P");
                DerivationRule secondRule = DerivationRuleMother
                        .create("""
                                    P(x, y) :- R(x, y), not(S(x))
                                    R(a, b) :- T(a, b)
                                """, "P");

                boolean equivalence = new LogicEquivalenceAnalyzer()
                        .areEquivalent(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }
        }
    }

}
