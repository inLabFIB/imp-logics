package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.assertions.DerivationRuleAssert;
import edu.upc.imp.logics.schema.BasePredicate;
import edu.upc.imp.logics.schema.DerivationRule;
import edu.upc.imp.logics.schema.LogicSchema;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LogicSchemaBuilderTest {

    @Test
    public void should_addBasePredicates_whenAddingSeveralPredicates_withNoDerivationRules() {
        LogicSchema logicSchema = new LogicSchemaBuilder()
                .addPredicate("arity2", 2)
                .addPredicate("arity3", 3)
                .addPredicate("arity4", 4)
                .build();

        assertThat(logicSchema.getAllPredicates()).hasSize(3);
        assertThat(logicSchema.getAllPredicates())
                .allSatisfy(p -> assertThat(p).isInstanceOf(BasePredicate.class));
        assertThat(logicSchema.getPredicateByName("arity2").getArity().getNumber()).isEqualTo(2);
        assertThat(logicSchema.getPredicateByName("arity3").getArity().getNumber()).isEqualTo(3);
        assertThat(logicSchema.getPredicateByName("arity4").getArity().getNumber()).isEqualTo(4);
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

    // P(x) :- Q(x), Q(x)

    // P(x) :-
    // P(x) :-

    // P(x) :- Q(x)
    // Q(x) :- R(x)





}