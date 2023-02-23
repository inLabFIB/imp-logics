package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.assertions.DerivationRuleAssert;
import edu.upc.imp.logics.assertions.LogicConstraintAssert;
import edu.upc.imp.logics.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.imp.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.DefaultStringToTermSpecFactory;
import edu.upc.imp.logics.services.creation.spec.helpers.DerivationRuleSpecBuilder;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicConstraintSpecBuilder;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicSchemaBuilderTest {

    @Test
    public void should_addPredicates_whenAddingSeveralPredicates_withNoDerivationRules() {
        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addPredicate("arity2", 2)
                .addPredicate("arity3", 3)
                .addPredicate("arity4", 4)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(3);
        assertThat(logicSchema.getPredicateByName("arity2").getArity().getNumber()).isEqualTo(2);
        assertThat(logicSchema.getPredicateByName("arity3").getArity().getNumber()).isEqualTo(3);
        assertThat(logicSchema.getPredicateByName("arity4").getArity().getNumber()).isEqualTo(4);
    }

    @Test
    public void should_notAddDerivedPredicate_whenAddingSeveralPredicateWithSameNameAndArity() {
        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addPredicate("p", 2)
                .addPredicate("p", 2)
                .addPredicate("p", 2)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(1);
        assertThat(logicSchema.getPredicateByName("p")).satisfies(
                p -> assertThat(p.getArity().getNumber()).isEqualTo(2)
        );
    }

    @Test
    public void should_throwsRepeatedPredicateName_whenAddingPredicateWithSameNameAndDiferentArity() {
        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addPredicate("p", 2)
                .addPredicate("p", 3)
        ).isInstanceOf(RepeatedPredicateName.class);
    }

    @Test
    public void should_addDerivedPredicate_whenAddingDerivationRuleSpecification() {
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
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
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addDerivationRule(derivationRuleSpec)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(2);
        assertThat(logicSchema.getPredicateByName("P")).satisfies(
                p -> {
                    assertThat(p.isDerived()).isTrue();
                    assertThat(p.getArity().getNumber()).isEqualTo(2);
                }
        );
        assertThat(logicSchema.getPredicateByName("Q")).satisfies(
                p -> {
                    assertThat(p.isDerived()).isFalse();
                    assertThat(p.getArity().getNumber()).isEqualTo(2);
                }
        );
        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("P");
        assertThat(derivationRules).hasSize(1);
        DerivationRuleAssert.assertThat(derivationRules.get(0)).correspondsSpec(derivationRuleSpec);
    }

    @Test
    public void should_throwRepeatedConstraintID_whenAddingLogicConstraintSpecWithRepeatedId() {
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .addLogicConstraint(logicConstraintSpec)).isInstanceOf(RepeatedConstraintID.class);
    }

    @Test
    public void should_addLogicConstraint_whenAddingLogicConstraintSpec() {
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .build();

        assertThat(logicSchema.getAllLogicConstraints()).hasSize(1);
        assertThat(logicSchema.getAllLogicConstraints()).first().satisfies(
                lc -> LogicConstraintAssert.assertThat(lc).correspondsSpec(logicConstraintSpec)
        );
    }

    @Test
    public void should_addPredicatesInLogicSchema_whenAddingLogicConstraintSpec_withPredicatesNotExplicitlyDefined() {
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(2);
        assertThat(logicSchema.getPredicateByName("P")).satisfies(
                p -> assertThat(p.getArity().getNumber()).isEqualTo(2)
        );
        assertThat(logicSchema.getPredicateByName("Q")).satisfies(
                p -> assertThat(p.getArity().getNumber()).isEqualTo(2)
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
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        LogicConstraintSpec logicConstraint1 = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("1")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintSpec logicConstraint2 = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("2")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Manages", "E", "D")
                .addOrdinaryLiteral("CrucialDept", "D")
                .addNegatedOrdinaryLiteral("Emp", "E")
                .build();

        LogicConstraintSpec logicConstraint3 = new LogicConstraintSpecBuilder(termFactory)
                .addConstraintId("3")
                .addOrdinaryLiteral("Dept", "D")
                .addOrdinaryLiteral("MinOneSpecialEmployee", false, "D")
                .build();

        DerivationRuleSpec derivationRule1 = new DerivationRuleSpecBuilder(termFactory)
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addOrdinaryLiteral("Happy", "E")
                .build();

        DerivationRuleSpec derivationRule2 = new DerivationRuleSpecBuilder(termFactory)
                .addHead("MinOneSpecialEmployee", "D")
                .addOrdinaryLiteral("WorksIn", "E", "D")
                .addNegatedOrdinaryLiteral("Rich", "E")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addLogicConstraints(logicConstraint1, logicConstraint2, logicConstraint3)
                .addDerivationRules(derivationRule1, derivationRule2)
                .addPredicate("Project", 1)
                .build();

        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy", "Project")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }


}