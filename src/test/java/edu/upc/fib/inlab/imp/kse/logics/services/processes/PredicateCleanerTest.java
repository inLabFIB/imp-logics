package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.PredicateSpec;
import org.junit.jupiter.api.Test;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert.assertThat;
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

        LogicSchema logicSchema = LogicSchemaBuilder.defaultLogicSchemaWithIDsBuilder()
                .addPredicate(unusedPredicateSpec)
                .build();

        PredicateCleaner predicateCleaner = new PredicateCleaner();
        LogicSchema logicSchemaResult = predicateCleaner.clean(logicSchema);

        assertThat(logicSchemaResult).isEmpty();
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

        assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        assertThat(logicSchemaResult).containsExactlyThesePredicateNames("A", "B", "C");
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

        assertThat(logicSchemaResult).containsExactlyTheseConstraintIDs("1");
        assertThat(logicSchemaResult).containsExactlyThesePredicateNames("A", "B", "C", "D", "E", "F");
        assertThat(logicSchemaResult.getAllDerivationRules()).hasSize(2);
    }

    @Test
    public void should_returnOriginalConstraintID_when_SchemaHasSeveralConstraints() {
        LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                @1 :- P(x)
                @2 :- Q(x)
                @3 :- R(x)
                """);

        PredicateCleaner predicateCleaner = new PredicateCleaner();
        SchemaTransformation schemaTransformation = predicateCleaner.executeTransformation(originalSchema);

        assertThat(schemaTransformation)
                .constraintIDComesFrom("1", "1")
                .constraintIDComesFrom("2", "2")
                .constraintIDComesFrom("3", "3");
    }
}
