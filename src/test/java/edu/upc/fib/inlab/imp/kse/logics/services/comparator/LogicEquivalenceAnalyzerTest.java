package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivationRuleMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicConstraintMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicEquivalenceAnalyzerTest {

    @Nested
    class DefaultConstructorUseExtendedHomomorphismFinder {
        @Test
        public void should_returnTrue_whenThereAreHomomorphicDerivedLiterals() {
            ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                    .create("R(x, y), not(S(x))", "R(a, b) :- T(a, b)");
            ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                    .create("R2(x2, y), not(S(x2))", "R2(a, b) :- T(a, b)");

            LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
            Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

            assertThat(equivalence).contains(true);
        }
    }

    @Nested
    class LiteralsListTest {
        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenFirstLiteralsList_isNull() {
                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, List.of()))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLiteralsList_isNull() {
                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(List.of(), null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_returnTrue_whenHomomorphismIsFoundBidirectionally() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x))");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create("R(a, b), not(S(a))");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence).contains(true);
            }

            @Test
            public void should_returnFalse_whenHomomorphismIsNotFoundBidirectionally_and_AllLiteralsAreBaseAndPositive() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                        .create("R(x, y), S(x)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                        .create("R(a, b), S(a), R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .contains(false);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreNegatedLiterals() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                        .create("R(x, y), not(S(x))");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                        .create("R(a, b), not(S(a)), R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreBuiltInLiterals() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                        .create("R(x, y), x < y");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                        .create("R(a, b), a < b, R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreDerivedLiterals() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                        .create("R(x, y)", "R(a, b) :- T(a, b)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                        .create("R(x, x)", "R(a, a) :- T(a, a)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsFound_inOneDirection_butNotViceversa() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother
                        .create("R(x, y), not(S(x))");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother
                        .create("R(a, b), not(S(a)), R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
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

                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, baseLogicConstraint))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLogicConstraint_isNull() {
                LogicConstraint baseLogicConstraint = LogicConstraintMother.createWithID("@1 :- R(x, y), not(S(x))");

                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(baseLogicConstraint, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_returnTrue_whenHomomorphismIsFoundBidirectionally() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("@1 :- R(x, y), not(S(x))");
                LogicConstraint secondRule = LogicConstraintMother.createWithID("@2 :- R(a, b), not(S(a))");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence).contains(true);
            }

            @Test
            public void should_returnFalse_whenHomomorphismIsNotFoundBidirectionally_and_AllLiteralsAreBaseAndPositive() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("""
                            @1 :- R(x, y), S(x)
                        """);
                LogicConstraint secondRule = LogicConstraintMother.createWithID("""
                            @2 :- R(a, b), S(a), R(b,b)
                        """);

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .contains(false);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreNegatedLiterals() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("""
                            @1 :- R(x, y), not(S(x))
                        """);
                LogicConstraint secondRule = LogicConstraintMother.createWithID("""
                            @2 :- R(a, b), not(S(a)), R(b,b)
                        """);

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreBuiltInLiterals() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("""
                            @1 :- R(x, y), x < y
                        """);
                LogicConstraint secondRule = LogicConstraintMother.createWithID("""
                            @2 :- R(a, b), a < b, R(b,b)
                        """);

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreDerivedLiterals() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("""
                            @1 :- R(x, y)
                            R(a, b) :- T(a, b)
                        """);
                LogicConstraint secondRule = LogicConstraintMother.createWithID("""
                            @2 :- R(x, x)
                            R(a, a) :- T(a, a)
                        """);

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsFound_inOneDirection_butNotViceversa() {
                LogicConstraint firstRule = LogicConstraintMother.createWithID("""
                            @1 :- R(x, y), not(S(x))
                        """);
                LogicConstraint secondRule = LogicConstraintMother.createWithID("""
                            @2 :- R(a, b), not(S(a)), R(b,b), T(a, b)
                        """);

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
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
                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(null, derivationRule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondDerivationRule_isNull() {
                DerivationRule derivationRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                LogicEquivalenceAnalyzer logicAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                assertThatThrownBy(() -> logicAnalyzer.areEquivalent(derivationRule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class EquivalenceAnalysis {
            @Test
            public void should_returnTrue_whenHomomorphismIsFoundBidirectionally() {
                DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother.create("P() :- R(a, b), not(S(a))");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence).contains(true);
            }

            @Test
            public void should_returnFalse_whenHomomorphismIsNotFoundBidirectionally_and_AllLiteralsAreBaseAndPositive() {
                DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");
                DerivationRule secondRule = DerivationRuleMother.create("P() :- R(a, b), S(a), R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .contains(false);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreNegatedLiterals() {
                DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother.create("P() :- R(a, b), not(S(a)), R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreBuiltInLiterals() {
                DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), x < y");
                DerivationRule secondRule = DerivationRuleMother.create("P() :- R(a, b), a < b, R(b,b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsNotFoundBidirectionally_and_thereAreDerivedLiterals() {
                DerivationRule firstRule = DerivationRuleMother.create("""
                            P() :- R(x, y)
                            R(a, b) :- T(a, b)
                        """, "P");
                DerivationRule secondRule = DerivationRuleMother.create("""
                            P() :- R(x, x)
                            R(a, a) :- T(a, a)
                        """, "P");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }

            @Test
            public void should_returnUnknown_whenHomomorphismIsFound_inOneDirection_butNotViceversa() {
                DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), not(S(x))");
                DerivationRule secondRule = DerivationRuleMother.create("P() :- R(a, b), not(S(a)), R(b,b), T(a, b)");

                LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();
                Optional<Boolean> equivalence = logicEquivalenceAnalyzer.areEquivalent(firstRule, secondRule);

                assertThat(equivalence)
                        .describedAs("Unknown expected")
                        .isEqualTo(UNKNOWN);
            }
        }
    }

}
