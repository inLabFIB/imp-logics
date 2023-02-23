package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.assertions.DerivationRuleAssert;
import edu.upc.imp.logics.assertions.LogicConstraintAssert;
import edu.upc.imp.logics.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.LogicSchema;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class LogicSchemaParserTest {

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsPredicate("p", 0);
    }

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsPredicate("p", 2);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", 0);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined_withOrdinaryLiteral_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");
    }

    @Test
    public void should_containDerivationRule_whenDerivationRuleIsDefined_withOrdinaryLiteral_withNonZeroArity() {
        String schemaString = "q(x) :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        Assertions.assertThat(derivationRulesList).hasSize(1);
        DerivationRule derivationRule = derivationRulesList.get(0);
        DerivationRuleAssert.assertThat(derivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");
    }

    @Test
    public void should_containBodyWithSeveralLiterals_whenBodyIsDefined_withSeveralLiterals() {
        String schemaString = "q(x) :- p(x, y), r(y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        Assertions.assertThat(derivationRulesList).hasSize(1);
        DerivationRule derivationRule = derivationRulesList.get(0);
        DerivationRuleAssert.assertThat(derivationRule)
                .hasBodySize(2)
                .containsOrdinaryLiteral("p", "x", "y")
                .containsOrdinaryLiteral("r", "y");
    }

    @Test
    public void should_containTwoDerivationRules_whenTwoDerivationRuleAreDefined_forTheSamePredicate() {
        String schemaString = """
                                q(x) :- p(x, y)
                                q(x) :- r(x)
                                """;

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        Assertions.assertThat(derivationRulesList).hasSize(2);
        DerivationRule firstDerivationRule = derivationRulesList.get(0);
        DerivationRuleAssert.assertThat(firstDerivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");

        DerivationRule secondDerivationRule = derivationRulesList.get(1);
        DerivationRuleAssert.assertThat(secondDerivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("r", "x");
    }

    @Test
    public void should_parseWholeSchema_whenDefiningSchema_withSeveralLogicConstraints_andSeveralDerivationRules() {
        String schemaString = """
                            @1 :- WorksIn(E, D), not(Emp(E))
                            @2 :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
                            @3 :- Dept(D), not(MinOneSpecialEmployee(D))
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
                """;

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);


        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

}
