package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicSchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.TermTypeCriteria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogicSchemaFactoryTest {

    @Test
    void should_createSchema_whenDefiningSchema() {
        //            :- WorksIn(E, D), not(Emp(E))
        //            :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
        //            :- Dept(D), not(MinOneSpecialEmployee(D))
        //            MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
        //            MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
        //            % Existing but unused predicates: Project(p)
        TermTypeCriteria termTypeCriteria = new AllVariableTermTypeCriteria();
        LogicConstraintWithIDSpec logicConstraint1 = new LogicConstraintWithIDSpecBuilder(termTypeCriteria)
                .addConstraintId("1")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintWithIDSpec logicConstraint2 = new LogicConstraintWithIDSpecBuilder(termTypeCriteria)
                .addConstraintId("2")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Manages", "E", "D")
                .addOrdinaryLiteral("CrucialDept", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintWithIDSpec logicConstraint3 = new LogicConstraintWithIDSpecBuilder(termTypeCriteria)
                .addConstraintId("3")
                .addOrdinaryLiteral("Dept", "D")
                .addOrdinaryLiteral("MinOneSpecialEmployee", false, "D")
                .build();

        DerivationRuleSpec derivationRule1 = new DerivationRuleSpecBuilder(termTypeCriteria)
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Happy", "E")
                .build();

        DerivationRuleSpec derivationRule2 = new DerivationRuleSpecBuilder(termTypeCriteria)
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Rich", false, "E")
                .build();

        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec = new LogicSchemaSpec<>();
        logicSchemaSpec.addLogicConstraintSpecs(logicConstraint1, logicConstraint2, logicConstraint3);
        logicSchemaSpec.addDerivationRuleSpecs(derivationRule1, derivationRule2);
        logicSchemaSpec.addPredicateSpecs(new PredicateSpec("Project", 1));

        LogicSchema logicSchema = LogicSchemaFactory.defaultLogicSchemaWithIDsFactory().createLogicSchema(logicSchemaSpec);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy", "Project")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

    @Test
    void should_createDifferentSchema_whenReusingTheSameFactory_withDifferentSpec() {
        //Arrange
        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec1 = new LogicSchemaSpec<>();
        logicSchemaSpec1.addPredicateSpecs(new PredicateSpec("P", 2));

        LogicSchemaFactory<LogicConstraintWithIDSpec> factory = LogicSchemaFactory.defaultLogicSchemaWithIDsFactory();
        factory.createLogicSchema(logicSchemaSpec1);

        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec2 = new LogicSchemaSpec<>();
        logicSchemaSpec2.addPredicateSpecs(new PredicateSpec("Q", 2));

        //Action
        LogicSchema logicSchema2 = factory.createLogicSchema(logicSchemaSpec2);

        //Assert
        LogicSchemaAssert.assertThat(logicSchema2).containsExactlyThesePredicateNames("Q");
    }

}
