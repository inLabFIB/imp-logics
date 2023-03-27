package edu.upc.imp.logics.services;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.utils.LogicSchemaMother;
import edu.upc.imp.logics.services.creation.LogicSchemaBuilder;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.PredicateSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.LogicConstraintWithIDSpecBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogicConstraintCleanerTest {

    @Test
    public void should_cleanPredicatesNotUsed() {
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

        LogicConstraintCleaner logicConstraintCleaner = new LogicConstraintCleaner();
        LogicSchema logicSchemaResult = logicConstraintCleaner.clean(logicSchema);

        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyThesePredicateNames("P", "Q");
    }

    @Test
    public void should_cleanDerivationRulesNotUsed() {
        String schemaString = """
                @1 :- A(x), B(x), C(x)
                D(x) :- A(x), B(x), C(x)
                """;
        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        LogicConstraintCleaner logicConstraintCleaner = new LogicConstraintCleaner();
        LogicSchema logicSchemaResult = logicConstraintCleaner.clean(logicSchema);

        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        LogicSchemaAssert.assertThat(logicSchemaResult).containsExactlyThesePredicateNames("A", "B", "C");
        assertThat(logicSchemaResult.getAllDerivationRules()).isEmpty();
    }
}
