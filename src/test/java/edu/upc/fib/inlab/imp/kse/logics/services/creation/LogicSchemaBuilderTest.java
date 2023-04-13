package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.DerivationRuleAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicConstraintAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicSchemaBuilderTest {

    @Test
    public void should_addPredicates_whenAddingSeveralPredicates_withNoDerivationRules() {
        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addPredicate("arity2", 2)
                .addPredicate("arity3", 3)
                .addPredicate("arity4", 4)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(3);
        assertThat(logicSchema.getPredicateByName("arity2").getArity()).isEqualTo(2);
        assertThat(logicSchema.getPredicateByName("arity3").getArity()).isEqualTo(3);
        assertThat(logicSchema.getPredicateByName("arity4").getArity()).isEqualTo(4);
    }

    @Test
    public void should_notAddDerivedPredicate_whenAddingSeveralPredicateWithSameNameAndArity() {
        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addPredicate("p", 2)
                .addPredicate("p", 2)
                .addPredicate("p", 2)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(1);
        assertThat(logicSchema.getPredicateByName("p")).satisfies(
                p -> assertThat(p.getArity()).isEqualTo(2)
        );
    }

    @Test
    public void should_throwsRepeatedPredicateName_whenAddingPredicateWithSameNameAndDiferentArity() {
        assertThatThrownBy(() -> LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addPredicate("p", 2)
                .addPredicate("p", 3)
        ).isInstanceOf(RepeatedPredicateName.class);
    }

    @Test
    public void should_addDerivedPredicate_whenAddingDerivationRuleSpecification() {
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder()
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addPredicate("P", 2)
                .addPredicate("Q", 2)
                .addDerivationRule(derivationRuleSpec)
                .build();

        assertThat(logicSchema.getPredicateByName("P").isDerived()).isTrue();
        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("P");
        assertThat(derivationRules).hasSize(1);
        DerivationRuleAssert.assertThat(derivationRules.get(0)).correspondsSpec(derivationRuleSpec);
    }

    @Test
    public void should_addDerivationRuleAndPredicates_whenAddDerivationRuleSpecWithNotExistentPredicates() {
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder()
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addDerivationRule(derivationRuleSpec)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(2);
        assertThat(logicSchema.getPredicateByName("P")).satisfies(
                p -> {
                    assertThat(p.isDerived()).isTrue();
                    assertThat(p.getArity()).isEqualTo(2);
                }
        );
        assertThat(logicSchema.getPredicateByName("Q")).satisfies(
                p -> {
                    assertThat(p.isDerived()).isFalse();
                    assertThat(p.getArity()).isEqualTo(2);
                }
        );
        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("P");
        assertThat(derivationRules).hasSize(1);
        DerivationRuleAssert.assertThat(derivationRules.get(0)).correspondsSpec(derivationRuleSpec);
    }

    @Test
    public void should_throwRepeatedConstraintID_whenAddingLogicConstraintSpecWithRepeatedId() {
        TermTypeCriteria termTypeCriteria = new AllVariableTermTypeCriteria();
        LogicConstraintWithIDSpec logicConstraintSpec = new LogicConstraintWithIDSpecBuilder(termTypeCriteria)
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        assertThatThrownBy(() -> LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .addLogicConstraint(logicConstraintSpec)).isInstanceOf(RepeatedConstraintID.class);
    }

    @Test
    public void should_addLogicConstraint_whenAddingLogicConstraintSpec() {
        LogicConstraintWithIDSpec logicConstraintSpec = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .build();

        assertThat(logicSchema.getAllLogicConstraints()).hasSize(1);
        assertThat(logicSchema.getAllLogicConstraints()).first().satisfies(
                lc -> LogicConstraintAssert.assertThat(lc).correspondsSpecWithId(logicConstraintSpec)
        );
    }

    @Test
    public void should_addPredicatesInLogicSchema_whenAddingLogicConstraintSpec_withPredicatesNotExplicitlyDefined() {
        LogicConstraintWithIDSpec logicConstraintSpec = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(2);
        assertThat(logicSchema.getPredicateByName("P")).satisfies(
                p -> assertThat(p.getArity()).isEqualTo(2)
        );
        assertThat(logicSchema.getPredicateByName("Q")).satisfies(
                p -> assertThat(p.getArity()).isEqualTo(2)
        );
    }

    @Test
    public void should_createSchema_whenDefiningSchema() {
//            :- WorksIn(E, D), not(Emp(E))
//            :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
//            :- Dept(D), not(MinOneSpecialEmployee(D))
//            MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
//            MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
//            % Existing but unused predicates: Project(p)
        LogicConstraintWithIDSpec logicConstraint1 = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("1")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintWithIDSpec logicConstraint2 = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("2")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Manages", "E", "D")
                .addOrdinaryLiteral("CrucialDept", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintWithIDSpec logicConstraint3 = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("3")
                .addOrdinaryLiteral("Dept", "D")
                .addOrdinaryLiteral("MinOneSpecialEmployee", false, "D")
                .build();

        DerivationRuleSpec derivationRule1 = new DerivationRuleSpecBuilder()
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Happy", "E")
                .build();

        DerivationRuleSpec derivationRule2 = new DerivationRuleSpecBuilder()
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addNegatedOrdinaryLiteral("Rich", "E")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraint1, logicConstraint2, logicConstraint3)
                .addDerivationRule(derivationRule1, derivationRule2)
                .addPredicate("Project", 1)
                .build();

        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy", "Project")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

    @Test
    public void should_addDifferentConstraintIDs_whenDefiningLogicConstraints_withoutIDs() {
        LogicConstraintWithoutIDSpec logicConstraintSpec1 = new LogicConstraintWithoutIDSpecBuilder()
                .addOrdinaryLiteral("P")
                .build();

        LogicConstraintWithoutIDSpec logicConstraintSpec2 = new LogicConstraintWithoutIDSpecBuilder()
                .addOrdinaryLiteral("R")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder()
                .addLogicConstraint(logicConstraintSpec1, logicConstraintSpec2)
                .build();

        LogicSchemaAssert.assertThat(logicSchema).containsExactlyTheseConstraintIDs("1", "2");

        LogicConstraint logicConstraint1 = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint1).containsOrdinaryLiteral("P");
        LogicConstraint logicConstraint2 = logicSchema.getLogicConstraintByID(new ConstraintID("2"));
        LogicConstraintAssert.assertThat(logicConstraint2).containsOrdinaryLiteral("R");
    }

    @Test
    public void should_addNormalClauseWithBuiltIn_whenSpecContainsBuiltIn() {
        LogicConstraintWithIDSpec logicConstraintSpec1 = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("1")
                .addOrdinaryLiteral("P")
                .addBuiltInLiteral("<", "a", "b")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintSpec1)
                .build();

        LogicConstraint logicConstraint1 = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint1).containsOrdinaryLiteral("P");
        LogicConstraintAssert.assertThat(logicConstraint1).containsComparisonBuiltInLiteral("<", "a", "b");
    }


}