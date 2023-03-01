package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.comparator.assertions.SubstitutionAssert;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.logics.services.parser.LogicSchemaParser;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomomorphismCheckerForDerivationRulesTest {

    /*
     * Tests for ensuring the correctness of the parameters
     */
    @Test
    public void should_throwException_whenDomainRule_isNull() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema schema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

        DerivationRule domainRule = null;
        DerivationRule rangeRule = schema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        assertThatThrownBy(() -> homomorphismChecker.computeHomomorphism(domainRule, rangeRule))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_whenRangeRule_isNull() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema schema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

        DerivationRule domainRule = schema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = null;

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        assertThatThrownBy(() -> homomorphismChecker.computeHomomorphism(domainRule, rangeRule))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /*
     * Tests for the cases in which every predicate is base
     */
    @Test
    public void should_findHomomorphism_whenNormalClauseHaveTheSameNames() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "x");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "y");
    }

    @Test
    public void should_findHomomorphism_whenNormalClauseIsTheSameUpToRenaming() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(a))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "b");
    }

    @Test
    public void should_notFindHomomorphism_whenNormalClauseIsTheNotSameUpToRenaming() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(b))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_findHomomorphism_whenNormalClauseIsSubsumedUpToRenaming() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(a, b) :- R(a, b), not(S(a)), T(a)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "b");
    }

    @Test
    public void should_notFindHomomorphism_whenNormalClauseIsTheSameUpToRenaming_whenHeadIsNotSamePredicate() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("Q(x, y) :- R(x, y), not(S(x))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("Q").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_notFindHomomorphism_whenNormalClauseIsTheSameUpToRenaming_butHeadSizeDoNotCoincide() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(x) :- R(x, y), not(S(x))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_notFindHomomorphism_whenNormalClauseIsTheSameUpToRenaming_butHeadTermsDoNotCoincide() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), not(S(x))");
        LogicSchema rangeSchema = parser.parse("P(y, x) :- R(x, y), not(S(x))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_notFindHomomorphism_whenNormalClauseIsTheSameUpToRenaming_butLiteralsSignDoNotCoincide() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P(x, y) :- R(x, y), S(x)");
        LogicSchema rangeSchema = parser.parse("P(y, x) :- R(x, y), not(S(x))");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    /*
     * Non-happy path tests
     */

    @Test
    public void should_findHomomorphism_whenRangeHasRepeatedLiterals() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();
        LogicSchema domainSchema = parser.parse("P() :- R(x, y), S(x)");
        LogicSchema rangeSchema = parser.parse("P() :- R(a, b), R(c, d), S(c)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "c");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "d");
    }

    @Test
    public void should_notFindHomomorphism_whenDomainUsesConstants() {
        LogicSchemaParser<LogicConstraintWithIDSpec> parserEverythingIsConstant = new LogicSchemaWithIDsParser(new StringToTermSpecFactory() {
            @Override
            protected boolean isConstant(String name) {
                return true;
            }

            @Override
            protected boolean isVariable(String name) {
                return false;
            }
        });
        LogicSchema domainSchema = parserEverythingIsConstant.parse("P() :- R(x, y), S(x)");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), R(c, d), S(c)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_findHomomorphism_whenRangeUsesConstants() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), S(a)");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(1, 2), S(1)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToConstant("a", "1");
        SubstitutionAssert.assertThat(substitution).mapsToConstant("b", "2");
    }

    @Test
    public void should_findHomomorphism_whenRangeRepeatsVariables() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), S(a)");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, x), S(x)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution substitution = homomorphismOpt.get();
        SubstitutionAssert.assertThat(substitution).mapsToVariable("a", "x");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("b", "x");
    }

    @Test
    public void should_notFindHomomorphism_whenDomainRepeatsVariables() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, a)");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y)");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_findHomomorphism_whenIncludingBuiltInLiterals() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), a > b");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution homomorphism = homomorphismOpt.get();
        SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "b");
    }

    @Test
    public void should_notFindHomomorphism_whenDomainHasBuiltIn_notInRange() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), a >= b");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isNotPresent();
    }

    @Test
    public void should_findHomomorphism_whenIncludingBuiltInLiterals_InvertingTheOperationAndTerms() {
        LogicSchema domainSchema = new LogicSchemaWithIDsParser().parse("P() :- R(x, y), x > y");
        LogicSchema rangeSchema = new LogicSchemaWithIDsParser().parse("P() :- R(a, b), b < a");

        DerivationRule domainRule = domainSchema.getDerivationRulesByPredicateName("P").get(0);
        DerivationRule rangeRule = rangeSchema.getDerivationRulesByPredicateName("P").get(0);

        HomomorphismChecker homomorphismChecker = new HomomorphismChecker();
        Optional<Substitution> homomorphismOpt = homomorphismChecker.computeHomomorphism(domainRule, rangeRule);
        assertThat(homomorphismOpt).isPresent();
        Substitution homomorphism = homomorphismOpt.get();
        SubstitutionAssert.assertThat(homomorphism).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(homomorphism).mapsToVariable("y", "b");
    }


    /* Tests for the cases including derived predicates */

    //TODO
}