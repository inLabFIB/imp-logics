package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.DerivationRuleMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicConstraintMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.DerivedLiteralInHomomorphismCheck;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static edu.upc.fib.inlab.imp.kse.logics.services.comparator.assertions.SubstitutionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomomorphismFinderTest {

    @Nested
    class ImmutableLiteralsListTest {
        @Nested
        class ParameterCorrectness {
            @Test
            void should_throwException_whenDomainLiteralsList_isNull() {
                List<Literal> domainLiterals = null;
                List<Literal> rangeLiterals = List.of();
                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenRangeLiteralsList_isNull() {
                List<Literal> rangeLiterals = null;
                List<Literal> domainLiterals = List.of();
                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenInitialSubstitution_isNull() {
                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                List<Literal> domainLiterals = List.of();
                List<Literal> rangeLiterals = List.of();
                Substitution initialSubstitution = null;
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals, initialSubstitution))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral_andThereIsNoDerivedLiteralCriteria() {
                ImmutableLiteralsList domainLiteralList = ImmutableLiteralsListMother.create("R(x, y), S(x)",
                        "R(x, y) :- T(x, y)");
                List<Literal> rangeLiterals = List.of();

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLiteralList, rangeLiterals))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral_andThereIsNoDerivedLiteralCriteria() {
                List<Literal> domainLiterals = List.of();
                ImmutableLiteralsList rangeLiteralsList = ImmutableLiteralsListMother.create("R(x, y), S(x)",
                        "R(x, y) :- T(x, y)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLiterals, rangeLiteralsList))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }


        }

        @Nested
        class FindHomomorphism {
            @Test
            void should_notFindHomomorphism_whenLiteralsListIsNotSameUpToRenaming() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), not(S(x))");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create(" R(a, b), not(S(b))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            void should_findHomomorphism_whenLiteralsListIsSubsumedUpToRenaming() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x, y) :- R(x, y), not(S(x))");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(a, b) :- R(a, b), not(S(a)), T(a)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToVariable("x", "a")
                        .mapsToVariable("y", "b");
            }

            @Test
            void should_notFindHomomorphism_whenLiteralsListIsTheSameUpToRenaming_butLiteralsSignDoNotCoincide() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), S(x)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(a, b) :- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isNotPresent();
            }


            @Test
            void should_findHomomorphism_whenLiteralsListRangeHasRepeatedLiterals() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), S(x)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(a, b), R(c, d), S(c)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToVariable("x", "c")
                        .mapsToVariable("y", "d");
            }

            @Test
            void should_notFindHomomorphism_whenDomainLiteralsListUsesConstants() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(1, 2), S(1)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(a, b), R(c, d), S(c)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            void should_findHomomorphism_whenLiteralsListRangeUsesConstants() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(a, b), S(a)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(1, 2), S(1)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToConstant("a", "1")
                        .mapsToConstant("b", "2");
            }

            @Test
            void should_findHomomorphism_whenLiteralsListRangeRepeatsVariables() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(a, b), S(a)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(x, x), S(x)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToVariable("a", "x")
                        .mapsToVariable("b", "x");
            }

            @Test
            void should_notFindHomomorphism_whenDomainLiteralsListRepeatsVariables() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create(" R(a, a)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(x, y)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            void should_findHomomorphism_whenLiteralsListIncludesBuiltInLiterals() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), x > y");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(a, b), a > b");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                assertThat(homomorphism)
                        .mapsToVariable("x", "a")
                        .mapsToVariable("y", "b");
            }

            @Test
            void should_notFindHomomorphism_whenDomainLiteralsListHasBuiltIn_notInRange() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), x > y");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(a, b), a >= b");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            void should_findHomomorphism_whenLiteralsListIncludesBuiltInLiterals_InvertingTheOperationAndTerms() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("R(x, y), x > y");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("R(a, b), b < a");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                assertThat(homomorphism)
                        .mapsToVariable("x", "a")
                        .mapsToVariable("y", "b");
            }

            @Test
            void should_findHomomorphism_whenLiteralsListIncludesBuiltInLiterals_InvertingTheTermsOfEqualities() {
                ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create("1 = y, T(x, b)");
                ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create("T(x, b), y = 1");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);
                assertThat(homomorphismOpt).isPresent();
            }

            @Test
            void should_findHomomorphism_whenLiteralsListIncludesEqualities_ThatCreatesSeveralPossibleHomomorphism() {
                ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("z = x, T(x, y)");
                ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("T(x, y), x = z");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                assertThat(homomorphismOpt).isPresent();
            }

            @Nested
            class SameNameDerivationRuleCriteria {
                @Test
                void should_findHomomorphism_whenUsingSameName() {
                    ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x), Derived(x)", "Derived(x) :- Q(x)");
                    ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(x), Derived(x)", "Derived(x) :- Q(x)");

                    HomomorphismFinder homomorphismFinder = new HomomorphismFinder(new SamePredicateNameCriteria());
                    Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                    assertThat(homomorphismOpt).isPresent();
                }

                @Test
                void should_notFindHomomorphism_whenUsingSameName_butDifferentPolarity() {
                    ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x), Derived(x)", "Derived(x) :- Q(x)");
                    ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(x), not(Derived(x))", "Derived(x) :- Q(x)");

                    HomomorphismFinder homomorphismFinder = new HomomorphismFinder(new SamePredicateNameCriteria());
                    Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                    assertThat(homomorphismOpt).isNotPresent();
                }

                @Test
                void should_notFindHomomorphism_whenUsingDifferentNames() {
                    ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x), Derived1(x)", "Derived1(x) :- Q(x)");
                    ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(x), Derived2(x)", "Derived2(x) :- Q(x)");

                    HomomorphismFinder homomorphismFinder = new HomomorphismFinder(new SamePredicateNameCriteria());
                    Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList);
                    assertThat(homomorphismOpt).isNotPresent();
                }

                @Test
                void should_findHomomorphism_whenGivingInitialCompatibleHomomorphism() {
                    ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                    ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(x), Q(x, y)");

                    HomomorphismFinder homomorphismFinder = new HomomorphismFinder(new SamePredicateNameCriteria());
                    Substitution substitution = new Substitution();
                    substitution.addMapping(new Variable("x"), new Variable("x"));
                    Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList, substitution);
                    assertThat(homomorphismOpt).isPresent();
                }

                @Test
                void should_notFindHomomorphism_whenGivingInitialIncompatibleHomomorphism() {
                    ImmutableLiteralsList domainList = ImmutableLiteralsListMother.create("P(x), Q(x, y)");
                    ImmutableLiteralsList rangeList = ImmutableLiteralsListMother.create("P(x), Q(x, y)");

                    HomomorphismFinder homomorphismFinder = new HomomorphismFinder(new SamePredicateNameCriteria());
                    Substitution substitution = new Substitution();
                    substitution.addMapping(new Variable("x"), new Variable("y"));
                    Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainList, rangeList, substitution);
                    assertThat(homomorphismOpt).isNotPresent();
                }
            }
        }
    }

    @Nested
    class LogicConstraintTest {

        @Nested
        class ParameterCorrectness {
            @Test
            void should_throwException_whenDomainRule_isNull() {
                LogicConstraint baseLogicConstraint = LogicConstraintMother.createWithoutID(":- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(null, baseLogicConstraint))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenRangeRule_isNull() {
                LogicConstraint baseLogicConstraint = LogicConstraintMother.createWithoutID(":- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(baseLogicConstraint, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral() {
                LogicConstraint domainConstraint = LogicConstraintMother.createWithoutID("""
                          :- R(x, y), S(x)
                          R(x, y) :- T(x, y)
                        """);
                LogicConstraint rangeConstraint = LogicConstraintMother.createWithoutID(":- R(x, y)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainConstraint, rangeConstraint))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral() {
                LogicConstraint domainConstraint = LogicConstraintMother.createWithoutID("""
                        :- R(x, y)
                        """);
                LogicConstraint rangeConstraint = LogicConstraintMother.createWithoutID("""
                        :- R(x, y), S(x)
                        R(x, y) :- T(x, y)
                        """);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainConstraint, rangeConstraint))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }
        }

        @Nested
        class FindHomomorphism {
            @Test
            void should_findHomomorphism_whenLogicConstraintsAreTheSame_evenWithDifferentConstraintID() {
                LogicConstraint domainConstraint = LogicConstraintMother.createWithID("""
                        @1 :- R(x, y), not(S(x))
                        """);
                LogicConstraint rangeConstraint = LogicConstraintMother.createWithID("""
                        @2 :- R(x, y), not(S(x))
                        """);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainConstraint, rangeConstraint);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                assertThat(homomorphism)
                        .mapsToVariable("x", "x")
                        .mapsToVariable("y", "y");
            }

            @Test
            void should_findHomomorphism_whenLogicConstraintsAreTheSameUpToRenamingVariables_evenWithDifferentConstraintID() {
                LogicConstraint domainConstraint = LogicConstraintMother.createWithID("""
                        @1 :- R(x, y), not(S(x))
                        """);
                LogicConstraint rangeConstraint = LogicConstraintMother.createWithID("""
                        @2 :- R(a, b), not(S(a))
                        """);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainConstraint, rangeConstraint);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                assertThat(homomorphism)
                        .mapsToVariable("x", "a")
                        .mapsToVariable("y", "b");
            }

            @Test
            void should_notFindHomomorphism_whenLogicConstraintsAreNotTheSameUpToRenamingVariables() {
                LogicConstraint domainConstraint = LogicConstraintMother.createWithoutID("""
                        :- R(x, y), not(S(x))
                        """);
                LogicConstraint rangeConstraint = LogicConstraintMother.createWithoutID("""
                        :- T(x)
                        """);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainConstraint, rangeConstraint);
                assertThat(homomorphismOpt).isNotPresent();
            }
        }
    }

    @Nested
    class DerivationRuleTest {

        @Nested
        class ParameterCorrectness {
            @Test
            void should_throwException_whenDomainRule_isNull() {
                DerivationRule rangeRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(null, rangeRule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void should_throwException_whenRangeRule_isNull() {
                DerivationRule domainRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainRule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }


            @Test
            void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral() {
                DerivationRule domainDerivationRule = DerivationRuleMother.create("""
                        P() :- R(x, y), S(x)
                        R(x, y) :- T(x, y)
                        """, "P");
                DerivationRule rangeDerivationRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainDerivationRule, rangeDerivationRule))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral() {
                DerivationRule domainRule = DerivationRuleMother.create("P() :- R(x, y), S(x)");
                DerivationRule rangeRule = DerivationRuleMother.create(
                        """
                                    P() :- R(x, y), S(x)
                                    R(x, y) :- T(x, y)
                                """,
                        "P");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainRule, rangeRule))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }
        }

        @Nested
        class FindHomomorphism {
            @Test
            void should_findHomomorphism_whenDerivationRulesAreTheSame() {
                DerivationRule domainRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule rangeRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToVariable("x", "x")
                        .mapsToVariable("y", "y");
            }

            @Test
            void should_findHomomorphism_whenDerivationRulesAreTheSameUpToRenaming() {
                DerivationRule domainRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule rangeRule = DerivationRuleMother.create("P(a, b) :- R(a, b), not(S(a))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                assertThat(substitution)
                        .mapsToVariable("x", "a")
                        .mapsToVariable("y", "b");
            }

            @Test
            void should_notFindHomomorphism_whenDerivationRuleIsTheSameUpToRenaming_butHeadSizeDoNotCoincide() {
                DerivationRule domainRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule rangeRule = DerivationRuleMother.create("P(x) :- R(x, y), not(S(x))");


                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            void should_notFindHomomorphism_whenDerivationRuleIsTheSameUpToRenaming_butHeadTermsDoNotCoincide() {
                DerivationRule domainRule = DerivationRuleMother.create("P(x, y) :- R(x, y), not(S(x))");
                DerivationRule rangeRule = DerivationRuleMother.create("P(y, x) :- R(x, y), not(S(x))");

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }
        }
    }

}