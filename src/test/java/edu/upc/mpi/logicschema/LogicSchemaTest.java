package edu.upc.mpi.logicschema;

import edu.upc.mpi.utils.LogicSchemaTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LogicSchemaTest extends LogicSchemaTestHelper {

    @Test
    public void testDeleteUnusedPredicates() {
        // Arrange
        LogicSchema logicSchema = this.createLogicSchemaWithConstraints(":- P(x), not(R(x))");
        List<Predicate> usedPredicates = logicSchema.getAllPredicates();
        Predicate Pextra = new PredicateImpl("Pextra",1);
        logicSchema.addPredicate(Pextra);

        // Action
        logicSchema.deleteUnusedPredicates();

        // Assert
        assertThat(logicSchema.getAllPredicates()).hasSameElementsAs(usedPredicates);

    }

    @Test
    public void testDeleteUnusedPredicatesWithDerivationRules() {
        // Arrange
        LogicSchema logicSchema = this.createLogicSchemaWithConstraints(":- P(x), not(R(x)), not(aux(x))\n" +
                "aux(y):- Q(y), not(T(y)), not(aux2(y))\n" +
                "aux2(z):- S(z)");
        List<Predicate> usedPredicates = logicSchema.getAllPredicates();
        Predicate Pextra = new PredicateImpl("Pextra",1);
        logicSchema.addPredicate(Pextra);

        // Action
        logicSchema.deleteUnusedPredicates();

        // Assert
        assertThat(logicSchema.getAllPredicates()).hasSameElementsAs(usedPredicates);
    }
}
