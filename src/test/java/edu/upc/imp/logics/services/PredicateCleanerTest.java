package edu.upc.imp.logics.services;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.utils.LogicSchemaMother;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.PredicateSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import edu.upc.imp.logics.services.normalizer.PredicateCleaner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PredicateCleanerTest {

    @Test
    public void should_throwException_when_logicSchemaIsNull() {
        PredicateCleaner predicateCleaner = new PredicateCleaner();

        assertThatThrownBy(() -> predicateCleaner.clean(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("LogicSchema cannot be null");
    }

    @Test
    public void should_cleanLogicSchema_when_ItContainsPredicatesNotUsed() {
        PredicateSpec unusedPredicateSpec = new PredicateSpec("Z", 2);

        LogicConstraintWithIDSpec logicConstraintSpec = new LogicConstraintWithIDSpecBuilder()
                .addConstraintId("1")
                .addOrdinaryLiteral("P", "x", "y")
                .addOrdinaryLiteral("Q", "x", "y")
                .build();

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addLogicConstraint(logicConstraintSpec)
                .addPredicate(unusedPredicateSpec)
                .build();

        PredicateCleaner predicateCleaner = new PredicateCleaner();
        LogicSchema logicSchemaResult = predicateCleaner.clean(logicSchema);

        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyThesePredicateNames("P", "Q");
    }

    @Test
    public void should_cleanLogicSchema_when_ItContainsDerivationRulesNotUsed() {
        String schemaString = """
                @1 :- A(x), B(x), C(x)
                D(x) :- A(x), B(x), C(x)
                """;
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        PredicateCleaner predicateCleaner = new PredicateCleaner();
        LogicSchema logicSchemaResult = predicateCleaner.clean(logicSchema);

        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyThesePredicateNames("A", "B", "C");
        assertThat(logicSchemaResult.getAllDerivationRules()).isEmpty();
    }

    @Test
    public void should_cleanLogicSchema_when_ItContainsNestedDerivationRules() {
        String schemaString = """
                @1 :- A(x), B(x), C(x)
                C(x) :- D(x)
                D(x) :- E(x), F(x)
                Z(x) :- X(x), Y(x)
                """;
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        PredicateCleaner predicateCleaner = new PredicateCleaner();
        LogicSchema logicSchemaResult = predicateCleaner.clean(logicSchema);

        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyThesePredicateNames("A", "B", "C", "D", "E", "F");
        assertThat(logicSchemaResult.getAllDerivationRules()).hasSize(2);
    }

}
