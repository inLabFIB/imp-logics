package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivationRuleMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicConstraintMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicStructureComparatorTest {

    @Nested
    class LiteralsListTest {

        @Nested
        class InputValidationTests {
            @Test
            public void should_throwException_whenFirstLiteralsListIsNull() {
                ImmutableLiteralsList rule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(null, rule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLiteralsListIsNull() {
                ImmutableLiteralsList rule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(rule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder() {
            ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");
            ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isTrue();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiteralsInDifferentOrder() {
            ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x))");
            ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create("not(S(x)), R(x, y)");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiterals_butNotSameSign() {
            ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create("R(x, y), S(x)");
            ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiteralsInSameOrder_butTermsAreRenamed() {
            ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create("R(x, y), not(S(x))");
            ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create("R(a, b), not(S(a))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Nested
        class RecursiveTests {
            @Nested
            class InputValidationTests {
                @Test
                public void should_throwException_whenFirstLiteralsListIsNull() {
                    ImmutableLiteralsList rule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(null, rule))
                            .isInstanceOf(IllegalArgumentException.class);
                }

                @Test
                public void should_throwException_whenSecondLiteralsListIsNull() {
                    ImmutableLiteralsList rule = ImmutableLiteralsListMother.create("R(x, y), not(S(x)), x<y");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(rule, null))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Test
            public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder_AndDerivationRulesHaveSameStructure() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder_AndDerivationRulesHaveSameStructure_inDifferentOrder() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)\n" +
                                "R(x, y) :- B(x, y)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- B(x, y)\n" +
                                "R(x, y) :- A(x, y)");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder_ButNotDerivationRules() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(a, b) :- A(a, b)");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isFalse();
            }

            @Test
            public void should_returnFalse_whenLiteralsListHaveSameLiteralsInSameOrder_ButNotDerivationRulesOfDerivationRules() {
                ImmutableLiteralsList firstRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)\n" +
                                "A(x, y) :- B(x, y)");
                ImmutableLiteralsList secondRule = ImmutableLiteralsListMother.create(
                        "R(x, y), not(S(x))",
                        "R(x, y) :- A(x, y)\n" +
                                "A(x, y) :- C(x, y)");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isFalse();
            }
        }
    }

    @Nested
    class DerivationRulesTest {
        @Nested
        class InputValidationTests {
            @Test
            public void should_throwException_whenFirstLiteralsListIsNull() {
                DerivationRule rule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(null, rule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLiteralsListIsNull() {
                DerivationRule rule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(rule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder() {
            DerivationRule firstRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");
            DerivationRule secondRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isTrue();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiterals_butHeadHasDifferentName() {
            DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");
            DerivationRule secondRule = DerivationRuleMother.create("Q() :- R(x, y), not(S(x))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiterals_butHeadHasDifferentTerms() {
            DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");
            DerivationRule secondRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiterals_butNotSameSign() {
            DerivationRule firstRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");
            DerivationRule secondRule = DerivationRuleMother.create("P() :- R(x, y), not(S(x))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiteralsInDifferentOrder() {
            DerivationRule firstRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");
            DerivationRule secondRule = DerivationRuleMother.create("P(x) :- not(S(x)), R(x, y), x<y");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Test
        public void should_returnFalse_whenLiteralsListHaveSameLiteralsInSameOrder_butTermsAreRenamed() {
            DerivationRule firstRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");
            DerivationRule secondRule = DerivationRuleMother.create("P(a) :- not(S(a)), R(a, y), a<y");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(firstRule, secondRule);

            assertThat(equivalence).isFalse();
        }

        @Nested
        class RecursiveTests {
            @Nested
            class InputValidationTests {
                @Test
                public void should_throwException_whenFirstLiteralsListIsNull() {
                    DerivationRule rule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(null, rule))
                            .isInstanceOf(IllegalArgumentException.class);
                }

                @Test
                public void should_throwException_whenSecondLiteralsListIsNull() {
                    DerivationRule rule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x)), x<y");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(rule, null))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Test
            public void should_returnTrue_whenLiteralsListHaveSameLiteralsInSameOrder_andDefinitionRulesHaveSameStructure() {
                DerivationRule firstRule = DerivationRuleMother.create(
                        """
                                P(x) :- R(x, y), not(S(x)), x<y
                                R(x, y) :- A(x, y)
                                A(x, y) :- B(x, y)
                                """, "P");
                DerivationRule secondRule = DerivationRuleMother.create(
                        """
                                P(x) :- R(x, y), not(S(x)), x<y
                                R(x, y) :- A(x, y)
                                A(x, y) :- B(x, y)
                                """, "P");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_returnFalse_whenLiteralsListHaveSameLiteralsInSameOrder_andDefinitionRulesHaveDifferentStructure() {
                DerivationRule firstRule = DerivationRuleMother.create(
                        """
                                P(x) :- R(x, y), not(S(x)), x<y
                                R(x, y) :- A(x, y)
                                A(x, y) :- B(x, y)
                                """, "P");
                DerivationRule secondRule = DerivationRuleMother.create(
                        """
                                P(x) :- R(x, y), not(S(x)), x<y
                                R(x, y) :- A(x, y)
                                A(x, y) :- C(x, y)
                                """, "P");

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(firstRule, secondRule);

                assertThat(equivalence).isFalse();
            }
        }
    }

    @Nested
    class LogicConstraintTest {
        @Nested
        class InputValidationTests {
            @Test
            public void should_throwException_whenFirstLogicConstraintIsNull() {
                LogicConstraint constraint = LogicConstraintMother.createWithID("@1 :- P(x), R(1)");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(null, constraint))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenSecondLogicConstraintIsNull() {
                LogicConstraint constraint = LogicConstraintMother.createWithID("@1 :- P(x), R(1)");

                LogicStructureComparator comparator = new LogicStructureComparator();
                assertThatThrownBy(() -> comparator.haveSameStructure(constraint, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        public void should_returnTrue_whenLogicConstraintsHaveSameStructure_evenWithDifferentIDs() {
            LogicConstraint constraint1 = LogicConstraintMother.createWithID("@1 :- P(x), R(1), not(S(x)), x = x");
            LogicConstraint constraint2 = LogicConstraintMother.createWithID("@2 :- P(x), R(1), not(S(x)), x = x");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(constraint1, constraint2);

            assertThat(equivalence).isTrue();
        }

        @Test
        public void should_returnFalse_whenLogicConstraintsHaveDifferentStructure() {
            LogicConstraint constraint1 = LogicConstraintMother.createWithID("@1 :- P(x), R(1), not(S(x)), x = x");
            LogicConstraint constraint2 = LogicConstraintMother.createWithID("@1 :- P(x), R(1), not(S(x))");

            boolean equivalence = new LogicStructureComparator()
                    .haveSameStructure(constraint1, constraint2);

            assertThat(equivalence).isFalse();
        }

        @Nested
        class RecursiveTest {
            @Nested
            class InputValidationTests {
                @Test
                public void should_throwException_whenFirstLogicConstraintIsNull() {
                    LogicConstraint constraint = LogicConstraintMother.createWithID("@1 :- P(x), R(1)");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(null, constraint))
                            .isInstanceOf(IllegalArgumentException.class);
                }

                @Test
                public void should_throwException_whenSecondLogicConstraintIsNull() {
                    LogicConstraint constraint = LogicConstraintMother.createWithID("@1 :- P(x), R(1)");

                    LogicStructureComparator comparator = new LogicStructureComparator();
                    assertThatThrownBy(() -> comparator.haveSameStructureRecursively(constraint, null))
                            .isInstanceOf(IllegalArgumentException.class);
                }
            }

            @Test
            public void should_returnTrue_whenLogicConstraintHaveSameStructure() {
                LogicConstraint constraint1 = LogicConstraintMother.createWithID("""
                                 @1 :- P(x), R(1,2), not(S(x,x)), x = x
                                 P(x) :- R(x, y)
                                 R(x, y) :- T(x, y)
                                 R(x, y) :- S(x, y)
                        """);
                LogicConstraint constraint2 = LogicConstraintMother.createWithID("""
                                 @2 :- P(x), R(1,2), not(S(x,x)), x = x
                                 P(x) :- R(x, y)
                                 R(x, y) :- S(x, y)
                                 R(x, y) :- T(x, y)
                        """);

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(constraint1, constraint2);

                assertThat(equivalence).isTrue();
            }

            @Test
            public void should_returnFalse_whenLogicConstraintsHaveDifferentStructure() {
                LogicConstraint constraint1 = LogicConstraintMother.createWithID("""
                                 @1 :- P(x), R(1,2), not(S(x,x)), x = x
                                 P(x) :- R(x, y)
                                 R(x, y) :- T(x, y)
                                 R(x, y) :- S(x, y)
                        """);
                LogicConstraint constraint2 = LogicConstraintMother.createWithID("""
                                 @2 :- P(x), R(1,2), not(S(x,x)), x = x
                                 P(x) :- R(x, y)
                                 R(x, y) :- S(x, y)
                        """);

                boolean equivalence = new LogicStructureComparator()
                        .haveSameStructureRecursively(constraint1, constraint2);

                assertThat(equivalence).isFalse();
            }
        }
    }
}
