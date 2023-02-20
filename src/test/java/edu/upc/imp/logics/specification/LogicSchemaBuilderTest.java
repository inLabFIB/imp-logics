package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.assertions.DerivationRuleAssert;
import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.imp.logics.schema.exceptions.RepeatedPredicateName;
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
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addPredicate("P", 2)
                .addPredicate("Q", 2)
                .addDerivationRuleSpec(derivationRuleSpec)
                .build();

        assertThat(logicSchema.getPredicateByName("P").isDerived()).isTrue();
        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("P");
        assertThat(derivationRules).hasSize(1);
        DerivationRuleAssert.assertThat(derivationRules.get(0)).correspondsSpec(derivationRuleSpec);
    }

    @Test
    public void should_addDerivationRuleAndPredicates_whenAddDerivationRuleSpecWithNotExistentPredicates() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addDerivationRuleSpec(derivationRuleSpec)
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
    public void should_throwsArityMismatch_whenAddDerivationRuleWithExistentPredicateNameAndDifferentArityInHead() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addPredicate("P", 3)
                .addDerivationRuleSpec(derivationRuleSpec)).isInstanceOf(ArityMismatch.class);
    }

    @Test
    public void should_throwsArityMismatch_whenAddDerivationRule_withExistentPredicateNameAndDifferentArityInLiteral() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpecBuilder(termFactory)
                .addHead("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addPredicate("Q", 3)
                .addDerivationRuleSpec(derivationRuleSpec)).isInstanceOf(ArityMismatch.class);
    }

    @Test
    public void should_throwRepeatedConstraintID_whenAddingLogicConstraintSpecWithRepeatedId() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addLogicConstraint("@1", logicConstraintSpec)
                .addLogicConstraint("@1", logicConstraintSpec)).isInstanceOf(RepeatedConstraintID.class);
    }

    @Test
    public void should_addLogicConstraint_whenAddingLogicConstraintSpec() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addLogicConstraint("@1", logicConstraintSpec)
                .build();

        assertThat(logicSchema.getAllLogicConstraints()).hasSize(1);
        assertThat(logicSchema.getAllLogicConstraints()).first().satisfies(
                logicConstraint -> assertThat(logicConstraint.getBody()).hasSize(2)
        );
        assertThat(logicSchema.getAllPredicates()).hasSize(2);
        assertThat(logicSchema.getPredicateByName("P")).satisfies(
                p -> assertThat(p.getArity().getNumber()).isEqualTo(2)
        );
        assertThat(logicSchema.getPredicateByName("Q")).satisfies(
                p -> assertThat(p.getArity().getNumber()).isEqualTo(2)
        );
    }

    @Test
    public void should_throwArityMismatch_whenAddingLogicConstraintSpecWithExistentPredicateNameAndDiferentArity() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        LogicConstraintSpec logicConstraintSpec = new LogicConstraintSpecBuilder(termFactory)
                .addOrdinaryLiteral("P", "x", "y")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addPredicate("P", 3)
                .addLogicConstraint("@1", logicConstraintSpec)).isInstanceOf(ArityMismatch.class);
    }

    @Test
    public void should_throwArityMismatch_whenAddingTwoLogicConstraintSpecWithSamePredicateNameAndDiferentArity() {
        StringToTermFactory termFactory = new DefaultStringToTermFactory();
        LogicConstraintSpec logicConstraintSpec1 = new LogicConstraintSpecBuilder(termFactory)
                .addOrdinaryLiteral("P", "x", "y")
                .build();

        LogicConstraintSpec logicConstraintSpec2 = new LogicConstraintSpecBuilder(termFactory)
                .addOrdinaryLiteral("P", "x", "y", "z")
                .build();

        assertThatThrownBy(() -> new LogicSchemaBuilder()
                .addLogicConstraint("@1", logicConstraintSpec1)
                .addLogicConstraint("@2", logicConstraintSpec2)).isInstanceOf(ArityMismatch.class);
    }

}