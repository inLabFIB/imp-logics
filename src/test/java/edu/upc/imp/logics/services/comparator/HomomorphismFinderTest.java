package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.comparator.assertions.SubstitutionAssert;
import edu.upc.imp.logics.services.comparator.exceptions.DerivedLiteralInHomomorphismCheck;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.DefaultTermTypeCriteria;
import edu.upc.imp.logics.services.parser.LogicSchemaParser;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomomorphismFinderTest {

    @Nested
    class LiteralsListTest {
        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenDomainLiteralsList_isNull() {
                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphismForLiteralsList(null, List.of()))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenRangeLiteralsList_isNull() {
                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphismForLiteralsList(List.of(), null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                    P() :- R(x, y), S(x)
                                    R(x, y) :- T(x, y)
                                """);
                List<Literal> domainLiteralList = domainSchema.getDerivationRulesByPredicateName("P").get(0).getBody();

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphismForLiteralsList(domainLiteralList, List.of()))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            public void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral() {
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                    P() :- R(x, y), S(x)
                                    R(x, y) :- T(x, y)
                                """);
                List<Literal> rangeLiteralsList = rangeSchema.getDerivationRulesByPredicateName("P").get(0).getBody();

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphismForLiteralsList(List.of(), rangeLiteralsList))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }
        }

        @Nested
        class FindHomomorphism {
            @Test
            public void should_notFindHomomorphism_whenLiteralsListIsNotSameUpToRenaming() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(b))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            public void should_findHomomorphism_whenLiteralsListIsSubsumedUpToRenaming() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(a)), T(a)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "a");
                SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "b");
            }

            @Test
            public void should_notFindHomomorphism_whenLiteralsListIsTheSameUpToRenaming_butLiteralsSignDoNotCoincide() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), S(x)");
                LogicSchema rangeSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }


            @Test
            public void should_findHomomorphism_whenLiteralsListRangeHasRepeatedLiterals() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P() :- R(x, y), S(x)");
                LogicSchema rangeSchema = parser.parse("P() :- R(a, b), R(c, d), S(c)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "c");
                SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "d");
            }

            @Test
            public void should_notFindHomomorphism_whenDomainLiteralsListUsesConstants() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parserEverythingIsConstant = new LogicSchemaWithIDsParser(new DefaultTermTypeCriteria());
                LogicSchema domainSchema = parserEverythingIsConstant.parse("P() :- R(1, 2), S(1)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), R(c, d), S(c)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            public void should_findHomomorphism_whenLiteralsListRangeUsesConstants() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), S(a)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(1, 2), S(1)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToConstant("a", "1");
                SubstitutionAssert.assertThat(substitution).mapsToConstant("b", "2");
            }

            @Test
            public void should_findHomomorphism_whenLiteralsListRangeRepeatsVariables() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), S(a)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, x), S(x)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToVariable("a", "x");
                SubstitutionAssert.assertThat(substitution).mapsToVariable("b", "x");
            }

            @Test
            public void should_notFindHomomorphism_whenDomainLiteralsListRepeatsVariables() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, a)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y)");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            public void should_findHomomorphism_whenLiteralsListIncludesBuiltInLiterals() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), a > b");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "a");
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "b");
            }

            @Test
            public void should_notFindHomomorphism_whenDomainLiteralsListHasBuiltIn_notInRange() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), a >= b");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            public void should_findHomomorphism_whenLiteralsListIncludesBuiltInLiterals_InvertingTheOperationAndTerms() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), b < a");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "a");
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "b");
            }
        }
    }

    @Nested
    class LogicConstraintTest {

        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenDomainRule_isNull() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema schema = parser.parse("@1 :- R(x, y), not(S(x))");

                LogicConstraint baseLogicConstraint = schema.getLogicConstraintByID(new ConstraintID("1"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(null, baseLogicConstraint))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenRangeRule_isNull() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema schema = parser.parse("@1 :- R(x, y), not(S(x))");

                LogicConstraint baseLogicConstraint = schema.getLogicConstraintByID(new ConstraintID("1"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(baseLogicConstraint, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                  @1 :- R(x, y), S(x)
                                  R(x, y) :- T(x, y)
                                """);
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("@2 :- R(x, y)");
                LogicConstraint domainLogicConstraint = domainSchema.getLogicConstraintByID(new ConstraintID("1"));
                LogicConstraint rangeLogicConstraint = rangeSchema.getLogicConstraintByID(new ConstraintID("2"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLogicConstraint, rangeLogicConstraint))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            public void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("@1 :- R(x, y)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                  @2 :- R(x, y), S(x)
                                  R(x, y) :- T(x, y)
                                """);
                LogicConstraint domainLogicConstraint = domainSchema.getLogicConstraintByID(new ConstraintID("1"));
                LogicConstraint rangeLogicConstraint = rangeSchema.getLogicConstraintByID(new ConstraintID("2"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainLogicConstraint, rangeLogicConstraint))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }
        }

        @Nested
        class FindHomomorphism {
            @Test
            public void should_findHomomorphism_whenLogicConstraintsAreTheSame_evenWithDifferentConstraintID() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("@1 :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("@2 :- R(x, y), not(S(x))");

                LogicConstraint domainLogicConstraint = domainSchema.getLogicConstraintByID(new ConstraintID("1"));
                LogicConstraint rangeLogicConstraint = rangeSchema.getLogicConstraintByID(new ConstraintID("2"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainLogicConstraint, rangeLogicConstraint);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "x");
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "y");
            }

            @Test
            public void should_findHomomorphism_whenLogicConstraintsAreTheSameUpToRenamingVariables_evenWithDifferentConstraintID() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("@1 :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("@2 :- R(a, b), not(S(a))");

                LogicConstraint domainLogicConstraint = domainSchema.getLogicConstraintByID(new ConstraintID("1"));
                LogicConstraint rangeLogicConstraint = rangeSchema.getLogicConstraintByID(new ConstraintID("2"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainLogicConstraint, rangeLogicConstraint);
                assertThat(homomorphismOpt).isPresent();
                Substitution homomorphism = homomorphismOpt.get();
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "a");
                SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "b");
            }

            @Test
            public void should_notFindHomomorphism_whenLogicConstraintsAreNotTheSameUpToRenamingVariables() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("@1 :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("@2 :- T(x)");

                LogicConstraint domainLogicConstraint = domainSchema.getLogicConstraintByID(new ConstraintID("1"));
                LogicConstraint rangeLogicConstraint = rangeSchema.getLogicConstraintByID(new ConstraintID("2"));

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainLogicConstraint, rangeLogicConstraint);
                assertThat(homomorphismOpt).isNotPresent();
            }
        }
    }

    @Nested
    class DerivationRuleTest {

        @Nested
        class ParameterCorrectness {
            @Test
            public void should_throwException_whenDomainRule_isNull() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema schema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

                DerivationRule rangeRule = schema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(null, rangeRule))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            public void should_throwException_whenRangeRule_isNull() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema schema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

                DerivationRule domainRule = schema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainRule, null))
                        .isInstanceOf(IllegalArgumentException.class);
            }


            @Test
            public void should_throwException_whenDomainsLiteralsListIncludesDerivedLiteral() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                    P() :- R(x, y), S(x)
                                    R(x, y) :- T(x, y)
                                """);
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), S(x)");
                DerivationRule domainDerivationRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeDerivationRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainDerivationRule, rangeDerivationRule))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }

            @Test
            public void should_throwException_whenRangeLiteralsListIncludesDerivedLiteral() {
                LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), S(x)");
                LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse(
                        """
                                    P() :- R(x, y), S(x)
                                    R(x, y) :- T(x, y)
                                """);
                DerivationRule domainDerivationRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeDerivationRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                assertThatThrownBy(() -> homomorphismFinder.findHomomorphism(domainDerivationRule, rangeDerivationRule))
                        .isInstanceOf(DerivedLiteralInHomomorphismCheck.class);
            }
        }

        @Nested
        class FindHomomorphism {
            @Test
            public void should_findHomomorphism_whenDerivationRulesAreTheSame() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "x");
                SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "y");
            }

            @Test
            public void should_findHomomorphism_whenDerivationRulesAreTheSameUpToRenaming() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(a))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isPresent();
                Substitution substitution = homomorphismOpt.get();
                SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "a");
                SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "b");
            }

            @Test
            public void should_notFindHomomorphism_whenDerivationRuleIsTheSameUpToRenaming_butHeadSizeDoNotCoincide() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(x) :- R(x, y), not(S(x))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }

            @Test
            public void should_notFindHomomorphism_whenDerivationRuleIsTheSameUpToRenaming_butHeadTermsDoNotCoincide() {
                LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
                LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
                LogicSchema rangeSchema = parser.parse("P(y, x) :- R(x, y), not(S(x))");

                DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
                DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

                HomomorphismFinder homomorphismFinder = new HomomorphismFinder();
                Optional<Substitution> homomorphismOpt = homomorphismFinder.findHomomorphism(domainRule, rangeRule);
                assertThat(homomorphismOpt).isNotPresent();
            }
        }
    }

}